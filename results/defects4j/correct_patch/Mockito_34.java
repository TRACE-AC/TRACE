public void captureArgumentsFrom(Invocation i) {
         int k = 0;
         Object[] args = i.getArguments(); // Added: store arguments for range check
         for (Matcher m : matchers) {
             if (m instanceof CapturesArguments && k < args.length) { // Insert Range Checker: only capture if index is valid
                 ((CapturesArguments) m).captureFrom(args[k]);
             }
             k++;
        }
}
