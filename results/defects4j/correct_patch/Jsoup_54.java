private void copyAttributes(org.jsoup.nodes.Node source, Element el) {
    for (Attribute attribute : source.attributes()) {
        // valid xml attribute names are: ^[a-zA-Z_:][-a-zA-Z0-9_:.]
        String key = attribute.getKey().replaceAll("[^-a-zA-Z0-9_:.]", "");
        // Added check: if key is empty or does not start with a valid character, skip setting the attribute.
        if (key.isEmpty() || !key.matches("^[a-zA-Z_:].*")) {
            continue; // Skip invalid attribute names to prevent DOMException
        }
        el.setAttribute(key, attribute.getValue());
    }
}
