public XYDataItem addOrUpdate(Number x, Number y) {
         if (x == null) {
             throw new IllegalArgumentException("Null 'x' argument.");
         }
 
         XYDataItem overwritten = null;
         int index = indexOf(x);
         if (index >= 0 && !this.allowDuplicateXValues) {
             XYDataItem existing = (XYDataItem) this.data.get(index);
             try {
                 overwritten = (XYDataItem) existing.clone();
             }
             catch (CloneNotSupportedException e) {
                 throw new SeriesException("Couldn't clone XYDataItem!");
             }
             existing.setY(y);
         }
         else {
             // For duplicate x-values allowed or when no matching item is found,
             // ignore binary search result and simply add the new item.
             if (this.autoSort && !this.allowDuplicateXValues) { // Modified condition to avoid negative index when duplicates are allowed.
                 this.data.add(-index - 1, new XYDataItem(x, y));
             }
             else {
                 this.data.add(new XYDataItem(x, y));
             }
             // check if this addition will exceed the maximum item count...
             if (getItemCount() > this.maximumItemCount) {
                 this.data.remove(0);
             }
         }
         fireSeriesChanged();
         return overwritten;
    }
