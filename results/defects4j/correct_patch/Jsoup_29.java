public String title() {
    // title is a preserve whitespace tag (for document output), but normalised here
    Element titleEl = getElementsByTag("title").first();
    if (titleEl != null) {
        // Using built-in text() method to capture full text with normalization
        return titleEl.text().trim().replaceAll("\\s+", " "); // collapse multiple whitespace to single space
    }
    return "";
}
