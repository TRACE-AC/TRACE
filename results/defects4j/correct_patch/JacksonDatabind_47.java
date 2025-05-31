public JavaType refineSerializationType(final MapperConfig<?> config,
            final Annotated a, final JavaType baseType) throws JsonMappingException
    {
        JavaType type = baseType;
        final TypeFactory tf = config.getTypeFactory();
        
        // 10-Oct-2015, tatu: For 2.7, we'll need to delegate back to
        //    now-deprecated secondary methods; this because while
        //    direct sub-class not yet retrofitted may only override
        //    those methods. With 2.8 or later we may consider removal
        //    of these methods

        
        // Ok: start by refining the main type itself; common to all types
        Class<?> serClass = findSerializationType(a);
        if (serClass != null) {
            if (type.hasRawClass(serClass)) {
                // 30-Nov-2015, tatu: As per [databind#1023], need to allow forcing of
                //    static typing this way
                type = type.withStaticTyping();
            } else {
                Class<?> currRaw = type.getRawClass();
                if (currRaw.isAssignableFrom(serClass)) { // base type is a super-type of serClass: narrow type
                    try {
                        // Changed: use specialized type when base type is a super-type of serClass
                        type = tf.constructSpecializedType(type, serClass); // swapped from generalized to specialized
                    } catch (IllegalArgumentException iae) {
                        throw new JsonMappingException(null,
                                String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s",
                                        type, serClass.getName(), a.getName(), iae.getMessage()),
                                iae);
                    }
                } else if (serClass.isAssignableFrom(currRaw)) { // base type is a sub-type of serClass: widen type
                    try {
                        // Changed: use generalized type when base type is a sub-type of serClass
                        type = tf.constructGeneralizedType(type, serClass); // swapped from specialized to generalized
                    } catch (IllegalArgumentException iae) {
                        throw new JsonMappingException(null,
                                String.format("Failed to widen type %s with annotation (value %s), from '%s': %s",
                                        type, serClass.getName(), a.getName(), iae.getMessage()),
                                iae);
                    }
                } else {
                    throw new JsonMappingException(null,
                            String.format("Can not refine serialization type %s into %s; types not related",
                                    type, serClass.getName()));
                }
            }
        }
        // Then further processing for container types

        // First, key type (for Maps, Map-like types):
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            Class<?> keyClass = findSerializationKeyType(a, keyType);
            if (keyClass != null) {
                if (keyType.hasRawClass(keyClass)) {
                    keyType = keyType.withStaticTyping();
                } else {
                    Class<?> currRaw = keyType.getRawClass();
                    try {
                        // Changed: swap the condition and corresponding method calls
                        if (currRaw.isAssignableFrom(keyClass)) { // base key type is a super-type of keyClass: narrow
                            keyType = tf.constructSpecializedType(keyType, keyClass);
                        } else if (keyClass.isAssignableFrom(currRaw)) { // base key type is a sub-type of keyClass: widen
                            keyType = tf.constructGeneralizedType(keyType, keyClass);
                        } else {
                            throw new JsonMappingException(null,
                                    String.format("Can not refine serialization key type %s into %s; types not related",
                                            keyType, keyClass.getName()));
                        }
                    } catch (IllegalArgumentException iae) {
                        throw new JsonMappingException(null,
                                String.format("Failed to refine key type of %s with concrete-type annotation (value %s), from '%s': %s",
                                        type, keyClass.getName(), a.getName(), iae.getMessage()),
                                iae);
                    }
                }
                type = ((MapLikeType) type).withKeyType(keyType);
            }
        }

        JavaType contentType = type.getContentType();
        if (contentType != null) { // collection[like], map[like], array, reference
            // And then value types for all containers:
            Class<?> contentClass = findSerializationContentType(a, contentType);
            if (contentClass != null) {
                if (contentType.hasRawClass(contentClass)) {
                    contentType = contentType.withStaticTyping();
                } else {
                    Class<?> currRaw = contentType.getRawClass();
                    try {
                        // Changed: swap the condition and corresponding method calls
                        if (currRaw.isAssignableFrom(contentClass)) { // base content type is a super-type of contentClass: narrow
                            contentType = tf.constructSpecializedType(contentType, contentClass);
                        } else if (contentClass.isAssignableFrom(currRaw)) { // base content type is a sub-type of contentClass: widen
                            contentType = tf.constructGeneralizedType(contentType, contentClass);
                        } else {
                            throw new JsonMappingException(null,
                                    String.format("Can not refine serialization content type %s into %s; types not related",
                                            contentType, contentClass.getName()));
                        }
                    } catch (IllegalArgumentException iae) { // shouldn't really happen
                        throw new JsonMappingException(null,
                                String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s",
                                        type, contentClass.getName(), a.getName(), iae.getMessage()),
                                iae);
                    }
                }
                type = type.withContentType(contentType);
            }
        }
        return type;
    }
