private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
         // https://html.spec.whatwg.org/multipage/parsing.html#has-an-element-in-the-specific-scope
         int bottom = stack.size() - 1; // use actual bottom index
         final int top = Math.max(bottom - MaxScopeSearchDepth, 0); // limit search depth to prevent scanning full stack
 
         for (int pos = bottom; pos >= top; pos--) {
             final String elName = stack.get(pos).nodeName();
             if (inSorted(elName, targetNames))
                 return true;
             if (inSorted(elName, baseTypes))
                 return false;
             if (extraTypes != null && inSorted(elName, extraTypes))
                 return false;
         }
         //Validate.fail("Should not be reachable"); // would end up false because hitting 'html' at root (basetypes)
         return false;
     }
