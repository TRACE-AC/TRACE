public static DateTimeZone forOffsetHoursMinutes(int hoursOffset, int minutesOffset) throws IllegalArgumentException {
    if (hoursOffset == 0 && minutesOffset == 0) {
        return DateTimeZone.UTC;
    }
    if (hoursOffset < -23 || hoursOffset > 23) {
        throw new IllegalArgumentException("Hours out of range: " + hoursOffset);
    }
    if (minutesOffset < -59 || minutesOffset > 59) { // Adjusted range for negative minutes
        throw new IllegalArgumentException("Minutes out of range: " + minutesOffset);
    }
    if (hoursOffset > 0 && minutesOffset < 0) { // Ensure positive hours do not have negative minutes
        throw new IllegalArgumentException("Minutes cannot be negative when hours are positive.");
    }
    int offset = 0;
    try {
        int hoursInMinutes = hoursOffset * 60;
        // Adjusted subtraction/addition logic for negative hours
        if (hoursOffset < 0) {
            minutesOffset = hoursInMinutes - Math.abs(minutesOffset);
        } else {
            minutesOffset = hoursInMinutes + minutesOffset;
        }
        offset = FieldUtils.safeMultiply(minutesOffset, DateTimeConstants.MILLIS_PER_MINUTE);
    } catch (ArithmeticException ex) {
        throw new IllegalArgumentException("Offset is too large");
    }
    return forOffsetMillis(offset);
}