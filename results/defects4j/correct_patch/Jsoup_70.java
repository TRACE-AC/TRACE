static boolean preserveWhitespace(Node node) {
    // looks only at this element and five levels up, to prevent recursion & needless stack searches
    int maxDepth = 5;
    int currentDepth = 0;
    while (node != null && node instanceof Element && currentDepth < maxDepth) {
        Element el = (Element) node;
        if (el.tag.preserveWhitespace())
            return true; // returns true if preserveWhitespace is true for this element
        node = el.parent(); // move up one level in the hierarchy
        currentDepth++; // increment the depth counter
    }
    return false; // returns false if no element up to five levels preserves whitespace
}
