from prompt.defects4j import *
from tester.junit import *
from tester.leetcode import *
from utils.patch_apply import *
import pandas as pd
from tqdm import tqdm
import time
import argparse
import yaml
import random

with open('config.yaml', 'r') as f:
    config = yaml.load(f, Loader=yaml.FullLoader)
    test_config = config['test_config']

meta = {
    'defects4j': {
        'code': 'data/defects4j_code.csv',
        'artifact': 'data/defects4j_artifact.csv',
        'skip': ['Lang_18', 'Lang_25', 'Lang_48', 'JacksonDatabind_65', 'JacksonDatabind_89'] # skip bugs which are deprecated in latest Defects4J version
    },
    'debugbench': {
        'all': 'data/debugbench_all.json'
    }
}


def junit_test(pred_path, eval_path):
    df_pred = pd.read_csv(pred_path, sep=',', encoding='utf-8')
    df_data = pd.read_csv(meta['defects4j']['code'], sep=',', encoding='utf-8')
    if os.path.exists(eval_path):
        df_eval = pd.read_csv(eval_path, sep=',', encoding='utf-8')
    else:
        df_eval = pd.DataFrame(columns=['ID', 'slug', 'reward', 'submission_result'])

    for i, row in tqdm(df_pred.iterrows(), total=len(df_pred)):
        bug_slug = row['slug']
        sample = df_data[df_data['slug'] == bug_slug].iloc[0]
        if bug_slug in meta['defects4j']['skip']:
            reward, submission_result = False, 'Bug deprecated'
        elif row['fix'] == 'Match failed':
            reward, submission_result = False, 'Match failed'
        else:
            bug_id = row['slug']
            class_path = sample['class_path']
            fixed_method = row['fix']
            original_method = sample['buggy_code'].strip()
            original_method_len = len(original_method.split('\n'))
            fixed_method = fixed_method.replace('@Override', '').strip()
            function_head = fixed_method.split('{')[0].split(')')[0] + ')'
            function_head = function_head.strip()
            class_replace_index = extract_method_start_end_index(class_path, function_head, original_method_len)
            if class_replace_index is None:
                reward, submission_result = False, 'Locate failed'
            else:
                replace_file(class_path, class_replace_index, fixed_method)
                reward, submission_result = run_JUnit(bug_id, test_config)
                restore_file(class_path, bug_id)
        df_eval.loc[i] = {'ID': i, 'slug': sample['slug'], 'reward': reward, 'submission_result': submission_result}
        df_eval.to_csv(eval_path, sep=',', encoding='utf-8', index=False)


def junit_evaluate(eval_path):
    df_eval = pd.read_csv(eval_path, sep=',', encoding='utf-8')
    name_list = ['Chart', 'Lang', 'Math', 'Mockito', 'Time']
    total_list = []
    pass_list = []
    sp_list = []
    for i, row in df_eval.iterrows():
        total_list.append(row['slug'])
        if row['reward'] == True and row['slug'] not in pass_list:
            pass_list.append(row['slug'])
            if row['slug'] not in sp_list and row['slug'].split('_')[0] in name_list:
                sp_list.append(row['slug'])
            if row['slug'] not in sp_list and row['slug'].split('_')[0] == 'Closure' and int(row['slug'].split('_')[1]) <= 133:
                sp_list.append(row['slug'])
    print(f"Pass: {len(pass_list)}")
    print(f"Pass-1.2: {len(sp_list)}")
    # print(sp_list)


def leetcode_test(pred_path, eval_path):
    df_pred = pd.read_csv(pred_path, sep=',', encoding='utf-8')
    print(df_pred)
    row_num = 0
    if os.path.exists(eval_path):
        df_eval = pd.read_csv(eval_path, sep=',', encoding='utf-8')
        row_num = df_eval.shape[0]
    else:
        df_eval = pd.DataFrame(columns=['ID', 'slug', 'reward', 'submission_result'])
    print(f'start from {row_num}')

    tester = LeetcodeValidator(0) # single cookie

    last_correct_slug = ""
    last_correct_info = ""
    iter_test = 0
    last_run = datetime.now()
    for idx, row in tqdm(df_pred.iterrows()):

        if idx < row_num:
            continue
        if row['fix'] == 'Match failed':
            continue
        
        if row['slug'] == last_correct_slug:
            reward = True
            submission_result = last_correct_info
        else:
            ### loop cookies
            wait_time = 30 + random.uniform(-10, 10)
            while (datetime.now() - last_run).total_seconds() < wait_time:
                time.sleep(random.uniform(1, 3))
            tester = LeetcodeValidator(iter_test, headers={
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                'Accept-Language': 'en-US,en;q=0.9',
                'Referer': 'https://leetcode.com/'
            })
            last_run = datetime.now()

            iter_test += 1
            if reward:
                last_correct_slug = row['slug']
                last_correct_info = submission_result
        df_eval.loc[idx] = {'ID': row['ID'], 'slug': row['slug'], 'reward': reward, 'submission_result': submission_result}
        df_eval.to_csv(eval_path, sep=',', encoding='utf-8', index=False)

def leetcode_test_with_test_msg(pred_path, eval_path):
    df_pred = pd.read_csv(pred_path, sep=',', encoding='utf-8')
    print(df_pred)
    row_num = 0

    if os.path.exists(eval_path):
        df_eval = pd.read_csv(eval_path, sep=',', encoding='utf-8')
        row_num = df_eval.shape[0]
    else:
        df_eval = pd.DataFrame(columns=['ID', 'slug', 'reward', 'status', 'submission_result'])
    print(f'start from {row_num}')

    tester = LeetcodeValidator(0) # single cookie

    last_correct_slug = ""
    last_correct_info = ""
    iter_test = 0
    last_run = datetime.now()
    for idx, row in tqdm(df_pred.iterrows()):

        if idx < row_num:
            continue
        if row['fix'] == 'Match failed':
            continue
        
        if row['slug'] == last_correct_slug:
            reward = True
            submission_result = last_correct_info
        else:

            ### loop cookies
            wait_time = 30 + random.uniform(-10, 10)
            while (datetime.now() - last_run).total_seconds() < wait_time:
                time.sleep(random.uniform(1, 3))
            tester = LeetcodeValidator(iter_test, headers={
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                'Accept-Language': 'en-US,en;q=0.9',
                'Referer': 'https://leetcode.com/'
            })
            last_run = datetime.now()

            reward, status, submission_result = tester.test_with_test_msg(row['fix'], row['slug'], row['lang'])
            iter_test += 1
            if reward:
                last_correct_slug = row['slug']
                last_correct_info = submission_result
        df_eval.loc[idx] = {'ID': row['ID'], 'slug': row['slug'], 'reward': reward, 'status': status, 'submission_result': submission_result}
        df_eval.to_csv(eval_path, sep=',', encoding='utf-8', index=False)

