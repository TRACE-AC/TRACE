public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        // Removed CustomCharSequence branch since CustomCharSequence is undefined
        if (cs1.length() != cs2.length()) { // added length check for proper comparison
            return false;
        }
        for (int i = 0, len = cs1.length(); i < len; i++) { // iterate over all characters
            if (cs1.charAt(i) != cs2.charAt(i)) { // compare each character
                return false;
            }
        }
        return true; // all characters match, so the sequences are equal
}
