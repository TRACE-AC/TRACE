void writePaxHeaders(String entryName,
                      Map<String, String> headers) throws IOException {
         String entry = stripTo7Bits(entryName); // Get 7-bit entry name
         // Remove any trailing slash(es) to avoid directory interpretation
         while (entry.endsWith("/")) {
             entry = entry.substring(0, entry.length() - 1); // fixed: ensure trailing '/' is removed
         }
         String name = "./PaxHeaders.X/" + entry;
             // TarEntry's constructor would think this is a directory
             // and not allow any data to be written
         if (name.length() >= TarConstants.NAMELEN) {
             name = name.substring(0, TarConstants.NAMELEN - 1);
         }
        TarArchiveEntry pex = new TarArchiveEntry(name,
                                                  TarConstants.LF_PAX_EXTENDED_HEADER_LC);

        StringWriter w = new StringWriter();
        for (Map.Entry<String, String> h : headers.entrySet()) {
            String key = h.getKey();
            String value = h.getValue();
            int len = key.length() + value.length()
                + 3 /* blank, equals and newline */
                + 2 /* guess 9 < actual length < 100 */;
            String line = len + " " + key + "=" + value + "\n";
            int actualLength = line.getBytes(CharsetNames.UTF_8).length;
            while (len != actualLength) {
                // Adjust for cases where length < 10 or > 100
                // or where UTF-8 encoding isn't a single octet
                // per character.
                // Must be in loop as size may go from 99 to 100 in
                // first pass so we'd need a second.
                len = actualLength;
                line = len + " " + key + "=" + value + "\n";
                actualLength = line.getBytes(CharsetNames.UTF_8).length;
            }
            w.write(line);
        }
        byte[] data = w.toString().getBytes(CharsetNames.UTF_8);
        pex.setSize(data.length);
        putArchiveEntry(pex);
        write(data);
        closeArchiveEntry();
}
