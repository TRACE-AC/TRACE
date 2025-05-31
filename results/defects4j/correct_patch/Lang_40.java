public static boolean containsIgnoreCase(String str, String searchStr) {
         if (str == null || searchStr == null) {
             return false;
         }
         // Quick check: an empty search string is always considered to be contained.
         if (searchStr.length() == 0) {
             return true;
         }
         // Instead of converting entire strings to upper case (which causes locale-dependent issues like "ß" -> "SS"),
         // iterate through 'str' and compare regions using case-insensitive comparison.
         int end = str.length() - searchStr.length();
         for (int i = 0; i <= end; i++) {
             if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) { // true enables case-insensitive comparison
                 return true;
             }
         }
         return false;
     }
