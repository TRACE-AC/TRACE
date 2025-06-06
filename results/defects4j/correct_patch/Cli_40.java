@SuppressWarnings("unchecked") // returned value will have type T because it is fixed by clazz
public static <T> T createValue(final String str, final Class<T> clazz) throws ParseException
{
    if (PatternOptionBuilder.STRING_VALUE == clazz)
    {
        return (T) str;
    }
    else if (PatternOptionBuilder.OBJECT_VALUE == clazz)
    {
        return (T) createObject(str);
    }
    else if (PatternOptionBuilder.NUMBER_VALUE == clazz)
    {
        return (T) createNumber(str);
    }
    else if (PatternOptionBuilder.DATE_VALUE == clazz)
    {
        return (T) createDate(str);
    }
    else if (PatternOptionBuilder.CLASS_VALUE == clazz)
    {
        return (T) createClass(str);
    }
    else if (PatternOptionBuilder.FILE_VALUE == clazz)
    {
        return (T) createFile(str);
    }
    else if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz)
    {
        return (T) openFile(str);
    }
    else if (PatternOptionBuilder.FILES_VALUE == clazz)
    {
        return (T) createFiles(str);
    }
    else if (PatternOptionBuilder.URL_VALUE == clazz)
    {
        return (T) createURL(str);
    }
    else
    {
        throw new ParseException("Unsupported type: " + clazz.getName()); // changed from returning null to throwing exception
    }
}
