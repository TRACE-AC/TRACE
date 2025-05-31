public List<String> getMatchingOptions(String opt)
    {
        opt = Util.stripLeadingHyphens(opt);
        
         List<String> matchingOpts = new ArrayList<String>();
 
         // first check for an exact match to avoid ambiguous options
         if (longOpts.containsKey(opt)) { // added exact match check
             matchingOpts.add(opt);
             return matchingOpts; // return immediately if exact match found
         }
 
         // if no exact match, collect options that start with the partial name
         for (String longOpt : longOpts.keySet())
         {
            if (longOpt.startsWith(opt))
            {
                matchingOpts.add(longOpt);
            }
         }
        
         return matchingOpts;
    }
