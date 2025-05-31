@Override
    public int read(byte[] buf, int offset, int numToRead) throws IOException {
        int totalRead = 0;

        if (hasHitEOF || entryOffset >= entrySize) {
            return -1;
        }

        if (currEntry == null) {
            throw new IllegalStateException("No current tar entry");
        }

        numToRead = Math.min(numToRead, available());

        totalRead = is.read(buf, offset, numToRead);
        // Insert explicit check for truncated entry: if stream returns -1 while entryOffset is less than entrySize, throw IOException.
        if (totalRead == -1) {
            if (entryOffset < entrySize) {
                throw new IOException("Truncated tar entry: unexpected end of stream");
            }
            hasHitEOF = true;
        } else {
            count(totalRead);
            entryOffset += totalRead;
        }

        return totalRead;
    }
