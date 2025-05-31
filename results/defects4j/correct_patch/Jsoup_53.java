public String chompBalanced(char open, char close) {
        int start = -1;
        int end = -1;
        int depth = 0;
        char last = 0;
        // Added variables to handle quoted context
        boolean inQuote = false;  
        char quoteChar = 0;
 
        do {
            if (isEmpty()) break;
            Character c = consume();
 
            // Handle quoted strings: if not in quote and current char is a quote, enter quoted state
            if (!inQuote && (c.equals('\"') || c.equals('\'')) && (last == 0 || last != ESC)) {
                inQuote = true;
                quoteChar = c;
                last = c;
                // Skip processing balance tokens inside quotes
                continue;
            } else if (inQuote) {
                // If we are in a quoted state, check if the quote ends (and is not escaped)
                if (c.equals(quoteChar) && (last == 0 || last != ESC)) {
                    inQuote = false;
                }
                last = c;
                // Update end if inside a balanced section even within quotes
                if (depth > 0)
                    end = pos; // don't include the outer match pair in the return
                continue; // Skip balance checking while in a quoted state
            }
 
            if (last == 0 || last != ESC) {
                if (c.equals(open)) {
                    depth++;
                    if (start == -1)
                        start = pos;
                }
                else if (c.equals(close))
                    depth--;
            }
 
            if (depth > 0 && last != 0)
                end = pos; // update end position for the balanced content
            last = c;
        } while (depth > 0);
        return (end >= 0) ? queue.substring(start, end) : "";
}
