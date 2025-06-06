void tryMinimizeExits(Node n, int exitType, String labelName) {

    // Just an 'exit'.
    // Added check to avoid removing exit nodes that are within a finally block.
    boolean inFinally = false;
    for (Node p = n; p != null; p = p.getParent()) {
      if (p.isTry() && NodeUtil.hasFinally(p)) {
        Node finallyBlock = p.getLastChild();
        // Check if n is a descendant of the finally block.
        for (Node temp = n; temp != null && temp != p; temp = temp.getParent()) {
          if (temp == finallyBlock) {
            inFinally = true;
            break;
          }
        }
        if (inFinally) break;
      }
    }
    if (matchingExitNode(n, exitType, labelName) && !inFinally) { // Only remove if not in a finally block.
      NodeUtil.removeChild(n.getParent(), n);
      compiler.reportCodeChange();
      return;
    }

    // Just an 'if'.
    if (n.isIf()) {
      Node ifBlock = n.getFirstChild().getNext();
      tryMinimizeExits(ifBlock, exitType, labelName);
      Node elseBlock = ifBlock.getNext();
      if (elseBlock != null) {
        tryMinimizeExits(elseBlock, exitType, labelName);
      }
      return;
    }

    // Just a 'try/catch/finally'.
    if (n.isTry()) {
      Node tryBlock = n.getFirstChild();
      tryMinimizeExits(tryBlock, exitType, labelName);
      Node allCatchNodes = NodeUtil.getCatchBlock(n);
      if (NodeUtil.hasCatchHandler(allCatchNodes)) {
        Preconditions.checkState(allCatchNodes.hasOneChild());
        Node catchNode = allCatchNodes.getFirstChild();
        Node catchCodeBlock = catchNode.getLastChild();
        tryMinimizeExits(catchCodeBlock, exitType, labelName);
      }
      /* Don't try to minimize the exits of finally blocks, as this
       * can cause problems if it changes the completion type of the finally
       * block. See ECMA 262 Sections 8.9 & 12.14
       */
      if (NodeUtil.hasFinally(n)) {
         Node finallyBlock = n.getLastChild();
         // Removed recursive call on finallyBlock to preserve control flow.
         // tryMinimizeExits(finallyBlock, exitType, labelName);
      }
    }
 
    // Just a 'label'.
    if (n.isLabel()) {
      Node labelBlock = n.getLastChild();
      tryMinimizeExits(labelBlock, exitType, labelName);
    }

    // TODO(johnlenz): The last case of SWITCH statement?

    // The rest assumes a block with at least one child, bail on anything else.
    if (!n.isBlock() || n.getLastChild() == null) {
      return;
    }

    // Multiple if-exits can be converted in a single pass.
    // Convert "if (blah) break;  if (blah2) break; other_stmt;" to
    // become "if (blah); else { if (blah2); else { other_stmt; } }"
    // which will get converted to "if (!blah && !blah2) { other_stmt; }".
    for (Node c : n.children()) {

      // An 'if' block to process below.
      if (c.isIf()) {
        Node ifTree = c;
        Node trueBlock, falseBlock;

        // First, the true condition block.
        trueBlock = ifTree.getFirstChild().getNext();
        falseBlock = trueBlock.getNext();
        tryMinimizeIfBlockExits(trueBlock, falseBlock,
            ifTree, exitType, labelName);

        // Now the else block.
        // The if blocks may have changed, get them again.
        trueBlock = ifTree.getFirstChild().getNext();
        falseBlock = trueBlock.getNext();
        if (falseBlock != null) {
          tryMinimizeIfBlockExits(falseBlock, trueBlock,
              ifTree, exitType, labelName);
        }
      }

      if (c == n.getLastChild()) {
        break;
      }
    }

    // Now try to minimize the exits of the last child, if it is removed
    // look at what has become the last child.
    for (Node c = n.getLastChild(); c != null; c = n.getLastChild()) {
      tryMinimizeExits(c, exitType, labelName);
      // If the node is still the last child, we are done.
      if (c == n.getLastChild()) {
        break;
      }
    }
  }
