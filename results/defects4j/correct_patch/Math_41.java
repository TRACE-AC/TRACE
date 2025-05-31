public double evaluate(final double[] values, final double[] weights,
                           final double mean, final int begin, final int length) {

    // Added explicit input validation to throw IllegalArgumentException if inputs are invalid
    if (values == null) {
        throw new IllegalArgumentException("Values array must not be null."); // Null check for values
    }
    if (weights == null) {
        throw new IllegalArgumentException("Weights array must not be null."); // Null check for weights
    }
    if (values.length != weights.length) {
        throw new IllegalArgumentException("Values and weights arrays must have the same length."); // Array length check
    }
    if (begin < 0 || length < 0 || (begin + length) > values.length) {
        throw new IllegalArgumentException("Invalid begin index or length."); // Check bounds for begin and length
    }
    for (double w : weights) {
        if (Double.isNaN(w) || Double.isInfinite(w) || w < 0) {
            throw new IllegalArgumentException("Weights array contains NaN, infinite, or negative values."); // Validate each weight
        }
    }
    
    double var = Double.NaN;

    if (test(values, weights, begin, length)) {
        if (length == 1) {
            var = 0.0;
        } else if (length > 1) {
            double accum = 0.0;
            double dev = 0.0;
            double accum2 = 0.0;
            for (int i = begin; i < begin + length; i++) {
                dev = values[i] - mean;
                accum += weights[i] * (dev * dev);
                accum2 += weights[i] * dev;
            }
 
            double sumWts = 0;
            // Fixed: Sum weights only for the subarray (from begin to begin+length)
            for (int i = begin; i < begin + length; i++) {
                sumWts += weights[i];
            }
 
            if (isBiasCorrected) {
                var = (accum - (accum2 * accum2 / sumWts)) / (sumWts - 1.0);
            } else {
                var = (accum - (accum2 * accum2 / sumWts)) / sumWts;
            }
        }
    }
    return var;
}
