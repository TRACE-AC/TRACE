@Override
public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
    int max = longest;
    if (index + longest > input.length()) {
        max = input.length() - index;
    }
    // descend so as to get a greedy algorithm
    for (int i = max; i >= shortest; i--) {
        // Convert the candidate substring to a String for content-based comparison
        final String key = input.subSequence(index, index + i).toString(); // modified: enforce String conversion
        CharSequence result = null;
        // Iterate over lookupMap keys, comparing their String representation to the candidate key
        for (CharSequence lookupKey : lookupMap.keySet()) { // modified: iterate over keys
            if (lookupKey.toString().equals(key)) { // use content equality
                result = lookupMap.get(lookupKey);
                break;
            }
        }
        if (result != null) {
            out.write(result.toString());
            return i;
        }
    }
    return 0;
}
