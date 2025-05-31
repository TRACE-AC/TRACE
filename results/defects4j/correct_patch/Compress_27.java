public static long parseOctal(final byte[] buffer, final int offset, final int length) {
    long    result = 0;
    int     end = offset + length;
    int     start = offset;

    if (length < 2) {
        throw new IllegalArgumentException("Length " + length + " must be at least 2");
    }

    while (start < end && buffer[start] == ' ') { // Ignore leading spaces first
        start++;
    }

    if (start < end && buffer[start] == 0) { // Check if it is a leading NUL after spaces handled
        return 0L;
    }

    while (start < end && (buffer[end - 1] == 0 || buffer[end - 1] == ' ')) {
        end--;
    }

    if (start >= end) {
        throw new IllegalArgumentException(
                exceptionMessage(buffer, offset, length, start, buffer[end - 1]));
    }

    for (; start < end; start++) {
        final byte currentByte = buffer[start];
        // CheckStyle:MagicNumber OFF
        if (currentByte < '0' || currentByte > '7') {
            throw new IllegalArgumentException(
                    exceptionMessage(buffer, offset, length, start, currentByte));
        }
        result = (result << 3) + (currentByte - '0'); // convert from ASCII
        // CheckStyle:MagicNumber ON
    }

    return result;
}
