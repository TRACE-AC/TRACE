@Override
     public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
         String str;
         
         if (value instanceof Date) {
             provider.defaultSerializeDateKey((Date) value, jgen);
             return;
         } else if (value instanceof Class) {
             str = ((Class<?>) value).getName(); // Handle Class objects to avoid the "class " prefix
         } else {
             str = value.toString();
         }
        jgen.writeFieldName(str);
    }
