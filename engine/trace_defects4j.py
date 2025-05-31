import argparse
import math
import os
import yaml

import pandas as pd
import textgrad as tg
from tqdm import tqdm

from prompt.repair_template import *
from prompt.defects4j import *
from tester.junit import *
from utils.patch_apply import *

with open('config.yaml') as f:
    config = yaml.safe_load(f)
    TEST_CONFIG = config['test_config']



def repair(args):
    if not os.path.exists(args.patch_path):
        os.makedirs(args.patch_path)
    data = pd.read_csv(args.data_path, sep=',', encoding='utf-8', engine='python',on_bad_lines='warn')
    msg = pd.read_csv(args.msg_path, sep=',', encoding='utf-8', engine='python')
    id_count = data['slug'].value_counts()
    total_unique = id_count[id_count == 1].sum()
    data = data.groupby('slug').filter(lambda x: len(x) == 1)

    for i, sample in tqdm(data.iterrows(), total=len(data)):
        print(f"========== RUNNING: {sample['slug']} ==========")

        stop_patch_attempt = False

        for k in range(args.max_try):
            query = PROBLEM_TG
            bugcode = BUGCODE_TG
            query = query.replace("{BUGGY_COMMENT}", sample['comment'].strip() if str(sample['comment']) not in {'', 'nan'} else "This function has no comment.")
            query = query.replace("{ERROR_MESSAGE}", msg[msg['slug'] == sample['slug']]['exception_info'].values[0] if len(msg[msg['slug'] == sample['slug']]['exception_info'].values) > 0 else "This function has no exception info.")
            query = query.replace("{FAILED_TEST}", msg[msg['slug'] == sample['slug']]['test_method'].values[0] if len(msg[msg['slug'] == sample['slug']]['test_method'].values) > 0 else "This function has no failed test.")
            bugcode = bugcode.replace("{BUGGY_CODE}", sample['buggy_code'].strip())
            history_report = BASIC_PROMPT
            history_report = history_report.replace("{BUGGY_COMMENT}", sample['comment'].strip() if str(sample['comment']) not in {'', 'nan'} else "This function has no comment.")
            history_report = history_report.replace("{ERROR_MESSAGE}", msg[msg['slug'] == sample['slug']]['exception_info'].values[0] if len(msg[msg['slug'] == sample['slug']]['exception_info'].values) > 0 else "This function has no exception info.")
            history_report = history_report.replace("{FAILED_TEST}", msg[msg['slug'] == sample['slug']]['test_method'].values[0] if len(msg[msg['slug'] == sample['slug']]['test_method'].values) > 0 else "This function has no failed test.")
            history_report = history_report.replace("{BUGGY_CODE}", sample['buggy_code'].strip())
            code = tg.Variable(value=bugcode,
                               requires_grad=True,
                               role_description="buggy code to fix")
            problem = tg.Variable(query,
                                  requires_grad=False,
                                  role_description="the bug report of the buggy code")
            constraints = ["Strictly format your code output as a code block starting with a line containing exactly three backticks (```), followed by your code on the next line(s) and ending with a line containing exactly three backticks (```).",
            "Do not change anything before the function header or modify the method signature. Keep the original function header and only modify the code inside the function body.",
            "While ensuring the modified code remains correct, make as few changes as possible and preserve the original code's structure and functionality. You can add simple comments after modified lines to explain the reason for the change."
            ]
            optimizer = tg.TGD(parameters=[code], constraints=constraints)
            system_prompt = "You are a programming expert that evaluates code snippets. You do not propose new code snippets, only evaluate existing solutions critically and provide very concise feedback in plain text."
            system_prompt = tg.Variable(system_prompt, requires_grad=False, role_description="system prompt to the loss function")

            instruction_template = """Analyze the given buggy function and its bug report to determine the root cause. Use the following checklist of common error types as a reference. There may be additional issues not captured here, so use it as a flexible guide. Here are the descriptions of common errors:
            {common_errors}
            """
            instruction = instruction_template.format(common_errors=COMMON_ERRORS_JAVA)

            format_string = "{instruction}\nBug Report: {{problem}}\nCurrent Code: {{code}}"
            format_string = format_string.format(instruction=instruction)
            fields = {"problem": None, "code": None}
            formatted_llm_call = tg.autograd.FormattedLLMCall(engine=llm_engine,
                                                              format_string=format_string,
                                                              fields=fields,
                                                              system_prompt=system_prompt)

            def loss_fn(problem: tg.Variable, code: tg.Variable) -> tg.Variable:
                inputs = {"problem": problem, "code": code}
                return formatted_llm_call(inputs=inputs,
                                          response_role_description=f"evaluation of the {code.get_role_description()}")

            for j in range(3):
                analysis = loss_fn(problem, code)
                print(f'ROOT CAUSE:\n {analysis}\n')

                analysis.backward(lang="java")
                print(f'STRATEGY:\n {code.gradients}\n')

                optimizer.step()
                print(f'REPAIRED CODE:\n {code.value}\n')

                response = code.value
                fixed_code = extract_code(response)[0]
                reward, submission_result = test(sample['slug'], sample['class_path'], sample['buggy_code'], fixed_code, sample['start'], sample['end'])
                optimizer.zero_grad()
                
                if reward:
                    patch_file = os.path.join(args.patch_path, sample['slug'] + ".java")
                    os.makedirs(args.patch_path, exist_ok=True)
                    with open(patch_file, "w", encoding="utf-8") as f:
                        f.write(fixed_code)
                    stop_patch_attempt = True
                    break

                tracelog_mesg = f"### The result of the last repair attempt was:\n{fixed_code}\nThis fixed version is still not correct.\n{submission_result}\nPlease fix it again."
                history_report += f"\n{tracelog_mesg}"
                problem = tg.Variable(history_report,
                                        requires_grad=False,
                                        role_description="the bug report of the buggy code")
                
            if stop_patch_attempt:
                break


def test(bug_id, class_path, original_method, fixed_method, start_line=None, end_line=None):
    if fixed_method == 'Match failed':
        return False, 'Match failed'
    original_method = original_method.replace('@Override', '').strip()
    original_method_len = len(original_method.split('\n'))
    fixed_method = fixed_method.replace('@Override', '').strip()
    
    if (
        start_line is not None and end_line is not None and
        not (isinstance(start_line, float) and math.isnan(start_line)) and
        not (isinstance(end_line, float) and math.isnan(end_line))
    ):
        start_line = int(start_line) - 1
        class_replace_index = [start_line, start_line + original_method_len]
    else:
        function_head = fixed_method.split('{')[0].split(')')[0] + ')'
        function_head = function_head.strip()
    
        class_replace_index = extract_method_start_end_index(class_path, function_head, original_method_len)
        if class_replace_index is None:
            return False, 'Locate failed'
    
    replace_file(class_path, class_replace_index, fixed_method)
    reward, submission_result = run_JUnit_with_test_msg(bug_id, TEST_CONFIG)
    restore_file(class_path, bug_id)
    return reward, submission_result



if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--model', default="gpt-4o", type=str)
    parser.add_argument('--data_path', default="data/defects4j_code.csv", type=str) 
    parser.add_argument('--msg_path', default="data/defects4j_artifact.csv", type=str)
    parser.add_argument('--patch_path', default="results/defects4j/patch", type=str) 
    parser.add_argument('--max_try', default=3, type=int)
    args = parser.parse_args()

    llm_engine = tg.get_engine(args.model)
    tg.set_backward_engine(llm_engine)
    repair(args)

