public final void translate(CharSequence input, Writer out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (input == null) {
             return;
         }
         // Use char index instead of code point count
         int pos = 0;
         int len = input.length();
         while (pos < len) {
             int consumed = translate(input, pos, out);
             if (consumed == 0) {
                 int cp = Character.codePointAt(input, pos);
                 char[] c = Character.toChars(cp);
                 out.write(c);
                 pos += Character.charCount(cp); // increment by code unit count of the code point
             }
             else {
                 // Advance pos by the number of char units corresponding to the consumed code points
                 for (int pt = 0; pt < consumed; pt++) {
                     int cp = Character.codePointAt(input, pos);
                     pos += Character.charCount(cp);
                 }
             }
         }
     }
