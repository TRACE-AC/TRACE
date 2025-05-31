public boolean hasSameMethod(Invocation candidate) {        
    //not using method.equals() for 1 good reason:
    //sometimes java generates forwarding methods when generics are in play see JavaGenericsForwardingMethodsTest
    Method m1 = invocation.getMethod();
    Method m2 = candidate.getMethod();
    
    // Use a custom equivalence check: compare method names and parameter types to handle forwarding methods.
    if(m1.getName().equals(m2.getName()) && java.util.Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes())) {
        return true; // methods are effectively the same
    }
    return false; // methods differ
}
