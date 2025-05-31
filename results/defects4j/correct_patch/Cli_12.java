protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption)
    {
        List tokens = new ArrayList();

        boolean eatTheRest = false;

        for (int i = 0; i < arguments.length; i++)
        {
            String arg = arguments[i];

            if ("--".equals(arg))
            {
                eatTheRest = true;
                tokens.add("--");
            }
            else if ("-".equals(arg))
            {
                tokens.add("-");
            }
            else if (arg.startsWith("-"))
            {
                int eqIndex = arg.indexOf('='); // New: check for '=' first
                if (eqIndex != -1)
                {
                    String candidate = arg.substring(0, eqIndex);
                    // If the candidate option (after stripping '-') is recognized, split the token.
                    if (options.hasOption(Util.stripLeadingHyphens(candidate)))
                    {
                        tokens.add(candidate);                     // add option part (e.g. "-foo")
                        tokens.add(arg.substring(eqIndex + 1));      // add value part (e.g. "bar")
                    }
                    else
                    {
                        // Fallback to existing logic if candidate isn't recognized
                        String opt = Util.stripLeadingHyphens(arg);
                        if (options.hasOption(opt))
                        {
                            tokens.add(arg);
                        }
                        else if (arg.length() >= 2 && options.hasOption(arg.substring(0, 2)))
                        {
                            tokens.add(arg.substring(0, 2)); // properties-style: option part (e.g. "-D")
                            tokens.add(arg.substring(2));    // properties-style: remaining part
                        }
                        else
                        {
                            eatTheRest = stopAtNonOption;
                            tokens.add(arg);
                        }
                    }
                }
                else
                {
                    // No '=' present: use the original logic.
                    String opt = Util.stripLeadingHyphens(arg);
                    if (options.hasOption(opt))
                    {
                        tokens.add(arg);
                    }
                    else if (arg.length() >= 2 && options.hasOption(arg.substring(0, 2)))
                    {
                        tokens.add(arg.substring(0, 2)); // properties-style: option part (e.g. "-D")
                        tokens.add(arg.substring(2));    // properties-style: remaining part
                    }
                    else
                    {
                        eatTheRest = stopAtNonOption;
                        tokens.add(arg);
                    }
                }
            }
            else
            {
                tokens.add(arg);
            }

            if (eatTheRest)
            {
                for (i++; i < arguments.length; i++)
                {
                    tokens.add(arguments[i]);
                }
            }
        }

        return (String[]) tokens.toArray(new String[tokens.size()]);
    }
