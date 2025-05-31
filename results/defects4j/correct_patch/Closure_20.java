private Node tryFoldSimpleFunctionCall(Node n) {
    Preconditions.checkState(n.isCall());
    Node callTarget = n.getFirstChild();
    if (callTarget != null && callTarget.isName() &&
          callTarget.getString().equals("String") &&
          n.getChildCount() == 2) { // Ensure there is exactly one argument
       Node value = callTarget.getNext();
       // Added immutability check to ensure the argument is an immutable literal
       if (value != null && NodeUtil.isImmutableValue(value)) { // Mutate Conditional Expression
         Node addition = IR.add(
             IR.string("").srcref(callTarget),
             value.detachFromParent());
        n.getParent().replaceChild(n, addition);
        reportCodeChange();
        return addition;
      }
    }
    return n;
}
