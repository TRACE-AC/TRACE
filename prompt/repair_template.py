COMMON_ERRORS_JAVA = """
1. Insert Cast Checker: An object is cast or stored into a target of a more specific component type without checking their type compatibility.
2. Insert Range Checker: Accessing an array, List, or any index-based collection with an invalid index.
3. Insert Null Pointer Checker: Using an object that might be null without any null check, potentially causing NullPointerException.
4. Insert Missed Statement: Missing a specific type of statement (method invocation, return statement, if statement, or try-catch block), leading to incomplete logic.
5. Mutate Conditional Expression: A logical error in the boolean condition; needs adding, removing, or modifying sub-conditions.
6. Mutate Data Type: Using an incorrect data type in variable declarations or cast expressions, causing type mismatch or runtime errors.
7. Mutate Literal Expression: An incorrect literal (boolean, numeric, or string) used in the code, needs replacing with a correct value or compatible expression.
8. Mutate Method Invocation Expression: A method name or its parameters are incorrect—possibly calling a non-existent method or mismatched parameter types.
9. Mutate Class Instance Creation: clone() is overridden without calling super.clone() or without copying all mutable fields, producing an incomplete copy.
10. Mutate Integer Division Operation: Integer division is used where a floating-point result is needed, causing precision loss.
11. Mutate Operators: Incorrect usage of relational, arithmetic, instanceof operators, or missing parentheses for correct precedence.
12. Mutate Return Statement: The return statement includes an incorrect or incompatible expression, affecting method correctness.
13. Mutate Variable: An incorrect or incompatible variable is used, causing logical or type conflicts.
14. Move Statement: A statement is placed in the wrong position, disrupting the intended execution flow.
15. Remove Buggy Statement: A problematic or superfluous statement that should be removed to fix the error.
"""

REPAIR_TEMPLATE_JAVA = """
1. Insert Cast Checker: 
- When to use: Whenever code performs a potentially unsafe cast or writes elements from one array/collection into another whose component or element type may be different.
- How to use: Insert a guard that verifies type compatibility before the cast/store; typical checks include `instanceof`, `Class.isAssignableFrom`, or comparing `getComponentType()` results.
- Example:
```diff
+ if (exp instanceof T) {
    var = (T) exp;
    // subsequent statements
+ }
```

2. Insert Range Checker: 
- When to use: When indexing into an array or collection (e.g., `array[index]` or `collection.get(index)`) without verifying the index or key is valid.
- How to use: Add a boundary check, e.g., `if (index < array.length)` or `if (index < collection.size())`.
- Example:
```diff
+ if (index < array.length) {
    array[index] = ...;
+ }
```

3. Insert Null Pointer Checker: 
- When to use: When a reference that could be null (field, parameter, return value, collection element, etc.) is dereferenced.
- How to use: Insert a null check such as `Objects.requireNonNull(exp, "exp must not be null")` or `if (exp == null)` (returning a default value, throwing an exception, etc). Prefer eliminating null at the source (Optional, @NonNull).
- Example:
```diff
+ if (exp == null) {
+    return false; // e.g., if the return type is boolean
+ }
doSomething(exp);
```

4. Insert Missed Statement: 
- When to use: When a crucial statement (method call, return, if, or try-catch) is missing.
- How to use: Insert the missing statement at the correct position, such as `someMethod(arg);` or `return DEFAULT_VALUE;`.
- Example:
```diff
+ someMethod(arg);
```
or
```diff
+ return DEFAULT_VALUE;
```

5. Mutate Conditional Expression: 
- When to use: When the boolean condition needs modification (adding, removing, or changing parts) to fix logical errors.
- How to use: 
  - Replace the entire condition with a new one.
  - Remove or add a sub-condition, e.g., `condExp1 Op condExp2`.
- Example:
```diff
- if (a > 0)
+ if (a > 0 && b < 5)
```

6. Mutate Data Type: 
- When to use: If the wrong data type is specified in a variable declaration or cast expression, causing a type mismatch.
- How to use: Replace the incorrect data type (e.g., `T1`) with a proper type (`T2`).
- Example:
```diff
- (T1) exp
+ (T2) exp
```

7. Mutate Literal Expression:
- When to use: If a boolean, numeric, or string literal is incorrect and needs to be replaced with a more suitable literal or expression.
- How to use: Replace the literal with another literal value or compatible expression.
- Example:
```diff
- return 1;
+ return 0;
```

8. Mutate Method Invocation Expression: 
- When to use: If the invoked method's name or parameters are incorrect; may require changing the method name or arguments.
- How to use: 
  1. Replace the method name with another that has compatible parameters/return type.
  2. Replace one or more arguments with compatible expressions.
  3. Remove or add arguments if the method has suitable overloaded versions.
- Example:
```diff
- method1(arg1, arg2)
+ method2(arg1, arg2)
```

9. Mutate Class Instance Creation:
- When to use: In an overridden `clone()` method, using `new T()` instead of `super.clone()` disrupts proper cloning.
- How to use: Replace `new T()` with `(T) super.clone()`.
- Example:
```diff
public Object clone() {
-   return new T();
+   return (T) super.clone();
}
```

10. Mutate Integer Division Operation: 
- When to use: When integer division is used where a floating-point result is needed.
- How to use: Cast the dividend or divisor to double/float.
- Example:
```diff
- int result = a / b;
+ double result = a / (double) b;
```

11. Mutate Operators: 
- When to use: If an incorrect operator is used or operator precedence is wrong.
- How to use: Replace the operator with the correct one or fix precedence using parentheses.
- Example:
```diff
- if (a != b)
+ if (a == b)
```

12. Mutate Return Statement: 
- When to use: If the expression in the return statement is incorrect and must be replaced.
- How to use: Replace with a correct, compatible expression.
- Example:
```diff
- return doSomething();
+ return doAnotherThing();
```

13. Mutate Variable: 
- When to use: If the wrong variable is referenced, causing logic or type errors.
- How to use: Replace the incorrect variable with the correct one.
- Example:
```diff
- result = var1;
+ result = var2;
```

14. Move Statement: 
- When to use: If a statement is placed incorrectly, disrupting the execution flow.
- How to use: Move the statement to the correct location.
- Example:
```diff
- statement;
  ...
+ statement;
```

15. Remove Buggy Statement: 
- When to use: If a problematic statement needs to be removed.
- How to use: Simply delete the statement.
- Example:
```diff
- statement;
```
"""

