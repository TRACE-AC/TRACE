public double cumulativeProbability(double x) throws MathException {
    // Early range checks for extreme values (beyond 40 standard deviations)
    if (x <= mean - 40 * standardDeviation) { // per documentation: return 0 if x is extremely low
        return 0;
    }
    if (x >= mean + 40 * standardDeviation) { // per documentation: return 1 if x is extremely high
        return 1;
    }
    final double dev = x - mean;
    try {
        return 0.5 * (1.0 + Erf.erf(dev / (standardDeviation * FastMath.sqrt(2.0))));
    } catch (MaxIterationsExceededException ex) {
        // With early checks in place, we can simply propagate the exception.
        throw ex;
    }
}
