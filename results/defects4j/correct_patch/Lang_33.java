public static Class<?>[] toClass(Object[] array) {
    if (array == null) {
        return null;
    } else if (array.length == 0) {
        return ArrayUtils.EMPTY_CLASS_ARRAY;
    }
    Class<?>[] classes = new Class[array.length];
    for (int i = 0; i < array.length; i++) {
        if (array[i] == null) { // Added null check to prevent NullPointerException
            classes[i] = null;
        } else {
            classes[i] = array[i].getClass();
        }
    }
    return classes;
}
