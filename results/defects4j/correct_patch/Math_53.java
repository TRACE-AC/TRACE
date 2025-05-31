public Complex add(Complex rhs)
    throws NullArgumentException {
    MathUtils.checkNotNull(rhs);
    if (Double.isNaN(real) || Double.isNaN(imaginary) ||   // Check if current complex number has NaN 
        Double.isNaN(rhs.getReal()) || Double.isNaN(rhs.getImaginary())) { // Check if rhs complex number has NaN
        return Complex.NaN; // Return NaN if any part is NaN
    }
    double newReal = real + rhs.getReal();
    double newImaginary = imaginary + rhs.getImaginary();
    // Removed redundant NaN check on computed values (newReal, newImaginary)
    return createComplex(newReal, newImaginary); // Correctly propagate result without forcing NaN
}
