public String get(final String name) {
    if (mapping == null) {
        throw new IllegalStateException(
                 "No header mapping was specified, the record values can't be accessed by name");
    }
    final Integer index = mapping.get(name);
    // Insert Range Checker: Ensure the index is within the bounds of the values array.
    if (index != null && (index.intValue() < 0 || index.intValue() >= values.length)) {
        throw new IllegalArgumentException("Record is inconsistent: index " + index + " is out of bounds");
    }
    return index != null ? values[index.intValue()] : null;
}
