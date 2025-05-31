public BaseSettings withDateFormat(DateFormat df) {
         if (_dateFormat == df) {
             return this;
         }
         // Always use the preconfigured _timeZone regardless of df's timezone
         TimeZone tz = _timeZone;
         return new BaseSettings(_classIntrospector, _annotationIntrospector, _visibilityChecker, _propertyNamingStrategy, _typeFactory,
                 _typeResolverBuilder, df, _handlerInstantiator, _locale,
                 tz, _defaultBase64);
     }
