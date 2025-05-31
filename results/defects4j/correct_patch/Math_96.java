public boolean equals(Object other) {
        boolean ret;
        
        if (this == other) { 
            ret = true;
        } else if (other == null) {
            ret = false;
        } else  {
            try {
                Complex rhs = (Complex) other;
                if (rhs.isNaN()) {
                    ret = this.isNaN();
                } else {
                    // Normalize zeros for both operands to treat -0.0 as 0.0
                    double normReal = (real == 0.0) ? 0.0 : real;
                    double normImaginary = (imaginary == 0.0) ? 0.0 : imaginary;
                    double normRhsReal = (rhs.getReal() == 0.0) ? 0.0 : rhs.getReal();
                    double normRhsImaginary = (rhs.getImaginary() == 0.0) ? 0.0 : rhs.getImaginary();
                    ret = (Double.compare(normReal, normRhsReal) == 0) 
                          && (Double.compare(normImaginary, normRhsImaginary) == 0); // compare normalized values
                }
            } catch (ClassCastException ex) {
                // ignore exception
                ret = false;
            }
        }
      
        return ret;
}
