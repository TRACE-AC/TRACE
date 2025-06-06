public int parseInto(DateTimeParserBucket bucket, String text, int position) {
    String str = text.substring(position);
    String bestMatch = null;
    for (String id : ALL_IDS) {
        if (str.startsWith(id)) {
            // Select the longest matching zone id to handle cases like "America/Dawson_Creek"
            if (bestMatch == null || id.length() > bestMatch.length()) {
                bestMatch = id;
            }
        }
    }
    if (bestMatch != null) {
        bucket.setZone(DateTimeZone.forID(bestMatch));
        return position + bestMatch.length();
    }
    return ~position;
}
