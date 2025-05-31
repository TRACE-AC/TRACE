protected void registerTypeVariablesOn(Type classType) {
    if (!(classType instanceof ParameterizedType)) {
        return;
    }
    ParameterizedType parameterizedType = (ParameterizedType) classType;
    TypeVariable[] typeParameters = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    for (int i = 0; i < actualTypeArguments.length; i++) {
        TypeVariable typeParameter = typeParameters[i];
        Type actualTypeArgument = actualTypeArguments[i];
        
        // Guard against self-referencing type variables to prevent infinite recursion
        if (actualTypeArgument == typeParameter) { 
            continue; // Skip registration if self-referencing
        }
        
        if (actualTypeArgument instanceof WildcardType) {
            contextualActualTypeParameters.put(typeParameter, boundsOf((WildcardType) actualTypeArgument));
        } else {
            contextualActualTypeParameters.put(typeParameter, actualTypeArgument);
        }
        // logger.log("For '" + parameterizedType + "' found type variable : { '" + typeParameter + "(" + System.identityHashCode(typeParameter) + ")" + "' : '" + actualTypeArgument + "(" + System.identityHashCode(typeParameter) + ")" + "' }");
    }
}
