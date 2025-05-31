#!/usr/bin/env python3
import csv
import difflib


with open(input_csv, newline='', encoding='utf-8') as csvfile_in:
    reader = csv.DictReader(csvfile_in)
    fieldnames = reader.fieldnames + ['diff'] if 'diff' not in reader.fieldnames else reader.fieldnames

    rows = []
    for row in reader:
        bug_content = row.get('bug', '')
        fix_content = row.get('fix', '')
        bug_lines = bug_content.splitlines(keepends=True)
        fix_lines = fix_content.splitlines(keepends=True)
        diff_result = difflib.unified_diff(
            bug_lines, fix_lines,
            fromfile='bug', tofile='fix', lineterm=''
        )
        diff_text = '\n'.join(diff_result)
        row['diff'] = diff_text
        rows.append(row)

with open(output_csv, 'w', newline='', encoding='utf-8') as csvfile_out:
    writer = csv.DictWriter(csvfile_out, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)

