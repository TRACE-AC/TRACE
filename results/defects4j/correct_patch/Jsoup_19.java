private boolean testValidProtocol(Element el, Attribute attr, Set<Protocol> protocols) {
         // try to resolve relative urls to abs, and optionally update the attribute so output html has abs.
         // rels without a baseuri get removed
         String value = el.absUrl(attr.getKey());
         // If absUrl returns an empty string, use the original attribute value (fix for custom protocols like "cid:" or "data:")
         if (value.isEmpty())
             value = attr.getValue(); // use original value for protocol check
         
         if (!preserveRelativeLinks)
             attr.setValue(value);
         
         for (Protocol protocol : protocols) {
             String prot = protocol.toString() + ":";
             if (value.toLowerCase().startsWith(prot)) {
                 return true;
             }
         }
         return false;
}
