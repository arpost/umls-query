package edu.emory.cci.aiw.umls;

final class MapToIdResult<T extends MapToIdQuerySearchUID> {
    private final T uid;
    private final UMLSQueryStringValue str;

    private MapToIdResult(T uid, UMLSQueryStringValue str) {
        this.uid = uid;
        this.str = str;
    }

    T getUid() {
        return this.uid;
    }

    UMLSQueryStringValue getStr() {
        return this.str;
    }

    static <U extends MapToIdQuerySearchUID> MapToIdResult<U> fromUidAndStr(
            U uid, UMLSQueryStringValue str) {
        return new MapToIdResult<U>(uid, str);
    }

    public String toString() {
        return uid.getKeyName() + ": " + uid.getValue() + "\t"
                + str.getKeyName() + ": " + str.getValue();
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        return uid.equals(((MapToIdResult<?>) o).getUid())
                && str.equals(((MapToIdResult<?>) o).getStr());

    }

    public int hashCode() {
        return (uid.getValue() + str.getValue()).hashCode();
    }
}