COMMON_ERRORS_PYTHON3 = """
1. Insert Type Checker: Accessing attributes, methods, or calling conversion functions on an object without verifying its actual type, which may raise TypeError or AttributeError.
2. Insert Range/Key Checker: Using an index outside the bounds of a list/tuple (IndexError) or a key that is not present in a dict (KeyError).
3. Insert None Checker: Performing attribute access, a function call, or arithmetic on a value that may be None, which can raise AttributeError or TypeError.
4. Insert Missed Statement: A critical statement (function call, return statement, if statement, or exception handling) is missing, causing incomplete logic.
5. Mutate Conditional Expression: A logical error in the Boolean condition; sub-conditions may need adding, removing, or altering.
6. Mutate Data Type: Using an incorrect built-in type or a wrong conversion function, causing TypeError or ValueError.
7. Mutate Literal Expression: A literal (boolean, numeric, or string) is incorrect and should be replaced with a more suitable or correct value.
8. Mutate Method Invocation Expression: A function call with the wrong function name or incorrect arguments, leading to an error or unintended behavior.
9. Mutate Division Operator: Confusing “/” (true division, always returns float) with “//” (floor division, returns int for two int operands and float otherwise), leading to incorrect result type or precision.
10. Mutate Operators: Incorrect usage of arithmetic, relational, or membership operators, or missing parentheses for the required precedence.
11. Mutate Return Statement: Function returns a value whose type or meaning conflicts with its contract, breaking later logic.
12. Mutate Variable: Variable name typo, wrong scope or wrong type (NameError, shadowing, type mismatch).
13. Move Statement: A statement is placed in the wrong location, disrupting the intended control flow.
14. Remove Buggy Statement: A problematic or superfluous statement should be removed to resolve an error or conflict.
"""

