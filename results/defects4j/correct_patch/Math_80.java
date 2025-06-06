private boolean flipIfWarranted(final int n, final int step) {
         if (1.5 * work[pingPong] < work[4 * (n - 1) + pingPong]) {
             // flip array
             int j = 4 * n - 4; // changed from 4*n - 1 to 4*n - 4 to correctly point to the beginning of the last row
             for (int i = 0; i < j; i += 4) {
                 for (int k = 0; k < 4; k += step) {
                     final double tmp = work[i + k];
                     work[i + k] = work[j - k];
                     work[j - k] = tmp;
                 }
                 j -= 4;
             }
             return true;
         }
         return false;
}
