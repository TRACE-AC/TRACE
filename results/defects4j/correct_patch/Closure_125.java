private void visitNew(NodeTraversal t, Node n) {
    Node constructor = n.getFirstChild();
    JSType type = getJSType(constructor).restrictByNotNullOrUndefined();
    // Process parameter list only if type is definitively a constructor.
    if (type.isConstructor()) { 
       FunctionType fnType = type.toMaybeFunctionType();
       if (fnType != null) {
         visitParameterList(t, n, fnType); // Only for true constructors
         // Check for null instance type to avoid IllegalStateException
         if (fnType.getInstanceType() != null) {
             ensureTyped(t, n, fnType.getInstanceType());
         } else {
             ensureTyped(t, n); // Fallback if instance type is null
         }
       } else {
         ensureTyped(t, n);
       }
    } else if (type.isEmptyType() || type.isUnknownType()) {
      // For empty or unknown types, do not process parameters; simply ensure type.
      ensureTyped(t, n);
    } else {
      report(t, n, NOT_A_CONSTRUCTOR);
      ensureTyped(t, n);
    }
}
