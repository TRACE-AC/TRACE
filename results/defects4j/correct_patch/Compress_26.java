public static long skip(InputStream input, long numToSkip) throws IOException {
    long totalSkipped = 0; // Use an accumulator for the actual bytes skipped
    while (numToSkip > 0) {
        long skipped = input.skip(numToSkip);
        if (skipped == 0) {
            // Fallback: try reading a chunk of bytes if skip returns 0
            int chunkSize = (int) Math.min(numToSkip, 8192); // Limit the buffer size
            byte[] buffer = new byte[chunkSize];
            int n = input.read(buffer, 0, chunkSize); // Attempt to read multiple bytes
            if (n < 0) { // End of stream reached
                break;
            }
            skipped = n; // Count the bytes read as skipped
        }
        numToSkip -= skipped;
        totalSkipped += skipped; // Accumulate the total skipped bytes
    }
    return totalSkipped;
}
