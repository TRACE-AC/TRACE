private Map<String, Integer> initializeHeader() throws IOException {
        Map<String, Integer> hdrMap = null;
        final String[] formatHeader = this.format.getHeader();
        if (formatHeader != null) {
            hdrMap = new LinkedHashMap<String, Integer>();

            String[] headerRecord = null;
            if (formatHeader.length == 0) {
                // read the header from the first line of the file
                final CSVRecord nextRecord = this.nextRecord();
                if (nextRecord != null) {
                    headerRecord = nextRecord.values();
                }
            } else {
                if (this.format.getSkipHeaderRecord()) {
                    this.nextRecord();
                }
                headerRecord = formatHeader;
            }

            // build the name to index mappings
            if (headerRecord != null) {
                 for (int i = 0; i < headerRecord.length; i++) {
                     // Use a mutable variable to allow substitution if header is null.
                     String effectiveHeader = headerRecord[i];
                     if (effectiveHeader == null) {
                         effectiveHeader = ""; // always substitute default for null header values
                     }
                     final boolean containsHeader = hdrMap.containsKey(effectiveHeader);
                     final boolean emptyHeader = effectiveHeader.trim().isEmpty();
                     if (containsHeader && (!emptyHeader || (emptyHeader && !this.format.getIgnoreEmptyHeaders()))) {
                         throw new IllegalArgumentException("The header contains a duplicate name: \"" + effectiveHeader +
                                 "\" in " + Arrays.toString(headerRecord));
                     }
                     hdrMap.put(effectiveHeader, Integer.valueOf(i));
                 }
            }
        }
        return hdrMap;
    }
