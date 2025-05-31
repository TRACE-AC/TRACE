static String stripLeadingHyphens(String str)
{
    if (str == null) return null; // added null check to prevent NullPointerException
    if (str.startsWith("--"))
    {
         return str.substring(2, str.length());
    }
    else if (str.startsWith("-"))
    {
         return str.substring(1, str.length());
    }
    return str;
}
