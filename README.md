# TRACE
In this codebase we provide instructions for reproducing our results from the paper. We hope that this work can be useful for future research on automatic program repair.


## Usage

### Environment Setup 
Download and install dependencies:
```
pip install openai==1.23.6
pip install tenacity==8.2.3
pip install pandas==1.5.3
pip install datasets==2.14.6
pip install tqdm==4.66.2
cd textgrad
pip install -e .
cd ..
```

### Running Defects4J

1. Install Defects4J from [GitHub](https://github.com/rjust/defects4j).
2. Run `python checkout.py` to check out the buggy repositories.
3. Create an OpenAI account and obtain your API key from [OpenAI](https://platform.openai.com/api-keys). Set your OpenAI API key as an environment variable named `OPENAI_API_KEY`.
4. Configure your parameters in the `config.yaml` file. Here, `model` specifies your base LLM; `max_try` indicates the maximum number of repair attempts; and `time_out` sets the timeout limit for each repair attempt.
5. To perform repairs using TRACE, run:
```sh
bash run_trace_defects4j.sh
```

### Running DebugBench

1. Create a LeetCode account and log in at [LeetCode](https://leetcode.com/).
2. Obtain your cookies. The method for retrieving cookies varies by browser. For example, in Firefox:
   - Press `Ctrl + Shift + I` (Windows/Linux) or `Cmd + Option + I` (Mac) to open the web developer tools.
   - Go to the `Network` tab in the developer tools and refresh the LeetCode webpage.
   - Search for requests with the keyword `graphql` in the developer tools, select any such request, and switch to the `Cookie` field.
   - Copy the values of `LEETCODE_SESSION` and `csrftoken`.
3. Configure your `LEETCODE_SESSION` and `csrftoken` in the `config.yaml` file. To avoid the impact of LeetCode's rate limiting on the experiments, it is recommended to use multiple accounts.
4. To perform repairs using TRACE, run:
```sh
bash run_trace_debugbench.sh
```
Then, run `validate.py` to output LeetCode test results.


## Results

The detailed correct patches for Defects4J and DebugBench can be found in `results/defects4j` and `results/debugbench`, respectively.


##  Online Appendix
### 1. Java Common Error Types and Corresponding Repair Templates

| Common Error                   | Description                                                                                                                                | Corresponding Fix Template          |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------- |
| Unsafe Type Cast               | An object is cast or stored into a target of a more specific component type without checking their type compatibility.                     | Insert Cast Checker                 |
| Out‑of‑Bounds Access           | Accessing an array, List, or any index-based collection with an invalid index.                                                             | Insert Range Checker                |
| Null Dereference               | Using an object that might be null without any null check, potentially causing NullPointerException.                                       | Insert Null Pointer Checker         |
| Missing Statement              | Missing a specific type of statement (method invocation, return statement, if statement, or try-catch block), leading to incomplete logic. | Insert Missed Statement             |
| Faulty Conditional             | A logical error in the boolean condition; needs adding, removing, or modifying sub-conditions.                                             | Mutate Conditional Expression       |
| Wrong Data Type                | Using an incorrect data type in variable declarations or cast expressions, causing type mismatch or runtime errors.                        | Mutate Data Type                    |
| Incorrect Literal              | An incorrect literal (boolean, numeric, or string) used in the code, needs replacing with a correct value or compatible expression.        | Mutate Literal Expression           |
| Invalid Method Call            | A method name or its parameters are incorrect—possibly calling a non-existent method or mismatched parameter types.                        | Mutate Method Invocation Expression |
| Incorrect Clone Implementation | clone() is overridden without calling super.clone() or without copying all mutable fields, producing an incomplete copy.                   | Mutate Class Instance Creation      |
| Unintended Integer Division    | Integer division is used where a floating-point result is needed, causing precision loss.                                                  | Mutate Integer Division Operation   |
| Operator Misuse                | Incorrect usage of relational, arithmetic, instanceof operators, or missing parentheses for correct precedence.                            | Mutate Operators                    |
| Wrong Return Value             | The return statement includes an incorrect or incompatible expression, affecting method correctness.                                       | Mutate Return Statement             |
| Wrong Variable Reference       | An incorrect or incompatible variable is used, causing logical or type conflicts.                                                          | Mutate Variable                     |
| Misplaced Statement            | A statement is placed in the wrong position, disrupting the intended execution flow.                                                       | Move Statement                      |
| Redundant Statement            | A problematic or superfluous statement that should be removed to fix the error.                                                            | Remove Buggy Statement              |

### 2. Python 3 Common Error Types and Corresponding Repair Templates

| Common Error                      | Description                                                                                                                                                                          | Corresponding Fix Template          |
| --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------- |
| Type Misuse                       | Accessing attributes, methods, or calling conversion functions on an object without verifying its actual type, which may raise TypeError or AttributeError.                          | Insert Type Checker                 |
| Out-of-Range / Missing-Key Access | Using an index outside the bounds of a list/tuple (IndexError) or a key that is not present in a dict (KeyError).                                                                    | Insert Range/Key Checker            |
| None Dereference                  | Performing attribute access, a function call, or arithmetic on a value that may be None, which can raise AttributeError or TypeError.                                                | Insert None Checker                 |
| Missing Crucial Statement         | A critical statement (function call, return statement, if statement, or exception handling) is missing, causing incomplete logic.                                                    | Insert Missed Statement             |
| Faulty Conditional Expression     | A logical error in the Boolean condition; sub-conditions may need adding, removing, or altering.                                                                                     | Mutate Conditional Expression       |
| Incorrect Data Type               | Using an incorrect built-in type or a wrong conversion function, causing TypeError or ValueError.                                                                                    | Mutate Data Type                    |
| Incorrect Literal Value           | A literal (boolean, numeric, or string) is incorrect and should be replaced with a more suitable or correct value.                                                                   | Mutate Literal Expression           |
| Incorrect Function Call           | A function call with the wrong function name or incorrect arguments, leading to an error or unintended behavior.                                                                     | Mutate Method Invocation Expression |
| Division Operator Confusion       | Confusing “/” (true division, always returns float) with “//” (floor division, returns int for two int operands and float otherwise), leading to incorrect result type or precision. | Mutate Division Operator            |
| Operator Misuse                   | Incorrect usage of arithmetic, relational, or membership operators, or missing parentheses for the required precedence.                                                              | Mutate Operators                    |
| Incorrect Return                  | Function returns a value whose type or meaning conflicts with its contract, breaking later logic.                                                                                    | Mutate Return Statement             |
| Incorrect Variable Usage          | Variable name typo, wrong scope or wrong type (NameError, shadowing, type mismatch).                                                                                                 | Mutate Variable                     |
| Misplaced Statement               | A statement is placed in the wrong location, disrupting the intended control flow.                                                                                                   | Move Statement                      |
| Superfluous Statement             | A problematic or superfluous statement should be removed to resolve an error or conflict.                                                                                            | Remove Buggy Statement              |

### 3. C++ Common Error Types and Corresponding Repair Templates

| Common Error                                                | Description                                                                                                                                                   | Corresponding Fix Template          |
| ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------- |
| Unsafe Cast (reinterpret / C-style / incorrect static_cast) | Using reinterpret_cast or an inappropriate C-style/static_cast without confirming the real type can cause undefined behaviour.                                | Insert Cast Checker                 |
| Out-of-Range Access                                         | Accessing past the end of an array/pointer or a container’s operator[] causes undefined behaviour; using at() will throw std::out_of_range.                   | Insert Range Checker                |
| Null/Empty Pointer Dereference                              | Dereferencing a null pointer leads to undefined behaviour; calling std::optional::value() without first checking has_value() throws std::bad_optional_access. | Insert Null Pointer Checker         |
| Missing Crucial Statement                                   | Missing return/break/delete/try-catch etc. causes logic errors or resource leaks.                                                                             | Insert Missed Statement             |
| Faulty Conditional                                          | A logical error in the boolean condition requiring changes.                                                                                                   | Mutate Conditional Expression       |
| Incorrect Data Type / Qualifier                             | Choosing wrong type, signed/unsigned mismatch, ignoring const/volatile causes compile or logic errors                                                         | Mutate Data Type                    |
| Incorrect Literal Value                                     | An incorrect literal (boolean, numeric, or string) used in the code, requiring replacement with a correct value.                                              | Mutate Literal Expression           |
| Wrong Function Call / Overload Misuse                       | Function name wrong, arguments mismatch with parameters or overload, or missing std:: scope.                                                                  | Mutate Method Invocation Expression |
| Integer vs Floating-Point Division                          | Using integer division where floating-point operations are required, causing precision loss.                                                                  | Mutate Integer Division Operation   |
| Operator Misuse/Precedence Error                            | Incorrect usage of relational, arithmetic, or pointer operators, or missing parentheses affecting operator precedence.                                        | Mutate Operators                    |
| Incorrect Return Statement                                  | The return statement has an incorrect expression or type that does not match the function's return type or logic.                                             | Mutate Return Statement             |
| Wrong Variable Usage                                        | Using the wrong variable (type or name), causing logic or type conflicts.                                                                                     | Mutate Variable                     |
| Misplaced Statement                                         | A statement is placed in an incorrect position, disrupting the intended flow.                                                                                 | Move Statement                      |
| Redundant/Buggy Statement                                   | A statement that causes errors should be removed.                                                                                                             | Remove Buggy Statement              |
