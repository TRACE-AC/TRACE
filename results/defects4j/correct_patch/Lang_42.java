public void escape(Writer writer, String str) throws IOException {
    int len = str.length();
    int i = 0;
    while (i < len) {
        int codePoint = str.codePointAt(i);
        String entityName = null;
        // Only perform entity lookup for BMP characters; supplementary characters are handled separately.
        if (!Character.isSupplementaryCodePoint(codePoint)) {
            entityName = this.entityName((char) codePoint);
        }
        if (entityName == null) {
            if (codePoint > 0x7F) {
                writer.write("&#");
                writer.write(Integer.toString(codePoint, 10));
                writer.write(';');
            } else {
                writer.write(codePoint);
            }
        } else {
            writer.write('&');
            writer.write(entityName);
            writer.write(';');
        }
        i += Character.charCount(codePoint); // Move to the next code point
    }
}
