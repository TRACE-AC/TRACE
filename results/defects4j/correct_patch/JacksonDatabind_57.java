public <T> MappingIterator<T> readValues(byte[] src, int offset, int length)
        throws IOException, JsonProcessingException
    {
         // Verify offset and length are within bounds
         if (offset < 0 || length < 0 || offset + length > src.length) {
             throw new IllegalArgumentException("Invalid offset and length");
         }
         if (_dataFormatReaders != null) {
             return _detectBindAndReadValues(_dataFormatReaders.findFormat(src, offset, length), false);
         }
         // Modified: use offset and length to create a parser for the subarray
         return _bindAndReadValues(_considerFilter(_parserFactory.createParser(src, offset, length), 
                 true));
     }
