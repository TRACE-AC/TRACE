public int[] getCounts(int index) {
        if (index < 0 ||
            index >= totalSize) {
            throw new OutOfRangeException(index, 0, totalSize);
        }

        final int[] indices = new int[dimension];

        int count = 0;
        for (int i = 0; i < last; i++) {
            int idx = 0;
            final int offset = uniCounterOffset[i];
            while (count <= index) {
                count += offset;
                ++idx;
            }
            --idx;
            count -= offset;
            indices[i] = idx;
        }

        int idx = 0; // Initialize idx to 0 for final dimension (consistent with earlier dimensions)
        while (count <= index) { // Use <= condition as in the for-loop
            count += 1; // Increment by 1 for the final dimension
            ++idx;
        }
        --idx; // Subtract one to correct the off-by-one error
        indices[last] = idx;

        return indices;
}
