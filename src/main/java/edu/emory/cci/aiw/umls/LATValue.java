package edu.emory.cci.aiw.umls;

public final class LATValue extends AbstractUMLSSearchUID {
    private char lat1;
    private char lat2;
    private char lat3;

    private LATValue(char lat1, char lat2, char lat3) {
        super(new StringBuilder().append(lat1).append(lat2).append(lat3)
                .toString());
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.lat3 = lat3;
    }

    private LATValue(String latStr) {
        this(latStr.charAt(0), latStr.charAt(1), latStr.charAt(2));
    }

    public String getKeyName() {
        return "LAT";
    }

    public boolean equals(Object o) {
        if (o instanceof LATValue) {
            return this.getValue().equals(((LATValue) o).getValue());
        }
        return false;
    }

    public static LATValue fromString(String latStr)
            throws MalformedUMLSUniqueIdentifierException {
        if (latStr.length() != 3) {
            throw new MalformedUMLSUniqueIdentifierException(
                    "LAT values must be exactly 3 characters");
        } else {
            return new LATValue(latStr);
        }
    }

    public String toString() {
        return new StringBuilder().append(lat1).append(lat2).append(lat3)
                .toString();
    }
}
