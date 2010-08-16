package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an Atom Unique Identifier (AUI) in a UMLS query. AUIs must match
 * the regular expression <code>A\d{7,8}</code> (the letter 'A' followed by 7 or
 * 8 digits). Implements several marker interfaces that allow objects of this
 * type to be used as a search key in various UMLS queries.
 * 
 * @author Michel Mansour
 * 
 */
public final class AtomUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, ParentsQuerySearchUID,
        NeighborQuerySearchUID, MapToIdQuerySearchUID {

    private static Pattern auidPattern;

    /*
     * An empty AUI, used only within this package when only the key name is
     * required
     */
    static final AtomUID EMPTY_AUI = new AtomUID("");

    /*
     * the regex all AUIs must match
     */
    static {
        auidPattern = Pattern.compile("A\\d{7,8}");
    }

    private AtomUID(String auid) {
        super(auid);
    }

    /**
     * Creates and returns a new <code>AtomUID</code> from the given string. The
     * string must match the AUI format, which is given by the regular
     * expression <code>A\d{7}</code> (the letter 'A' followed by exactly 7 or 8
     * digits). If the string does not match the regex, then a
     * <code>MalformedUMLSUniqueIdentifierException</code> is thrown.
     * 
     * @param auid
     *            the string representing the AUI to be created
     * @return an <code>AtomUID</code> whose value is the given string
     * @throws MalformedUMLSUniqueIdentifierException
     *             if the string argument does not match the AUI format
     */
    public static AtomUID fromString(String auid)
            throws MalformedUMLSUniqueIdentifierException {
        Matcher m = auidPattern.matcher(auid);
        if (m.matches()) {
            return new AtomUID(auid);
        } else {
            throw new MalformedUMLSUniqueIdentifierException(
                    "Atom Unique Identifiers must consist of the letter "
                            + "'A' followed by 7 or 8 digits: " + auid);
        }
    }

    @Override
    public String getKeyName() {
        // TODO Auto-generated method stub
        return "AUI";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomUID) {
            return this.getValue().equals(((AtomUID) o).getValue());
        }
        return false;
    }
}
