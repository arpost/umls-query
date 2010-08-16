package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an Concept Unique Identifier (CUI) in a UMLS query. CUIs must
 * match the regular expression <code>C\d{7}</code> (the letter 'C' followed by
 * 7 digits). Implements several marker interfaces that allow objects of this
 * type to be used as a search key in various UMLS queries.
 * 
 * @author Michel Mansour
 * 
 */
public final class ConceptUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, ParentsQuerySearchUID,
        NeighborQuerySearchUID, MapToIdQuerySearchUID {

    private static Pattern cuidPattern;

    /*
     * An empty CUI, used only within this package when only the key name is
     * required.
     */
    static final ConceptUID EMPTY_CUI = new ConceptUID("");

    /*
     * the regex all CUIs must match
     */
    static {
        cuidPattern = Pattern.compile("C\\d{7}");
    }

    private ConceptUID(String cuid) {
        super(cuid);
    }

    /**
     * Creates and returns a new <code>ConceptUID</code> from the given string.
     * The string must match the CUI format, which is given by the regular
     * expression <code>C\d{7}</code> (the letter 'C' followed by exactly 7
     * digits). If the string does not match the regex, then a
     * <code>MalformedUMLSUniqueIdentifierException</code> is thrown.
     * 
     * @param cuid
     *            the string representing the CUI to be created
     * @return a <code>ConceptUID</code> whose valu is the given string
     * @throws MalformedUMLSUniqueIdentifierException
     *             if the string argument does not match the CUI format
     */
    public static ConceptUID fromString(String cuid)
            throws MalformedUMLSUniqueIdentifierException {
        Matcher m = cuidPattern.matcher(cuid);
        if (m.matches()) {
            return new ConceptUID(cuid);
        } else {
            throw new MalformedUMLSUniqueIdentifierException(
                    "Concept Unique Identifiers must consist of the letter 'C' "
                            + "followed by 7 digits: " + cuid);
        }
    }

    @Override
    public String getKeyName() {
        return "CUI";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConceptUID) {
            return this.getValue().equals(((ConceptUID) o).getValue());
        }
        return false;
    }
}
