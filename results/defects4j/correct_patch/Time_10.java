protected static int between(ReadablePartial start, ReadablePartial end, ReadablePeriod zeroInstance) {
    if (start == null || end == null) {
        throw new IllegalArgumentException("ReadablePartial objects must not be null");
    }
    if (start.size() != end.size()) {
        throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
    }
    for (int i = 0, isize = start.size(); i < isize; i++) {
        if (start.getFieldType(i) != end.getFieldType(i)) {
            throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
        }
    }
    if (DateTimeUtils.isContiguous(start) == false) {
         throw new IllegalArgumentException("ReadablePartial objects must be contiguous");
     }
     Chronology chrono = DateTimeUtils.getChronology(start.getChronology()).withUTC();
     // Use a safe reference instant (2000-01-01T00:00:00Z) instead of hardcoding 0L,
     // ensuring that partials like MonthDay get a valid context (e.g., 2000 is a leap year)
     long safeReferenceInstant = 946684800000L; // January 1, 2000 in UTC
     int[] values = chrono.get(zeroInstance, chrono.set(start, safeReferenceInstant), chrono.set(end, safeReferenceInstant));
     return values[0];
}
