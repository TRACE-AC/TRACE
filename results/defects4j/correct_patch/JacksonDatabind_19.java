private JavaType _mapType(Class<?> rawClass)
{
    // 28-May-2015, tatu: Properties are special, as per [databind#810]
    JavaType[] typeParams = findTypeParameters(rawClass, Map.class);
    // ok to have no types ("raw")
    if (typeParams == null) {
        // Ensure handling of Properties with String key-value pairs
        if (rawClass.equals(Properties.class)) {
            return MapType.construct(rawClass, TypeFactory.defaultInstance().constructType(String.class), TypeFactory.defaultInstance().constructType(String.class));
        }
        return MapType.construct(rawClass, _unknownType(), _unknownType());
    }
    // but exactly 2 types if any found
    if (typeParams.length != 2) {
        throw new IllegalArgumentException("Strange Map type "+rawClass.getName()+": cannot determine type parameters");
    }

    // Ensure both keys and values are Strings for Properties
    if (rawClass.equals(Properties.class)) {
        return MapType.construct(rawClass, TypeFactory.defaultInstance().constructType(String.class), TypeFactory.defaultInstance().constructType(String.class));
    }

    return MapType.construct(rawClass, typeParams[0], typeParams[1]);
}