REPAIR_TEMPLATE_PYTHON3 = """
1. Insert Type Checker: 
- When to use: If you need to convert or use an object as a certain type but risk TypeError/AttributeError if it is not actually that type.
- How to use: Apply an isinstance() check or confirm the object's type before the operation.
- Example:
```diff
+ if isinstance(exp, SomeClass):
+     var = exp  # Safe usage after check
+     # subsequent statements
```

2. Insert Range/Key Checker: 
- When to use: When accessing a list, tuple, or dictionary key without verifying the index/key is valid.
- How to use: Check boundaries or the existence of the key before indexing.
- Example:
```diff
+ if index < len(my_list):
+     my_list[index] = new_value
```

3. Insert None Checker: 
- When to use: When a variable might be None, leading to AttributeError when its methods or attributes are accessed.
- How to use: Insert an "if var is None" check and handle accordingly (return a default, raise an exception, etc).
- Example:
```diff
+ if param is None:
+     return 0  # e.g., if a numeric return is expected
process(param)
```

4. Insert Missed Statement: 
- When to use: If a crucial statement is missing (function call, return, if, or exception handling).
- How to use: Insert the needed statement in the appropriate logical position.
- Example:
```diff
+ some_function_call(arg)
```
or
```diff
+ return default_result
```

5. Mutate Conditional Expression: 
- When to use: When the Boolean condition is incorrect or incomplete.
- How to use: Modify parts of the condition to fix the logic.
- Example:
```diff
- if a > 0:
+ if a > 0 and b < 5:
```

6. Mutate Data Type: 
- When to use: If an incorrect type or conversion function is used.
- How to use: Replace with the proper type or function.
- Example:
```diff
- val = int(exp)
+ val = float(exp)
```

7. Mutate Literal Expression: 
- When to use: If a literal (boolean, numeric, or string) is incorrect.
- How to use: Replace with a more appropriate literal or expression.
- Example:
```diff
- return 1
+ return 0
```

8. Mutate Method Invocation Expression: 
- When to use: If the called function or its arguments are incorrect.
- How to use: Change the function name or arguments to match the intended signature.
- Example:
```diff
- process_data(val1, val2)
+ process_record(val1, val2)
```

9. Mutate Division Operator: 
- When to use: If integer division is used but a float result is expected.
- How to use: Replace // with / and ensure float conversion if needed.
- Example:
```diff
- result = a // b
+ result = a / b
```

10. Mutate Operators: 
- When to use: If the wrong operator or inappropriate parentheses usage leads to logic errors.
- How to use: Replace with the correct operator or add parentheses to fix precedence.
- Example:
```diff
- if a != b:
+ if a == b:
```

11. Mutate Return Statement: 
- When to use: If the return expression is incorrect or incompatible.
- How to use: Replace the return expression with a correct, compatible expression.
- Example:
```diff
- return compute_a()
+ return compute_b()
```

12. Mutate Variable: 
- When to use: If the code references the wrong variable or an incompatible identifier.
- How to use: Replace it with the correct variable.
- Example:
```diff
- result = var1
+ result = var2
```

13. Move Statement: 
- When to use: If a statement's order disrupts the control flow.
- How to use: Relocate the statement to the appropriate position.
- Example:
```diff
- statement
  ...
+ statement
```

14. Remove Buggy Statement: 
- When to use: If a statement causes errors, removing it resolves the problem.
- How to use: Delete the problematic statement.
- Example:
```diff
- statement
```
"""

COMMON_ERRORS_CPP = """
1. Insert Cast Checker: Using reinterpret_cast or an inappropriate C-style/static_cast without confirming the real type can cause undefined behaviour.
2. Insert Range Checker: Accessing past the end of an array/pointer or a container’s operator[] causes undefined behaviour; using at() will throw std::out_of_range.
3. Insert Null Pointer Checker: Dereferencing a null pointer leads to undefined behaviour; calling std::optional::value() without first checking has_value() throws std::bad_optional_access.
4. Insert Missed Statement: Missing return/break/delete/try-catch etc. causes logic errors or resource leaks.
5. Mutate Conditional Expression: A logical error in the boolean condition requiring changes.
6. Mutate Data Type: Choosing wrong type, signed/unsigned mismatch, ignoring const/volatile causes compile or logic errors
7. Mutate Literal Expression: An incorrect literal (boolean, numeric, or string) used in the code, requiring replacement with a correct value.
8. Mutate Method Invocation Expression: Function name wrong, arguments mismatch with parameters or overload, or missing std:: scope.
9. Mutate Integer Division Operation: Using integer division where floating-point operations are required, causing precision loss.
10. Mutate Operators: Incorrect usage of relational, arithmetic, or pointer operators, or missing parentheses affecting operator precedence.
11. Mutate Return Statement: The return statement has an incorrect expression or type that does not match the function's return type or logic.
12. Mutate Variable: Using the wrong variable (type or name), causing logic or type conflicts.
13. Move Statement: A statement is placed in an incorrect position, disrupting the intended flow.
14. Remove Buggy Statement: A statement that causes errors should be removed.
"""

