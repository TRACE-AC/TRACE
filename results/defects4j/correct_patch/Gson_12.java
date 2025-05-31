@Override public void skipValue() throws IOException {
    if (peek() == JsonToken.NAME) {
      nextName();
      if (stackSize > 1) {
        pathNames[stackSize - 2] = "null"; // safe assignment when stackSize > 1
      } else if (stackSize > 0) { 
        pathNames[stackSize - 1] = "null"; // adjusted for stackSize == 1 to avoid index -1
      }
    } else {
      popStack();
      if (stackSize > 0) { // range check to avoid negative index after popStack()
        pathNames[stackSize - 1] = "null";
      }
    }
    if (stackSize > 0) { // range check before updating pathIndices
      pathIndices[stackSize - 1]++;
    }
}
