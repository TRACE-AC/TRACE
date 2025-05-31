public XmlDeclaration asXmlDeclaration() {
         String data = getData();
         Document doc = Jsoup.parse("<" + data.substring(1, data.length() - 1) + ">", baseUri(), Parser.xmlParser());
         XmlDeclaration decl = null;
         if (doc.children().size() > 0) { // Changed guard: check element children instead of all child nodes
             Element el = doc.children().get(0); // Safely retrieve the first element child
             decl = new XmlDeclaration(NodeUtils.parser(doc).settings().normalizeTag(el.tagName()), data.startsWith("!"));
             decl.attributes().addAll(el.attributes());
         }
         return decl;
}
