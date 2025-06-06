private void printAndQuote(final Object object, final CharSequence value, final int offset, final int len,
            final Appendable out, final boolean newRecord) throws IOException {
        boolean quote = false;
        int start = offset;
        int pos = offset;
        final int end = offset + len;

        final char delimChar = getDelimiter();
        final char quoteChar = getQuoteCharacter().charValue();

        QuoteMode quoteModePolicy = getQuoteMode();
        if (quoteModePolicy == null) {
            quoteModePolicy = QuoteMode.MINIMAL;
        }
        switch (quoteModePolicy) {
        case ALL:
        case ALL_NON_NULL:
            quote = true;
            break;
        case NON_NUMERIC:
            quote = !(object instanceof Number);
            break;
        case NONE:
            // Use the existing escaping code
            printAndEscape(value, offset, len, out);
            return;
        case MINIMAL:
            // Removed early check based solely on object type to allow minimal quoting logic
            if (len <= 0) {
                // always quote an empty token that is the first on the line,
                // as it may be the only thing on the line. If it were not quoted in that case,
                // an empty line has no tokens.
                if (newRecord) {
                    quote = true;
                }
            } else {
                char c = value.charAt(pos);

                // Modified: Removed overly aggressive newRecord condition for non-ASCII characters
                if (c <= COMMENT) {
                    quote = true;
                } else {
                    while (pos < end) {
                        c = value.charAt(pos);
                        if (c == LF || c == CR || c == quoteChar || c == delimChar) {
                            quote = true;
                            break;
                        }
                        pos++;
                    }

                    if (!quote) {
                        pos = end - 1;
                        c = value.charAt(pos);
                        // Encapsulate if ending character is less than ' '
                        if (c <= SP) {
                            quote = true;
                        }
                    }
                }
            }

            if (!quote) {
                // No encapsulation needed - write out the original value
                out.append(value, start, end);
                return;
            }
            break;
        default:
            throw new IllegalStateException("Unexpected Quote value: " + quoteModePolicy);
        }

        if (!quote) {
            // No encapsulation needed - write out the original value
            out.append(value, start, end);
            return;
        }

        // We hit something that needed encapsulation
        out.append(quoteChar);

        // Pick up where we left off: pos should be positioned on the first character that caused
        // the need for encapsulation.
        while (pos < end) {
            final char c = value.charAt(pos);
            if (c == quoteChar) {
                // Write out the chunk up until this point

                // Add 1 to the length to write out the encapsulator also
                out.append(value, start, pos + 1);
                // Set start to the quoting character for doubling
                start = pos;
            }
            pos++;
        }

        // Write the last segment and closing quote
        out.append(value, start, pos);
        out.append(quoteChar);
}
