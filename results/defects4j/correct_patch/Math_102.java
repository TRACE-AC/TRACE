public double chiSquare(double[] expected, long[] observed)
        throws IllegalArgumentException {
        if ((expected.length < 2) || (expected.length != observed.length)) {
            throw new IllegalArgumentException(
                    "observed, expected array lengths incorrect");
        }
        if (!isPositive(expected) || !isNonNegative(observed)) {
             throw new IllegalArgumentException(
                 "observed counts must be non-negative and expected counts must be postive");
         }
         // Rescale the expected array only if necessary so that its total matches the observed total
         double sumExpected = 0.0d;
         double sumObserved = 0.0d;
         for (int i = 0; i < expected.length; i++) {
             sumExpected += expected[i];
             sumObserved += observed[i]; // Note: observed is long, but conversion to double is implicit
         }
         double scale = sumObserved / sumExpected; // compute scale factor
         final double TOLERANCE = 1E-9; // tolerance for comparing scale to 1

         double sumSq = 0.0d;
         double dev = 0.0d;
         for (int i = 0; i < observed.length; i++) {
                 // Apply scaling only if necessary; if scale is effectively 1, use expected[i] directly
                 double scaledExpected = (Math.abs(scale - 1.0) < TOLERANCE) ? expected[i] : expected[i] * scale; // modified: conditional scaling
                 dev = ((double) observed[i] - scaledExpected);
                 sumSq += dev * dev / scaledExpected;
         }
         return sumSq;
     }
