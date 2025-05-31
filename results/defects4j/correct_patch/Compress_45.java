public static int formatLongOctalOrBinaryBytes(
        final long value, final byte[] buf, final int offset, final int length) {

        // Check whether we are dealing with UID/GID or SIZE field
        final long maxAsOctalChar = length == TarConstants.UIDLEN ? TarConstants.MAXID : TarConstants.MAXSIZE;

        final boolean negative = value < 0;
        if (!negative && value <= maxAsOctalChar) { // OK to store as octal chars
            return formatLongOctalBytes(value, buf, offset, length);
        }
 
        if (length <= 8) { // Changed condition to use fast binary formatting for fields <= 8 bytes
            formatLongBinary(value, buf, offset, length, negative);
            buf[offset] = (byte) (negative ? 0xff : 0x80); // Moved sign marker setting into fast binary branch
            return offset + length;
        } else {
            formatBigIntegerBinary(value, buf, offset, length, negative); // Use BigInteger-based formatting for fields > 8 bytes
            buf[offset] = (byte) (negative ? 0xff : 0x80);
            return offset + length;
        }
}
