private VariableLiveness isVariableReadBeforeKill(
       Node n, String variable) {
     if (NodeUtil.isName(n) && variable.equals(n.getString())) {
       if (NodeUtil.isLhs(n, n.getParent())) {
         // Modified: Instead of immediately returning KILL for a LHS,
         // check if the RHS contains a read of the variable.
         Node parent = n.getParent();
         if (parent != null && parent.getChildCount() > 1) {
           Node rhs = n.getNext();  // Get the right-hand side expression.
           if (rhs != null) {
             VariableLiveness rhsState = isVariableReadBeforeKill(rhs, variable);
             if (rhsState == VariableLiveness.READ) {
               // A read occurs in the RHS, so report READ.
               return VariableLiveness.READ;
             }
           }
         }
         return VariableLiveness.KILL;
       } else {
         return VariableLiveness.READ;
       }
     }
  
     // Expressions are evaluated left-right, depth first.
     for (Node child = n.getFirstChild();
         child != null; child = child.getNext()) {
       if (!ControlFlowGraph.isEnteringNewCfgNode(child)) { // Not a FUNCTION
         VariableLiveness state = isVariableReadBeforeKill(child, variable);
         if (state != VariableLiveness.MAYBE_LIVE) {
           return state;
         }
       }
     }
     return VariableLiveness.MAYBE_LIVE;
   }
