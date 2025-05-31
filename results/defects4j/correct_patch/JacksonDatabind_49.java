public Object generateId(Object forPojo) {
    // 04-Jun-2016, tatu: As per [databind#1255], need to consider possibility of
    //    id being generated for "alwaysAsId", but not being written as POJO; regardless,
    //    need to use existing id if there is one:
    if (this.id != null) { // Inserted 'this.' qualifier to reference the class field id.
        return this.id;  // Return the existing id to avoid unresolved forward reference error.
    }
    this.id = generator.generateId(forPojo); // generate new id if none exists
    return this.id;
}
