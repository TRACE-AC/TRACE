private boolean isSafeReplacement(Node node, Node replacement) {
    // No checks are needed for simple names.
    if (node.isName()) {
      return true;
    }
    Preconditions.checkArgument(node.isGetProp());

    // Traverse the entire getProp chain to find the leftmost identifier.
    Node current = node;
    while (current.isGetProp()) {  // Traverse through chained getProp nodes.
      current = current.getFirstChild();
    }
    
    if (current.isName() && isNameAssignedTo(current.getString(), replacement)) {
      return false;
    }

    return true;
}
