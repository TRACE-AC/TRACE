@Override
  public void matchConstraint(JSType constraint) {
    // We only want to match constraints on anonymous types.
    if (hasReferenceName()) {
      return;
    }

    // Handle the case where the constraint object is a record type.
    // Also handle union record types by iterating through union alternatives.
    if (constraint.isRecordType()) {
      matchRecordTypeConstraint(constraint.toObjectType());
    } else if (constraint.isUnionType()) { // Added branch to check for union types.
      for (JSType alt : constraint.toMaybeUnionType().getAlternates()) { // Iterate over union alternatives.
        if (alt.isRecordType()) { // If the alternative is a record type.
          matchRecordTypeConstraint(alt.toObjectType()); // Apply record type constraint.
        }
      }
    }
  }
