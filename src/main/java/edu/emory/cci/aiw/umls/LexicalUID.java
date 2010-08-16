package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Lexical Unique Identifier (LUI) in a UMLS query. LUIs must match
 * the regular expression <code>L\d{7}</code> (the letter 'L' followed by 7
 * digits). Implements several marker interfaces that allow objects of this type
 * to be used as a search key in various UMLS queries.
 * 
 * @author Michel Mansour
 * 
 */
public final class LexicalUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, MapToIdQuerySearchUID {
    private static Pattern luidPattern;

    /*
     * Empty LUI used only within this package when only the key name is needed
     */
    static final LexicalUID EMPTY_LUI = new LexicalUID("");

    /*
     * regex that LUIs must match
     */
    static {
        luidPattern = Pattern.compile("L\\d{7}");
    }

    private LexicalUID(String luid) {
        super(luid);
    }

    /**
     * Creates and returns a new <code>LexicalUID</code> from the given string.
     * The string must match the LUI format, which is given by the regular
     * expression <code>L\d{7}</code> (the letter 'L' followed by 7 digits). If
     * the string does not match the regex, then a
     * <code>MalformedUMLSUniqueIdentifierException</code> is thrown.
     * 
     * @param luid
     *            the string representing the LUI to be created
     * @return a <code>LexicalUID</code> whose value is the given string
     * @throws MalformedUMLSUniqueIdentifierException
     *             if the string argument does not match the LUI format
     */
    public static LexicalUID fromString(String luid)
            throws MalformedUMLSUniqueIdentifierException {
        Matcher m = luidPattern.matcher(luid);
        if (m.matches()) {
            return new LexicalUID(luid);
        } else {
            throw new MalformedUMLSUniqueIdentifierException(
                    "Concept Unique Identifiers must consist of the letter 'L' "
                            + "followed by 7 digits");
        }
    }

    @Override
    public String getKeyName() {
        return "LUI";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LexicalUID) {
            return this.getValue().equals(((LexicalUID) o).getValue());
        }
        return false;
    }
}
