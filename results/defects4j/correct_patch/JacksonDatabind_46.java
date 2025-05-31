@Override
    public StringBuilder getGenericSignature(StringBuilder sb)
    {
         _classSignature(_class, sb, false);
         sb.append('<');
         sb = _referencedType.getGenericSignature(sb);
         sb.append('>'); // Inserted missing closing '>' to complete the generic signature
         sb.append(';'); // Inserted missing terminator to complete the signature as expected
         return sb;
    }
