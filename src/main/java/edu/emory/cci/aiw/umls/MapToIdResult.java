package edu.emory.cci.aiw.umls;

/**
 * Container class for the result of a mapToId UMLS query, via one of the
 * mapToId* methods in the API. It contains a unique identifier and the string
 * value it mapped from.
 * 
 * @author Michel Mansour
 * 
 * @param <T>
 *            the type of the unique identifier, which must implement the
 *            <code>MapToIdQuerySearchUID</code> interface
 */
public final class MapToIdResult<T extends MapToIdQuerySearchUID> {
    private final T uid;
    private final UMLSQueryStringValue str;

    private MapToIdResult(T uid, UMLSQueryStringValue str) {
        this.uid = uid;
        this.str = str;
    }

    /**
     * Gets the unique identifier
     * @return unique identifier of type <code>T</code>
     */
    public T getUid() {
        return this.uid;
    }

    /**
     * Gets the string value
     * @return a <code>UMLSQueryStringValue</code>
     */
    public UMLSQueryStringValue getStr() {
        return this.str;
    }

    static <U extends MapToIdQuerySearchUID> MapToIdResult<U> fromUidAndStr(
            U uid, UMLSQueryStringValue str) {
        return new MapToIdResult<U>(uid, str);
    }

    @Override
    public String toString() {
        return uid.getKeyName() + ": " + uid.getValue() + "\t"
                + str.getKeyName() + ": " + str.getValue();
    }

    @Override
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

    @Override
    public int hashCode() {
        return (uid.getValue() + str.getValue()).hashCode();
    }
}
