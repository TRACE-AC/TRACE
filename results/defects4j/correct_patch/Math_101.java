public Complex parse(String source, ParsePosition pos) {
    int initialIndex = pos.getIndex();

    // parse whitespace
    parseAndIgnoreWhitespace(source, pos);

    // parse real
    Number re = parseNumber(source, getRealFormat(), pos);
    if (re == null) {
        // invalid real number
        pos.setErrorIndex(pos.getIndex()); // added to record error position
        pos.setIndex(initialIndex);
        return null;
    }

    // parse sign
    int startIndex = pos.getIndex();
    char c = parseNextCharacter(source, pos);
    int sign = 0;
    switch (c) {
    case 0:
        // no sign
        // return real only complex number
        return new Complex(re.doubleValue(), 0.0);
    case '-':
        sign = -1;
        break;
    case '+':
        sign = 1;
        break;
    default:
        // invalid sign
        pos.setIndex(initialIndex);
        pos.setErrorIndex(startIndex);
        return null;
    }

    // parse whitespace
    parseAndIgnoreWhitespace(source, pos);

    // parse imaginary
    Number im = parseNumber(source, getRealFormat(), pos);
    if (im == null) {
        // invalid imaginary number
        pos.setErrorIndex(pos.getIndex()); // added to record error position
        pos.setIndex(initialIndex);
        return null;
    }

    // parse imaginary character
    int n = getImaginaryCharacter().length();
    startIndex = pos.getIndex();
    int endIndex = startIndex + n;
    // Insert range check: ensure source has enough characters before substring call
    if (source.length() < endIndex) {
        pos.setIndex(initialIndex);
        pos.setErrorIndex(startIndex);
        return null;
    }
    if (source.substring(startIndex, endIndex).compareTo(getImaginaryCharacter()) != 0) {
        // set index back to initial, error index should be the start index character examined.
        pos.setIndex(initialIndex);
        pos.setErrorIndex(startIndex);
        return null;
    }
    pos.setIndex(endIndex);

    return new Complex(re.doubleValue(), im.doubleValue() * sign);
}
