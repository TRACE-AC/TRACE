@SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        // 22-Sep-2012, tatu: For 2.1, use this new method, may force coercion:
        String text = p.getValueAsString();
        if (text != null) { // has String representation
            if (text.length() == 0 || (text = text.trim()).length() == 0) {
                // 04-Feb-2013, tatu: Usually should become null; but not always
                return _deserializeFromEmptyString();
            }
            try {
                T result = _deserialize(text, ctxt); // changed: removed null check to allow valid null
                return result; // return result even if it's null
            } catch (IllegalArgumentException iae) {
                 Exception cause = iae;
                 String msg = "not a valid textual representation";
                 String m2 = iae.getMessage();
                 if (m2 != null) {
                     msg = msg + ", problem: " + m2;
                 }
                 JsonMappingException e = ctxt.weirdStringException(text, _valueClass, msg);
                 e.initCause(cause);
                 throw e;
            } catch (MalformedURLException me) {
                Exception cause = me;
                String msg = "not a valid textual representation";
                String m2 = me.getMessage();
                if (m2 != null) {
                    msg = msg + ", problem: " + m2;
                }
                JsonMappingException e = ctxt.weirdStringException(text, _valueClass, msg);
                e.initCause(cause);
                throw e;
            }
            // Removed unreachable code that always threw an exception.
        }
        JsonToken t = p.getCurrentToken();
        // [databind#381]
        if (t == JsonToken.START_ARRAY) {
            return _deserializeFromArray(p, ctxt);
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            // Trivial cases; null to null, instance of type itself returned as is
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (_valueClass.isAssignableFrom(ob.getClass())) {
                return (T) ob;
            }
            return _deserializeEmbedded(ob, ctxt);
        }
        return (T) ctxt.handleUnexpectedToken(_valueClass, p);
    }
