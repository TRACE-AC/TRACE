<M extends Map<String, String>> M putIn(final M map) {
    for (final Entry<String, Integer> entry : mapping.entrySet()) {
        final int col = entry.getValue().intValue();
        // Skip mapping entries with invalid column indexes to prevent ArrayIndexOutOfBoundsException
        if (col < 0 || col >= values.length) {
            continue;
        }
        map.put(entry.getKey(), values[col]);
    }
    return map;
}
