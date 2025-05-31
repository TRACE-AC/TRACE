public void println() throws IOException {
   final String recordSeparator = format.getRecordSeparator();
   if (recordSeparator != null) { // Check if recordSeparator is not null to avoid appending "null"
       out.append(recordSeparator);
   } else {
       out.append(""); // Append empty string if recordSeparator is null
   }
   newRecord = true;
}
