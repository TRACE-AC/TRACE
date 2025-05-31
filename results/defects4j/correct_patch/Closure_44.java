void add(String newcode) {
    maybeEndStatement();

    if (newcode.length() == 0) {
      return;
    }

    char c = newcode.charAt(0);
    // Updated condition: also check for a forward slash following a forward slash per bug report feedback
    if (((isWordChar(c) || c == '\\') && isWordChar(getLastChar())) || (c == '/' && getLastChar() == '/')) {
       // need space to separate. This is not pretty printing.
       // For example: "return foo;"
       append(" "); // Insert space to separate tokens
       // Do not allow a forward slash to appear after a DIV.
       // For example,
       // REGEXP DIV REGEXP
       // is valid and should print like
       // / // / /
     }
 
     append(newcode);
  }
