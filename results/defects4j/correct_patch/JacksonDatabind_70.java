public void remove(SettableBeanProperty propToRm)
    {
        ArrayList<SettableBeanProperty> props = new ArrayList<SettableBeanProperty>(_size);
        String key = getPropertyName(propToRm); // key is already normalized to lower-case
        boolean found = false;

        for (int i = 1, end = _hashArea.length; i < end; i += 2) {
            // Retrieve normalized key from key slot (i-1)
            String slotKey = (String) _hashArea[i - 1];
            SettableBeanProperty prop = (SettableBeanProperty) _hashArea[i];
            if (prop == null) {
                continue;
            }
            if (!found) {
                 // 09-Jan-2017, tatu: Important: must check name slot and NOT property name,
                 // as only the key slot is lower-case for case-insensitive matching.
                 found = key.equals(slotKey); // Changed to compare normalized keys directly
                 if (found) {
                     // Need to leave a hole here
                     _propsInOrder[_findFromOrdered(prop)] = null;
                     continue;
                 }
            }
            props.add(prop);
        }
        if (!found) {
            throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove");
        }
        init(props);
    }
