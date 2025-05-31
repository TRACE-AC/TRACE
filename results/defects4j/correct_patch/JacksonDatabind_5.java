protected void _addMethodMixIns(Class<?> targetClass, AnnotatedMethodMap methods,
            Class<?> mixInCls, AnnotatedMethodMap mixIns)
{
    List<Class<?>> parents = new ArrayList<Class<?>>();
    parents.add(mixInCls);
    ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
    for (Class<?> mixin : parents) {
        for (Method m : mixin.getDeclaredMethods()) {
            if (!_isIncludableMemberMethod(m)) {
                continue;
            }
            AnnotatedMethod am = methods.find(m);
            /* Do we already have a method to augment (from sub-class
             * that will mask this mixIn)? If so, add if visible
             * without masking (no such annotation)
             */
            if (am != null) {
                _addMixUnders(m, am);
                /* Otherwise will have precedence, but must wait
                 * until we find the real method (mixIn methods are
                 * just placeholder, can't be called)
                 */
            } else {
                // Insert missing merge step for multi-level merge of mixins
                AnnotatedMethod existingMixIn = mixIns.find(m); // check if placeholder exists
                if (existingMixIn != null) {
                    _addMixUnders(m, existingMixIn); // merge annotations from current mixin into existing placeholder
                } else {
                    mixIns.add(_constructMethod(m));
                }
            }
        }
    }
}
