public static Locale toLocale(String str) {
    if (str == null) {
        return null;
    }
    int len = str.length();
    if (len != 2 && len != 5 && len < 7) {
        throw new IllegalArgumentException("Invalid locale format: " + str);
    }
    char ch0 = str.charAt(0);
    char ch1 = str.charAt(1);
    if (ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z') {
        throw new IllegalArgumentException("Invalid locale format: " + str);
    }
    if (len == 2) {
        return new Locale(str, "");
    } else {
        if (str.charAt(2) != '_') {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        // Find the position of the second underscore to separate country and variant
        int posSecondUnderscore = str.indexOf('_', 3);
        if (posSecondUnderscore == -1) {
            // No variant provided; country part is from index 3 to end.
            String country = str.substring(3);
            if (!country.isEmpty() && (country.length() != 2 ||
                country.charAt(0) < 'A' || country.charAt(0) > 'Z' ||
                country.charAt(1) < 'A' || country.charAt(1) > 'Z')) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            return new Locale(str.substring(0, 2), country);
        } else {
            // Variant is provided.
            String country = str.substring(3, posSecondUnderscore);
            String variant = str.substring(posSecondUnderscore + 1);
            if (!country.isEmpty() && (country.length() != 2 ||
                country.charAt(0) < 'A' || country.charAt(0) > 'Z' ||
                country.charAt(1) < 'A' || country.charAt(1) > 'Z')) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            return new Locale(str.substring(0, 2), country, variant);
        }
    }
}
