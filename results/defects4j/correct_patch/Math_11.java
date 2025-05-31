public double density(final double[] vals) throws DimensionMismatchException {
        final int dim = getDimension();
        if (vals.length != dim) {
             throw new DimensionMismatchException(vals.length, dim);
         }
 
         return FastMath.pow(2 * FastMath.PI, -dim / 2.0) *  // Changed integer division to floating-point division
             FastMath.pow(covarianceMatrixDeterminant, -0.5) *
             getExponentTerm(vals);
     }
