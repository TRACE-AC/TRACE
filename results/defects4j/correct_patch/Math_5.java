public Complex reciprocal() {
        if (isNaN) {
            return NaN;
        }
 
        // Return INF for zero complex number to avoid division by zero (modified as per bug report feedback)
        if (real == 0.0 && imaginary == 0.0) {
            return INF; // Changed from NaN to INF
        }
 
        if (isInfinite) {
            return ZERO;
        }
 
        if (FastMath.abs(real) < FastMath.abs(imaginary)) {
            double q = real / imaginary;
            double scale = 1. / (real * q + imaginary);
            return createComplex(scale * q, -scale);
        } else {
            double q = imaginary / real;
            double scale = 1. / (imaginary * q + real);
            return createComplex(scale, -scale * q);
        }
}
