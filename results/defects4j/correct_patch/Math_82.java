private Integer getPivotRow(final int col, final SimplexTableau tableau) {
        double minRatio = Double.MAX_VALUE;
        Integer minRatioPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
             final double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
             final double entry = tableau.getEntry(i, col);
             // Changed condition to strictly positive to avoid division by zero issues
             if (MathUtils.compareTo(entry, 0, epsilon) > 0) {
                 final double ratio = rhs / entry;
                 if (ratio < minRatio) {
                     minRatio = ratio;
                     minRatioPos = i;
                 }
             }
        }
        return minRatioPos;
}
