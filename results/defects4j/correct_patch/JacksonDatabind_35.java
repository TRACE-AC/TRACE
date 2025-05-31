@SuppressWarnings("resource")
    private final Object _deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        // 02-Aug-2013, tatu: May need to use native type ids
        if (p.canReadTypeId()) {
            Object typeId = p.getTypeId();
            if (typeId != null) {
                return _deserializeWithNativeTypeId(p, ctxt, typeId);
            }
        }
        // first, sanity checks: allow either START_OBJECT or FIELD_NAME tokens
        if (p.getCurrentToken() != JsonToken.START_OBJECT && p.getCurrentToken() != JsonToken.FIELD_NAME) {
            throw ctxt.wrongTokenException(p, JsonToken.START_OBJECT,
                    "need JSON Object to contain As.WRAPPER_OBJECT type information for class " + baseTypeName());
        }
        // if current token is START_OBJECT, advance to FIELD_NAME; otherwise, we're already at FIELD_NAME
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            if (p.nextToken() != JsonToken.FIELD_NAME) {
                throw ctxt.wrongTokenException(p, JsonToken.FIELD_NAME,
                        "need JSON String that contains type id (for subtype of " + baseTypeName() + ")");
            }
        }
        final String typeId = p.getText();
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
        p.nextToken(); // move to the token representing the actual value
        
        // Minor complication: we may need to merge type id in?
        if (_typeIdVisible && p.getCurrentToken() == JsonToken.START_OBJECT) {
            // but what if there's nowhere to add it in? Error? Or skip? For now, skip.
            TokenBuffer tb = new TokenBuffer(null, false);
            tb.writeStartObject(); // recreate START_OBJECT
            tb.writeFieldName(_typePropertyName);
            tb.writeString(typeId);
            p = JsonParserSequence.createFlattened(tb.asParser(p), p);
            p.nextToken();
        }
        
        Object value = deser.deserialize(p, ctxt);
        // And then need the closing END_OBJECT
        if (p.nextToken() != JsonToken.END_OBJECT) {
            throw ctxt.wrongTokenException(p, JsonToken.END_OBJECT,
                    "expected closing END_OBJECT after type information and deserialized value");
        }
        return value;
    }
