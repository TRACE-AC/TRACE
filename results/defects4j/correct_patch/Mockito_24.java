public Object answer(InvocationOnMock invocation) {
        if (methodsGuru.isToString(invocation.getMethod())) {
            Object mock = invocation.getMock();
            MockName name = mockUtil.getMockName(mock);
            if (name.isDefault()) {
                return "Mock for " + mockUtil.getMockSettings(mock).getTypeToMock().getSimpleName() + ", hashCode: " + mock.hashCode();
            } else {
                return name.toString();
            }
        } else if (methodsGuru.isCompareToMethod(invocation.getMethod())) {
             //see issue 184.
             //mocks by default should return 0 if references are the same, otherwise some other value because they are not the same.
             Object other = invocation.getArguments()[0]; // changed from getArgument(0) to getArguments()[0] to fix compile error
             Object mock = invocation.getMock(); // get the current mock
             if (other == mock) { // return 0 if comparing the same object
                 return 0; // correct behavior for compareTo when comparing the same reference
             }
             return 1; // for different references, return a non-zero value
         }
         
         Class<?> returnType = invocation.getMethod().getReturnType();
        return returnValueFor(returnType);
}
