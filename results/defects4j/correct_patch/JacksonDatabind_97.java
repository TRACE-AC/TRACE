@Override
    public final void serialize(JsonGenerator gen, SerializerProvider ctxt) throws IOException
    {
        if (_value == null) {
            ctxt.defaultSerializeNull(gen);
        } else if (_value instanceof JsonSerializable) {
            ((JsonSerializable) _value).serialize(gen, ctxt);
         } else {
             // 25-May-2018, tatu: [databind#1991] changed to use SerializerProvider for proper context propagation
             ctxt.defaultSerializeValue(_value, gen); // fixed: Delegate serialization to SerializerProvider
         }
     }
