private boolean isShortOption(String token)
     {
         // short options (-S, -SV, -S=V, -SV1=V2, -S1S2)
         String opt = token.startsWith("-") 
                      ? (token.contains("=") ? token.substring(1, token.indexOf("=")) : token.substring(1))  // extract option identifier correctly
                      : "";
         return token.startsWith("-") && token.length() >= 2 && options.hasShortOption(opt);
         // removed incorrect fixed-length extraction
     }
