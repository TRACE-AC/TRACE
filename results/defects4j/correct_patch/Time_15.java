public static long safeMultiply(long val1, int val2) {
         switch (val2) {
             case -1:
                 if (val1 == Long.MIN_VALUE) { // check for overflow when negating Long.MIN_VALUE
                     throw new ArithmeticException("Multiplication overflows a long: " + val1 + " * " + val2);
                 }
                 return -val1;
             case 0:
                 return 0L;
             case 1:
                 return val1;
         }
         long total = val1 * val2;
         if (total / val2 != val1) {
             throw new ArithmeticException("Multiplication overflows a long: " + val1 + " * " + val2);
         }
         return total;
}
