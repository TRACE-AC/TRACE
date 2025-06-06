static boolean isSimpleNumber(String s) {
     int len = s.length();
     for (int index = 0; index < len; index++) {
       char c = s.charAt(index);
       if (c < '0' || c > '9') {
         return false;
       }
     }
     return len > 0 && (s.charAt(0) != '0' || len == 1); // allow "0" as valid
   }
