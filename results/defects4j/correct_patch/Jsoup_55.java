void read(Tokeniser t, CharacterReader r) {
    char c = r.consume();
    switch (c) {
        case '>':
            t.tagPending.selfClosing = true;
            t.emitTagPending();
            t.transition(Data);
            break;
        case eof:
            t.eofError(this);
            t.transition(Data);
            break;
        default:
            t.error(this);
            r.unconsume(); // Reconsume the character to avoid dropping the last character
            t.transition(BeforeAttributeName);
    }
}
