package edu.emory.cci.aiw.umls;

/**
 * Represents a SAB dictionary value in a UMLS query, as a name and a
 * description.
 * 
 * @author Michel Mansour
 * 
 */
public final class SAB extends AbstractUMLSSearchUID {
    private String name;
    private String description;

    private SAB(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }

    @Override
    public String getKeyName() {
        return "SAB";
    }

    /**
     * Creates and returns a <code>SABValue</code> with the given string as the
     * name. The description is left blank.
     * 
     * @param name
     *            the name of the SAB dictionary
     * @return a <code>SABValue</code> with the given name
     */
    public static SAB withName(String name) {
        return new SAB(name, "");
    }

    /**
     * Creates and returns a <code>SABValue</code> with the given name and
     * description.
     * 
     * @param name
     *            the name of the SAB dictionary
     * @param description
     *            the description of the dictionary
     * @return a <code>SABValue</code> with the given name and description
     */
    public static SAB withNameAndDescription(String name,
            String description) {
        return new SAB(name, description);
    }

    /**
     * Gets the name of the dictionary
     * 
     * @return the name of the dictionary as a <code>String</code>
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the description of the dictionary
     * 
     * @return the description of the dictionary as a <code>String</code>
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SAB) {
            return this.getValue().equals(((SAB) o).getValue());
        }
        return false;
    }
}
