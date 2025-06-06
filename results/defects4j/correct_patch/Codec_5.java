void decode(byte[] in, int inPos, int inAvail) {
    if (eof) {
        return;
    }
    if (inAvail < 0) {
        eof = true;
    }
    for (int i = 0; i < inAvail; i++) {
        if (buffer == null || buffer.length - pos < decodeSize) {
            resizeBuffer();
        }
        byte b = in[inPos++];
        if (b == PAD) {
            // We're done.
            eof = true;
            break;
        } else {
            if (b >= 0 && b < DECODE_TABLE.length) {
                int result = DECODE_TABLE[b];
                if (result >= 0) {
                    modulus = (++modulus) % 4;
                    x = (x << 6) + result;
                    if (modulus == 0) {
                        buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                        buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                        buffer[pos++] = (byte) (x & MASK_8BITS);
                    }
                }
            }
        }
    }

    // Two forms of EOF as far as base64 decoder is concerned: actual
    // EOF (-1) and first time '=' character is encountered in stream.
    // This approach makes the '=' padding characters completely optional.
    if (eof && modulus != 0) {
        // Ensure buffer is initialized before writing in the EOF block
        if (buffer == null || buffer.length - pos < decodeSize) {
            resizeBuffer(); // Added to prevent NullPointerException when writing to buffer
        }
        x = x << 6;
        switch (modulus) {
            case 2:
                x = x << 6;
                buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                break;
            case 3:
                buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                break;
        }
    }
}
