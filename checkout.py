"""
DATA PROCESS SCRIPTS
FOR DEFECTS4J V2.0
"""
import os
import shutil

def get_repos(root_dir, proj_list, id_list):
    repos_dir = os.path.join(root_dir, 'defects4j')
    os.makedirs(repos_dir, exist_ok=True)
    
    os.system('git config --global --add safe.directory "*"')
    
    for i in range(len(proj_list)):
        project = proj_list[i]
        for j in id_list[i]:
            unique_id = f"{project}_{j}"
            target_dir = os.path.join(repos_dir, f"{unique_id}_buggy")
            
            try:
                if os.path.exists(target_dir):
                    if os.listdir(target_dir):
                        print(f"Skip existing: {unique_id}")
                        continue
                    else:
                        print(f"Reinstalling empty directory: {unique_id}")
                        shutil.rmtree(target_dir)
                
                print(f"Processing: {unique_id}")
                
                cmd = f'defects4j checkout -p {project} -v {j}b -w {target_dir}'
                exit_code = os.system(cmd)
                
                if exit_code != 0:
                    print(f"Checkout failed for {unique_id}, exit code: {exit_code}")
                    if os.path.exists(target_dir):
                        shutil.rmtree(target_dir)
                
            except Exception as e:
                print(f"Error processing {unique_id}: {str(e)}")
                if os.path.exists(target_dir):
                    shutil.rmtree(target_dir)

root_dir = os.getcwd()
proj_list = [
    'Chart', 'Math', 'Lang', 'Cli', 'Closure', 'Codec', 'Mockito',
    'Jsoup', 'JacksonDatabind', 'JacksonCore', 'Compress', 'Collections',
    'Time', 'JacksonXml', 'Gson', 'Csv', 'JxPath'
]
id_range = [
    '1-25', '1-105', '1-64', '2-40', '1-170', '2-18', '1-37',
    '2-93', '2-112', '2-26', '2-47', '26-28', '1-27', '2-6',
    '2-18', '2-16', '2-22'
]

id_list = []
for range_str in id_range:
    start, end = map(int, range_str.split('-'))
    id_list.append(list(range(start, end + 1)))

get_repos(root_dir, proj_list, id_list)
    
