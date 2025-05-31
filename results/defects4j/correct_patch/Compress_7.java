public static String parseName(byte[] buffer, final int offset, final int length) {
        StringBuffer result = new StringBuffer(length);
         int          end = offset + length;
 
         for (int i = offset; i < end; ++i) {
             if (buffer[i] == 0) {
                 break;
             }
             result.append((char)(buffer[i] & 0xFF)); // fixed: interpret byte as unsigned
         }
 
         return result.toString();
    }
