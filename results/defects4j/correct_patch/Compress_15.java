@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ZipArchiveEntry other = (ZipArchiveEntry) obj;
        String myName = getName();
        String otherName = other.getName();
        if (myName == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!myName.equals(otherName)) {
            return false;
        }
         String myComment = getComment();
         String otherComment = other.getComment();
         // Normalize comments: treat null as ""
         myComment = (myComment == null) ? "" : myComment;
         otherComment = (otherComment == null) ? "" : otherComment;
         // Compare normalized comments
         if (!myComment.equals(otherComment)) {
             return false;
         }
         return getTime() == other.getTime()
             && getInternalAttributes() == other.getInternalAttributes()
             && getPlatform() == other.getPlatform()
             && getExternalAttributes() == other.getExternalAttributes()
            && getMethod() == other.getMethod()
            && getSize() == other.getSize()
            && getCrc() == other.getCrc()
            && getCompressedSize() == other.getCompressedSize()
            && Arrays.equals(getCentralDirectoryExtra(),
                             other.getCentralDirectoryExtra())
            && Arrays.equals(getLocalFileDataExtra(),
                             other.getLocalFileDataExtra())
            && gpb.equals(other.gpb);
    }
