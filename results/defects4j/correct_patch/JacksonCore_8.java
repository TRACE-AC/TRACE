public char[] getTextBuffer()
    {
        // Are we just using shared input buffer?
        if (_inputStart >= 0 && _inputBuffer != null) return _inputBuffer; // Ensure _inputBuffer is not null
        if (_resultArray != null) return _resultArray;
        if (_resultString != null) {
            _resultArray = _resultString.toCharArray(); // Create array from string if not null
            return _resultArray;
        }
        // Nope; but does it fit in just one segment?
        if (!_hasSegments && _currentSegment != null) return _currentSegment; // Ensure _currentSegment is not null
        // Nope, need to have/create a non-segmented array and return it
        return contentsAsArray();
    }
