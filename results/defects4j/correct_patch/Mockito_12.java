public Class getGenericType(Field field) {        
         Type generic = field.getGenericType();
         if (generic != null && generic instanceof ParameterizedType) {
             Type actual = ((ParameterizedType) generic).getActualTypeArguments()[0];
             // Check if the actual type is directly a Class
             if (actual instanceof Class) {
                 return (Class) actual;
             }
             // If actual is a ParameterizedType, retrieve its raw type
             else if (actual instanceof ParameterizedType) {
                 Type raw = ((ParameterizedType) actual).getRawType();
                 if (raw instanceof Class) {
                     return (Class) raw;
                 }
             }
         }
         
         return Object.class;
}
