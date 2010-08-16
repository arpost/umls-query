package edu.emory.cci.aiw.umls;

public final class SABValue extends AbstractUMLSSearchUID {
    private String name;
    private String description;

    private SABValue(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }

    public String getKeyName() {
        return "SAB";
    }

    public static SABValue withName(String name) {
        return new SABValue(name, "");
    }

    public static SABValue withNameAndDescription(String name,
            String description) {
        return new SABValue(name, description);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return getName();
    }

    public boolean equals(Object o) {
        if (o instanceof SABValue) {
            return this.getValue().equals(((SABValue) o).getValue());
        }
        return false;
    }
}