REPAIR_TEMPLATE_CPP = """
1. Insert Cast Checker: 
- When to use: When a cast (pointer or reference) can be wrong at run time—for example, down-casting through an inheritance hierarchy or converting between unrelated pointer types—because an invalid cast leads to undefined behaviour.
- How to use: Use dynamic_cast on a polymorphic base and test the result (or catch std::bad_cast for references); if performance demands a static_cast, wrap it in a debug-only assert that the corresponding dynamic_cast succeeds, and avoid reinterpret_cast altogether—when it is truly unavoidable, add explicit run-time validation.
- Example:
```diff
if (auto* d = dynamic_cast<Derived*>(base)) {
    // safe use of d
}
```

2. Insert Range Checker: 
- When to use: When accessing arrays, pointers, or containers without ensuring the index is valid.
- How to use: Check boundaries, such as comparing the index with the size of the array or container (e.g., vec.size()).
- Example:
```diff
+ if (index < vec.size()) {
+     vec[index] = value;
+ }
```

3. Insert Null Pointer Checker: 
- When to use: If a pointer can be nullptr, dereferencing it without checking can cause a crash.
- How to use: Add a check: if (ptr == nullptr) and handle it appropriately.
- Example:
```diff
+ if (ptr == nullptr) {
+     return 0; // e.g., if the function returns an int
+ }
usePointer(ptr);
```

4. Insert Missed Statement: 
- When to use: If a crucial statement, such as a function call, return, if, or try-catch, is missing.
- How to use: Insert it in the correct place to restore proper logic.
- Example:
```diff
+ someFunctionCall(arg);
```
or
```diff
+ return default_value;
```

5. Mutate Conditional Expression: 
- When to use: If a conditional expression is incorrect or missing parts, requiring logic adjustments.
- How to use: Add, remove, or replace parts of the expression as needed.
- Example:
```diff
- if (a > 0)
+ if (a > 0 && b < 5)
```

6. Mutate Data Type: 
- When to use: If the declared type or cast type is incorrect.
- How to use: Change to the correct type in the variable declaration or cast operation.
- Example:
```diff
- int value = static_cast<int>(someVar);
+ double value = static_cast<double>(someVar);
```

7. Mutate Literal Expression: 
- When to use: If a literal (bool, numeric, string) is wrong.
- How to use: Replace with the correct literal or expression.
- Example:
```diff
- return 1;
+ return 0;
```

8. Mutate Method Invocation Expression: 
- When to use: If the function name or parameters do not match any valid overload or correct usage.
- How to use: Update the function name or argument list to match the intended signature.
- Example:
```diff
- processData(val1, val2);
+ processRecord(val1, val2);
```

9. Mutate Integer Division Operation: 
- When to use: If integer division is used where a floating-point result is needed.
- How to use: Cast one operand to double or float.
- Example:
```diff
- int result = a / b;
+ double result = static_cast<double>(a) / b;
```

10. Mutate Operators: 
- When to use: If an incorrect operator or missing parentheses causes logical errors.
- How to use: Replace with the correct operator or add parentheses to fix the precedence.
- Example:
```diff
- if (a != b) ...
+ if (a == b) ...
```

11. Mutate Return Statement: 
- When to use: If the return expression is incorrect or incompatible with the function's return type.
- How to use: Use a more appropriate expression or correct type.
- Example:
```diff
- return doSomething();
+ return doAnotherThing();
```

12. Mutate Variable: 
- When to use: If the code references the wrong variable or an incompatible identifier.
- How to use: Replace it with a variable that satisfies the required type and logic.
- Example:
```diff
- result = val1;
+ result = val2;
```

13. Move Statement: 
- When to use: If a statement is in the wrong place, breaking the logical flow.
- How to use: Move the statement to the correct position.
- Example:
```diff
- statement;
  ...
+ statement;
```

14. Remove Buggy Statement: 
- When to use: If a statement causes errors, removing it resolves the problem.
- How to use: Delete the problematic statement.
- Example:
```diff
- statement;
```
"""

