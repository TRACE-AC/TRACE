public JsonWriter value(double value) throws IOException {
     writeDeferredName();
     // Only throw exception if not in lenient mode and value is non-finite
     if ((Double.isNaN(value) || Double.isInfinite(value)) && !lenient) {
       throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
     }
     beforeValue();
     out.append(Double.toString(value));
     return this;
}
