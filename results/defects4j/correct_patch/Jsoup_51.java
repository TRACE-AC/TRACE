boolean matchesLetter() {
         if (isEmpty())
             return false;
         char c = input[pos];
         return Character.isLetter(c); // Use Unicode check for letters instead of ASCII ranges
     }
