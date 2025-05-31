#!/usr/bin/env python
# -*- coding: utf-8 -*-

import pandas as pd
import os
import argparse

def main():
    parser = argparse.ArgumentParser(
        description="summarize results and count language information"
    )
    parser.add_argument(
        "--input", 
        type=str, 
        default="results/debugbench/results_summary.csv",
        help="Path to the evaluation results"
    )
    args = parser.parse_args()

    input_path = args.input
    df = pd.read_csv(input_path, sep=',', encoding='utf-8')
    selected_rows = []
    for _, group in df.groupby(['ID', 'slug'], sort=False):
        true_rows = group[group['reward'] == True]
        if not true_rows.empty:
            if true_rows.shape[0] > 1:
                print(f"Wrong format")
            selected_rows.append(true_rows.iloc[0])
    
    correct_df = pd.DataFrame(selected_rows)
    directory, filename = os.path.split(input_path)
    new_filename = "correct_" + filename
    new_path = os.path.join(directory, new_filename)
    correct_df.to_csv(new_path, index=False, sep=',', encoding='utf-8')
    print(f"New file generated: {new_path}")

    total_count = correct_df.shape[0]
    count_cpp = correct_df['submission_result'].astype(str).str.contains("'lang': 'cpp'").sum()
    count_java = correct_df['submission_result'].astype(str).str.contains("'lang': 'java'").sum()
    count_python = correct_df['submission_result'].astype(str).str.contains("'lang': 'python3'").sum()

    print(f"Total number of rows in new file: {total_count}")
    print(f"Number of rows containing 'lang': 'cpp': {count_cpp}")
    print(f"Number of rows containing 'lang': 'java': {count_java}")
    print(f"Number of rows containing 'lang': 'python3': {count_python}")

if __name__ == '__main__':
    main()

