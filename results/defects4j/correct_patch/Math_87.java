private Integer getBasicRow(final int col) {
    Integer row = null;
    for (int i = getNumObjectiveFunctions(); i < getHeight(); i++) {
        double entry = getEntry(i, col); // store entry value for clarity
        if (!MathUtils.equals(entry, 0.0, epsilon)) {
            if (!MathUtils.equals(entry, 1.0, epsilon)) { // Check that the nonzero entry is normalized to 1
                return null; // Nonzero entry is not 1, so the column is not basic
            }
            if (row == null) {
                row = i;
            } else {
                return null; // Return null immediately if a second entry equal to 1 is found
            }
        }
    }
    return row;
}
