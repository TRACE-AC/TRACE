import re

def extract_code(s: str) -> str:
    pattern = r"```.*?\n(.*?)```"
    codeblocks = re.findall(pattern, s, flags=re.DOTALL)
    if len(codeblocks) == 0:
        return 'Match failed'
    return codeblocks
