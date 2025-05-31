@Override
public int translate(CharSequence input, int index, Writer out) throws IOException {
    // TODO: Protect from ArrayIndexOutOfBounds
    if(index < input.length() - 1) { // Insert Range Checker
        if(input.charAt(index) == '&' && input.charAt(index + 1) == '#') {
            int start = index + 2;
            boolean isHex = false;

            char firstChar = input.charAt(start);
            if(firstChar == 'x' || firstChar == 'X') {
                start++;
                isHex = true;
            }

            int end = start;
            while(end < input.length() && input.charAt(end) != ';') { // Range Checker
                end++;
            }

            if(end < input.length()) { // Further validation check
                int entityValue;
                try {
                    if(isHex) {
                        entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 16);
                    } else {
                        entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 10);
                    }

                    // Write the correct character(s) to the output
                    char[] chars = Character.toChars(entityValue); // Mutate Method Invocation
                    out.write(chars, 0, chars.length); // Handle surrogate pairs
                } catch(NumberFormatException nfe) {
                    return 0;
                }

                return 2 + (end - start) + (isHex ? 1 : 0) + 1;
            }
        }
    }
    return 0;
}
