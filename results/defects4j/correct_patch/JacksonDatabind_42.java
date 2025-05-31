@Override
protected Object _deserializeFromEmptyString() throws IOException {
    // As per [databind#398], URI requires special handling
    if (_kind == STD_URI) {
         return URI.create("");
     }
     // Check for Locale type to return Locale.ROOT for empty strings
     if (_kind == STD_LOCALE) { // Added check for Locale type
         return Locale.ROOT; // Return Locale.ROOT for empty string
     }
     return super._deserializeFromEmptyString();
}
