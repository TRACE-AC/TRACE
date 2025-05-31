protected JavaType _typeFromId(String id, DatabindContext ctxt) throws IOException
    {
        /* 30-Jan-2010, tatu: Most ids are basic class names; so let's first
         *    check if any generics info is added; and only then ask factory
         *    to do translation when necessary
         */
        TypeFactory tf = ctxt.getTypeFactory();
        if (id.indexOf('<') > 0) {
            // note: may want to try combining with specialization (esp for EnumMap)?
            // 17-Aug-2017, tatu: As per [databind#1735] need to ensure assignment
            //    compatibility -- needed later anyway, and not doing so may open
            //    security issues.
            JavaType t = tf.constructFromCanonical(id);
            // Added compatibility check for generic types as in non-generic branch
            if (!_baseType.getRawClass().isAssignableFrom(t.getRawClass())) {
                throw new IllegalArgumentException("Type " + t + " not subtype of " + _baseType);
            }
            return t;
        }
        Class<?> cls;
        try {
            cls = tf.findClass(id);
        } catch (ClassNotFoundException e) {
            if (ctxt instanceof DeserializationContext) {
                DeserializationContext dctxt = (DeserializationContext) ctxt;
                // First: we may have problem handlers that can deal with it?
                return dctxt.handleUnknownTypeId(_baseType, id, this, "no such class found");
            }
            return null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid type id '" + id + "' (for id type 'Id.class'): " + e.getMessage(), e);
        }
        JavaType specializedType = tf.constructSpecializedType(_baseType, cls); // Construct specialized type
        // Compatibility check: ensure specializedType is a valid subtype of _baseType
        if (!_baseType.getRawClass().isAssignableFrom(specializedType.getRawClass())) {
            throw new IllegalArgumentException("Type " + specializedType + " not subtype of " + _baseType);
        }
        return specializedType;
    }
