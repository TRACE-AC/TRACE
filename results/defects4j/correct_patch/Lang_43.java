private StringBuffer appendQuotedString(String pattern, ParsePosition pos,
            StringBuffer appendTo, boolean escapingOn) {
         int start = pos.getIndex();
         char[] c = pattern.toCharArray();
         // If the first character is a quote and escaping is on, update pos and append a single quote.
         if (escapingOn && c[start] == QUOTE) {
             pos.setIndex(start + 1); // update pos for consistency
             return appendTo == null ? null : appendTo.append(QUOTE);
         }
         int lastHold = start;
         int i = start; // use a single loop variable for tracking the current position
         while (i < pattern.length()) {
             // Check for an escaped quote at the current position.
             if (escapingOn && pattern.startsWith(ESCAPED_QUOTE, i)) {
                 // Append the segment from lastHold up to i and then a single quote.
                 appendTo.append(c, lastHold, i - lastHold).append(QUOTE);
                 i += ESCAPED_QUOTE.length(); // advance i past the escape sequence
                 lastHold = i;              // set lastHold to the new position
                 continue;
             }
             // When a closing quote is encountered, update pos and return the accumulated text.
             if (c[i] == QUOTE) {
                 pos.setIndex(i + 1); // update pos after the closing quote
                 return appendTo == null ? null : appendTo.append(c, lastHold, i - lastHold);
             }
             i++; // move to the next character
         }
         throw new IllegalArgumentException(
                 "Unterminated quoted string at position " + start);
}
