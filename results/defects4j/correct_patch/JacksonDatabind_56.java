@Override
        protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException
        {
            switch (_kind) {
            case STD_FILE:
                return new File(value);
            case STD_URL:
                return new URL(value);
            case STD_URI:
                return URI.create(value);
            case STD_CLASS:
                try {
                    return ctxt.findClass(value);
                } catch (Exception e) {
                    throw ctxt.instantiationException(_valueClass, ClassUtil.getRootCause(e));
                }
            case STD_JAVA_TYPE:
                return ctxt.getTypeFactory().constructFromCanonical(value);
            case STD_CURRENCY:
                // will throw IAE if unknown:
                return Currency.getInstance(value);
            case STD_PATTERN:
                // will throw IAE (or its subclass) if malformed
                return Pattern.compile(value);
            case STD_LOCALE:
                {
                     value = value.replace('-', '_'); // normalize hyphens to underscores
                     int ix = value.indexOf('_');
                     if (ix < 0) { // single argument
                         return new Locale(value);
                     }
                     String first = value.substring(0, ix);
                     value = value.substring(ix+1);
                     ix = value.indexOf('_');
                     if (ix < 0) { // two pieces
                         return new Locale(first, value);
                     }
                    String second = value.substring(0, ix);
                    return new Locale(first, second, value.substring(ix+1));
                }
            case STD_CHARSET:
                return Charset.forName(value);
            case STD_TIME_ZONE:
                return TimeZone.getTimeZone(value);
            case STD_INET_ADDRESS:
                return InetAddress.getByName(value);
            case STD_INET_SOCKET_ADDRESS:
                if (value.startsWith("[")) {
                    // bracketed IPv6 (with port number)

                    int i = value.lastIndexOf(']');
                    if (i == -1) {
                        throw new InvalidFormatException(ctxt.getParser(),
                                "Bracketed IPv6 address must contain closing bracket",
                                value, InetSocketAddress.class);
                    }

                    int j = value.indexOf(':', i);
                    int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
                    return new InetSocketAddress(value.substring(0, i + 1), port);
                } else {
                    int ix = value.indexOf(':');
                    if (ix >= 0 && value.indexOf(':', ix + 1) < 0) {
                        // host:port
                        int port = Integer.parseInt(value.substring(ix+1));
                        return new InetSocketAddress(value.substring(0, ix), port);
                    }
                    // host or unbracketed IPv6, without port number
                    return new InetSocketAddress(value, 0);
                }
            }
            throw new IllegalArgumentException();
        }
