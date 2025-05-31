private static void appendOption(final StringBuffer buff, 
                                     final Option option, 
                                     final boolean required)
    {
        if (!required)
        {
            buff.append("[");
        }

        if (option.getOpt() != null)
        {
            buff.append("-").append(option.getOpt());
        }
        else
        {
            buff.append("--").append(option.getLongOpt());
        }
 
        // Specifically manage empty argument names as per the test requirement
        if (option.hasArg())
        {
            if (option.getArgName() == null || option.getArgName().isEmpty())
            {
                // No placeholder needed according to the test expectation
            }
            else
            {
                buff.append(" <").append(option.getArgName()).append(">");
            }
        }

        // Ensure brackets are only added around optional options
        if (!required)
        {
            buff.append("]"); // Moved brackets placement out of the argument-checking block to apply only for non-required options
        }
    }
