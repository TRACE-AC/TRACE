public double getChiSquare() {
         double chiSquare = 0;
         for (int i = 0; i < rows; ++i) {
             final double residual = residuals[i];
             chiSquare += residual * residual * residualsWeights[i]; // Changed division to multiplication to correctly apply the weight
         }
         return chiSquare;
     }
