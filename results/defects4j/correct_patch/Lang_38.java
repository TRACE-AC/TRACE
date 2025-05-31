public StringBuffer format(Calendar calendar, StringBuffer buf) {
         if (mTimeZoneForced) {
             // Instead of cloning, create a new Calendar in the forced time zone and copy the absolute time
             Calendar newCal = Calendar.getInstance(mTimeZone); // Create new instance with forced time zone
             newCal.setTimeInMillis(calendar.getTimeInMillis()); // Set time using absolute milliseconds to recalc fields correctly
             calendar = newCal; // Use the new calendar instance
         }
        return applyRules(calendar, buf);
}
