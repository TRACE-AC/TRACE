import argparse
import csv
import json
import os
import random
import sys
import time
from datetime import datetime

csv.field_size_limit(sys.maxsize)

import pandas as pd
import textgrad as tg
from tqdm import tqdm

from prompt.repair_template import *
from prompt.debugbench import *
from tester.leetcode import LeetcodeValidator
from utils.patch_apply import *



def repair(args):
    if not os.path.exists(args.save_path):
        os.makedirs(args.save_path)
    with open(args.data_path, 'r') as json_file:
        data = json.load(json_file)
    if os.path.exists(args.result_path):
        df_results = pd.read_csv(args.result_path, sep=',', encoding='utf-8', engine='python')
        if not df_results.empty:
            last_processed_id = df_results['ID'].max()
        else:
            last_processed_id = -1
    else:
        df_results = pd.DataFrame(columns=['ID', 'lang', 'slug', 'bug', 'diff', 'fix'])
        last_processed_id = -1
    if os.path.exists(args.eval_path):
        df_eval = pd.read_csv(args.eval_path, sep=',', encoding='utf-8', engine='python')
        global_index_eval = df_eval.shape[0]
    else:
        df_eval = pd.DataFrame(columns=['ID', 'slug', 'reward', 'status', 'submission_result'])
        global_index_eval = 0
        
    last_run = datetime.now()
    
    iter_test = 0
    
    for i in tqdm(range(len(data))):
        sample = data[i]
        sample_id = i
        if sample_id <= last_processed_id:
            continue    
        if sample['category'] != "logic error":
            continue
        print(f"========== RUNNING: {sample_id} - {sample['slug']} ==========")

        stop_patch_attempt = False
        for k in range(args.max_try):
            query = PROBLEM_TG
            bugcode = BUGCODE_TG
            query = query.replace("{LANGUAGE}", sample['language'].strip())
            query = query.replace("{REQUIREMENT}", sample['question'].strip())
            query = query.replace("{CONSTRAINT}", sample['constraints'].strip())
            query = query.replace("{EXAMPLE}", '\n'.join(sample['examples']).strip())
            bugcode = bugcode.replace("{LANGUAGE}", sample['language'].strip())
            bugcode = bugcode.replace("{BUGGY_CODE}", sample['buggy_code'].strip())
            history_report = BASIC_PROMPT
            history_report = history_report.replace("{LANGUAGE}", sample['language'].strip())
            history_report = history_report.replace("{REQUIREMENT}", sample['question'].strip())
            history_report = history_report.replace("{CONSTRAINT}", sample['constraints'].strip())
            history_report = history_report.replace("{EXAMPLE}", '\n'.join(sample['examples']).strip())
            history_report = history_report.replace("{BUGGY_CODE}", sample['buggy_code'].strip())
            code = tg.Variable(value=bugcode,
                            requires_grad=True,
                            role_description="buggy code to fix")
            problem = tg.Variable(query,
                                requires_grad=False,
                                role_description="the coding problem")
            constrained_optimization = ["Strictly format your code output as a code block starting with a line containing exactly three backticks (```), followed by your code on the next line(s) and ending with a line containing exactly three backticks (```), with no additional text or annotations."]
            optimizer = tg.TGD(parameters=[code], constraints=constrained_optimization)
            system_prompt = "You are a programming expert that evaluates code snippets. You do not solve problems or propose new code snippets, only evaluate existing solutions critically and give very concise feedback."
            system_prompt = tg.Variable(system_prompt, requires_grad=False, role_description="system prompt to the loss function")

            lang = sample['language'].strip().lower()
            if lang == "java":
                common_errors = COMMON_ERRORS_JAVA
            elif lang == "python3":
                common_errors = COMMON_ERRORS_PYTHON3
            elif lang == "cpp":
                common_errors = COMMON_ERRORS_CPP
            else:
                raise ValueError(f"Unsupported language for repair template repository: {lang}")

            instruction_template = """Analyze the given buggy function and its bug report to determine the root cause. Use the following checklist of common error types as a reference. There may be additional issues not captured here, so use it as a flexible guide. Here are the descriptions of common errors:
            {common_errors}
            """
            instruction = instruction_template.format(common_errors=common_errors)

            format_string = "{instruction}\nExample: {{problem}}\nCurrent Code: {{code}}"
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

                analysis.backward(lang=lang)
                print(f'STRATEGY:\n {code.gradients}\n')

                optimizer.step()
                print(f'REPAIRED CODE:\n {code.value}\n')

                response = code.value
                fixed_code = extract_code(response)[0]
                df_results.loc[df_results.shape[0]] = {
                    'ID': sample_id,
                    'lang': sample['language'],
                    'slug': sample['slug'],
                    'bug': sample['buggy_code'],
                    'diff': 'None',
                    'fix': fixed_code
                }
                df_results.to_csv(args.result_path, sep=',', encoding='utf-8', index=False)
                
                wait_time = 30 + random.uniform(-10, 10)
                while (datetime.now() - last_run).total_seconds() < wait_time:
                    time.sleep(random.uniform(1, 3))
                tester = LeetcodeValidator(iter_test, headers={
                    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                    'Accept-Language': 'en-US,en;q=0.9',
                    'Referer': 'https://leetcode.com/'
                })
                last_run = datetime.now()
                test_reward, test_status, test_submission_result = tester.test_with_test_msg(fixed_code, sample['slug'], sample['language'])
                iter_test += 1

                df_eval.loc[global_index_eval] = {
                    'ID': sample_id,
                    'slug': sample['slug'],
                    'reward': test_reward,
                    'status': test_status,
                    'submission_result': test_submission_result
                }
                df_eval.to_csv(args.eval_path, sep=',', encoding='utf-8', index=False)
                global_index_eval += 1
                
                optimizer.zero_grad()

                if test_reward is False:
                    tracelog_mesg = f"### The result of the last repair attempt was:\n{fixed_code}\nThis fixed version is still not correct. Its test result is:\n{test_status}\nPlease fix it again."
                    history_report += f"\n{tracelog_mesg}"

                    problem = tg.Variable(history_report,
                                        requires_grad=False,
                                        role_description="the bug report of the buggy code")

                if test_reward is True:
                    if not os.path.exists(args.correct_patch_path):
                        os.makedirs(args.correct_patch_path)
                    correct_filename = f"{sample['slug']}_{sample_id}.txt"
                    correct_filepath = os.path.join(args.correct_patch_path, correct_filename)
                    with open(correct_filepath, 'w', encoding='utf-8') as f:
                        f.write(fixed_code)
                    
                    stop_patch_attempt = True
                    break


            if stop_patch_attempt:
                break
            

    

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--model', default="gpt-4o", type=str)
    parser.add_argument('--data_path', default="data/debugbench_all.json", type=str)
    parser.add_argument('--save_path', default="results/debugbench", type=str)
    parser.add_argument('--max_try',   default=3, type=int)
    args = parser.parse_args()

    llm_engine = tg.get_engine(args.model)
    tg.set_backward_engine(llm_engine)

    save_dir = args.save_path.rstrip("/")
    args.result_path = os.path.join(save_dir, f"predictions.csv")
    args.eval_path = os.path.join(save_dir, f"evaluations.csv")
    args.correct_patch_path = os.path.join(save_dir, "correct_patch")

    repair(args)

