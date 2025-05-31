private boolean _hasCustomHandlers(JavaType t) {
    if (t.isContainerType()) {
         // First: value types may have both value and type handlers
         JavaType ct = t.getContentType();
         if (ct != null) {
             if ((ct.getValueHandler() != null) || (ct.getTypeHandler() != null)) { // modified: check for content type's handlers
                 return true;
             }
         }
         // Second: map(-like) types may have a custom key deserializer (not stored as key value handler)
         if (t.isMapLikeType()) { // added branch to check key type
             JavaType kt = t.getKeyType(); // retrieve the key type
             if (kt != null && kt.getValueHandler() != null) { // modified: replaced invalid getKeyDeserializer() call with getValueHandler() check
                 return true;
             }
         }
     }
     return false;
}
