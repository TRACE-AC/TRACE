@Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            // Insert primitive type check to handle primitives before calling Class.forName
            if ("boolean".equals(name) || "Z".equals(name)) return boolean.class; // Updated to handle possible primitive signatures
            if ("byte".equals(name) || "B".equals(name)) return byte.class;
            if ("char".equals(name) || "C".equals(name)) return char.class;
            if ("short".equals(name) || "S".equals(name)) return short.class;
            if ("int".equals(name) || "I".equals(name)) return int.class;
            if ("long".equals(name) || "J".equals(name)) return long.class;
            if ("float".equals(name) || "F".equals(name)) return float.class;
            if ("double".equals(name) || "D".equals(name)) return double.class;
            if ("void".equals(name) || "V".equals(name)) return void.class;
            
            try {
                return Class.forName(name, false, classLoader);
            } catch (ClassNotFoundException ex) {
                return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
            }
         }
