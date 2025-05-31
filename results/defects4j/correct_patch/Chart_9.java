public TimeSeries createCopy(RegularTimePeriod start, RegularTimePeriod end)
        throws CloneNotSupportedException {

        if (start == null) {
            throw new IllegalArgumentException("Null 'start' argument.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Null 'end' argument.");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(
                    "Requires start on or before end.");
        }
        boolean emptyRange = false;
        int startIndex = getIndex(start);
        if (startIndex < 0) {
            startIndex = -(startIndex + 1);
            if (startIndex == this.data.size()) {
                emptyRange = true;  // start is after last data item
            }
        }
        int endIndex = getIndex(end);
        if (endIndex < 0) {             // end period is not in original series
             int insertionPoint = -(endIndex + 1); // first item AFTER end period
             if (insertionPoint - 1 >= startIndex) {
                 endIndex = insertionPoint - 1;    // use last item BEFORE end
             } else {
                 emptyRange = true; // adjusted endIndex would be before startIndex
             }
         }
         if (endIndex < 0) {
             emptyRange = true;
         }
         if (!emptyRange && endIndex < startIndex) {
             emptyRange = true; // additional safety check
         }
         if (emptyRange) {
            TimeSeries copy = (TimeSeries) super.clone();
            copy.data = new java.util.ArrayList();
            return copy;
        }
        else {
            return createCopy(startIndex, endIndex);
        }

}
