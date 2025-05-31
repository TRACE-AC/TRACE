public static DateTimeZone forOffsetHoursMinutes(int hoursOffset, int minutesOffset) throws IllegalArgumentException {
         // Added range checker for hoursOffset
         if (hoursOffset < -23 || hoursOffset > 23) {
             throw new IllegalArgumentException("Hours out of range: " + hoursOffset);
         }
         if (hoursOffset == 0 && minutesOffset == 0) {
             return DateTimeZone.UTC;
         }
         if (minutesOffset < 0 || minutesOffset > 59) {
             throw new IllegalArgumentException("Minutes out of range: " + minutesOffset);
         }
         int offset = 0;
         try {
             int totalMinutes; // combine hours and minutes correctly
             if (hoursOffset < 0) { // use hoursOffset directly to decide sign
                 totalMinutes = FieldUtils.safeAdd(FieldUtils.safeMultiply(hoursOffset, 60), -minutesOffset); // subtract minutes for negative offset
             } else {
                 totalMinutes = FieldUtils.safeAdd(FieldUtils.safeMultiply(hoursOffset, 60), minutesOffset);
             }
             offset = FieldUtils.safeMultiply(totalMinutes, DateTimeConstants.MILLIS_PER_MINUTE);
         } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Offset is too large");
         }
         return forOffsetMillis(offset);
    }
