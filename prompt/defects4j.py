BASIC_PROMPT = """### Buggy function comment:
{BUGGY_COMMENT}

### Error message from JUnit test:
{ERROR_MESSAGE}

### Failed JUnit test:
```java
{FAILED_TEST}
```

### Buggy function (You need to generate a fixed version of this program):
```java
{BUGGY_CODE}
```"""

PROBLEM_TG = """### Buggy function comment:
{BUGGY_COMMENT}

### Error message from JUnit test:
{ERROR_MESSAGE}

### Failed JUnit test:
```java
{FAILED_TEST}
```
"""

BUGCODE_TG = """### Buggy function:
```java
{BUGGY_CODE}
```
"""

