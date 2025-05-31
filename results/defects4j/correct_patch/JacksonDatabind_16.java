protected final boolean _add(Annotation ann) {
    if (_annotations == null) {
         _annotations = new HashMap<Class<? extends Annotation>, Annotation>();
    }
    Annotation previous = _annotations.put(ann.annotationType(), ann);
    // Return true if no previous annotation existed or the new annotation overrides the previous one.
    return (previous == null || !previous.equals(ann));
}
