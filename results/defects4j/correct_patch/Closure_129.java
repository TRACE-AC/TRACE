private void annotateCalls(Node n) {
      Preconditions.checkState(n.isCall());

      // Keep track of the "this" context of a call. A call without an
      // explicit "this" is a free call.
      Node first = n.getFirstChild();
      // Recursively unwrap cast node(s) if present to get the effective target.
      Node effectiveTarget = first;
      while (effectiveTarget != null && effectiveTarget.isCast()) {
         effectiveTarget = effectiveTarget.getFirstChild(); // Recursively unwrapping casts.
      }

      if (!NodeUtil.isGet(effectiveTarget)) {
         n.putBooleanProp(Node.FREE_CALL, true);
      }

      // Keep track of the context in which eval is called. It is important
      // to distinguish between "(0, eval)()" and "eval()".
      if (effectiveTarget.isName() &&
          "eval".equals(effectiveTarget.getString())) {
         effectiveTarget.putBooleanProp(Node.DIRECT_EVAL, true);
      }
}
