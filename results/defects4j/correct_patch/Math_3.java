public static double linearCombination(final double[] a, final double[] b)
        throws DimensionMismatchException {
        final int len = a.length;
        if (len != b.length) {
            throw new DimensionMismatchException(len, b.length);
        }

            // Revert to scalar multiplication.

        final double[] prodHigh = new double[len];
        double prodLowSum = 0;

        for (int i = 0; i < len; i++) {
            final double ai = a[i];
            final double ca = SPLIT_FACTOR * ai;
            final double aHigh = ca - (ca - ai);
            final double aLow = ai - aHigh;

            final double bi = b[i];
            final double cb = SPLIT_FACTOR * bi;
            final double bHigh = cb - (cb - bi);
            final double bLow = bi - bHigh;
            prodHigh[i] = ai * bi;
            final double prodLow = aLow * bLow - (((prodHigh[i] -
                                                    aHigh * bHigh) -
                                                   aLow * bHigh) -
                                                  aHigh * bLow);
            prodLowSum += prodLow;
        }

        double sHighPrev;
        double sLowSum = 0;
        if (len > 1) { // Check for array length > 1 before accessing prodHigh[1]
            final double prodHighCur = prodHigh[0];
            double prodHighNext = prodHigh[1];
            sHighPrev = prodHighCur + prodHighNext;
            double sPrime = sHighPrev - prodHighNext;
            sLowSum = (prodHighNext - (sHighPrev - sPrime)) + (prodHighCur - sPrime);

            final int lenMinusOne = len - 1;
            for (int i = 1; i < lenMinusOne; i++) {
                prodHighNext = prodHigh[i + 1];
                final double sHighCur = sHighPrev + prodHighNext;
                sPrime = sHighCur - prodHighNext;
                sLowSum += (prodHighNext - (sHighCur - sPrime)) + (sHighPrev - sPrime);
                sHighPrev = sHighCur;
            }
        } else {
            sHighPrev = prodHigh[0]; // Handle the case where the array length is 1
        }

        double result = sHighPrev + (prodLowSum + sLowSum);

        if (Double.isNaN(result)) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = 0;
            for (int i = 0; i < len; ++i) {
                result += a[i] * b[i];
            }
        }

        return result;
    }
