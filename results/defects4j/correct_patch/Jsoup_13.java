public boolean hasAttr(String attributeKey) {
         Validate.notNull(attributeKey);
         
         // First, check if the literal attribute exists.
         if (attributes.hasKey(attributeKey)) {
             return true;
         }
         
         if (attributeKey.startsWith("abs:")) { // handle abs: prefix case when literal key is missing
             String key = attributeKey.substring(4); // remove "abs:" prefix
             if (attributes.hasKey(key)) { // if underlying attribute exists
                 String abs = absUrl(key); // compute absolute URL for the attribute
                 return abs != null && !abs.isEmpty(); // return true only if non-empty absolute URL
             }
         }
         
         return false;
     }
