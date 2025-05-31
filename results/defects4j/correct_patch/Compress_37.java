Map<String, String> parsePaxHeaders(final InputStream i)
        throws IOException {
        final Map<String, String> headers = new HashMap<String, String>(globalPaxHeaders);
        // Format is "length keyword=value\n";
        outer: while(true){ // get length
            int ch;
            int len = 0;
            int read = 0;
            // Parse the length field, ensuring only digits contribute to the length.
            while((ch = i.read()) != -1) {
                // If a blank line is encountered (e.g. only newline) before any digit, skip it.
                if(read == 0 && ch == '\n') {
                    continue outer; // Skip blank line
                }
                if(ch == ' ') {
                    break; // End of length field when space found.
                }
                if(!Character.isDigit(ch)) { // Check that the character is a digit.
                    throw new IOException("Malformed Paxheader: non-digit character encountered while reading length: " + (char) ch);
                }
                read++;
                len = len * 10 + (ch - '0');
            }
            if(ch == -1){ // EOF reached while reading length
                break;
            }
            // Count the space that terminated the length field.
            read++;
            // Get keyword
            final ByteArrayOutputStream coll = new ByteArrayOutputStream();
            while((ch = i.read()) != -1) {
                read++;
                if(ch == '='){ // end of keyword
                    final String keyword = coll.toString(CharsetNames.UTF_8);
                    // Get rest of entry
                    final int restLen = len - read;
                    if(restLen < 0) { // Range checker for malformed header lines
                        throw new IOException("Malformed Paxheader: computed rest length is negative (" + restLen + ").");
                    }
                    if(restLen == 1) { // only NL
                        headers.remove(keyword);
                    } else {
                        final byte[] rest = new byte[restLen];
                        final int got = IOUtils.readFully(i, rest);
                        if(got != restLen) {
                            throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
                        }
                        // Drop trailing NL
                        final String value = new String(rest, 0, restLen - 1, CharsetNames.UTF_8);
                        headers.put(keyword, value);
                    }
                    break;
                }
                coll.write((byte) ch);
            }
            if(ch == -1){ // EOF reached while reading keyword
                break;
            }
        }
        return headers;
    }
