void read(Tokeniser t, CharacterReader r) {
    if (r.matchesLetter()) {
         String name = r.consumeLetterSequence();
         t.tagPending.appendTagName(name.toLowerCase());
         t.dataBuffer.append(name);
         // Removed unconditional r.advance() to avoid losing significant characters in the script
         return;
     }

    if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
         char c = r.consume();
         switch (c) {
             case '\t':
             case '\n':
             case '\f':
             case ' ':
                 t.transition(BeforeAttributeName);
                 break;
             case '/':
                 t.transition(SelfClosingStartTag);
                 break;
             case '>':
                 t.emitTagPending();
                 t.transition(Data);
                 break;
             default:
                 t.dataBuffer.append(c);
                 anythingElse(t, r);
                 break;
         }
     } else {
         anythingElse(t, r);
     }
}
