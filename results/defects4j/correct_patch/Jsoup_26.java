public Document clean(Document dirtyDocument) {
         Validate.notNull(dirtyDocument);
 
         Document clean = Document.createShell(dirtyDocument.baseUri());
         if (dirtyDocument.body() != null) { // Added null pointer check to avoid NullPointerException
             copySafeNodes(dirtyDocument.body(), clean.body());
         }
 
         return clean;
    }
