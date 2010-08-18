package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a String Unique Identifier (SUI) in a UMLS query. SUIs must match
 * the regular expression <code>S\d{7}</code> (the letter 'S' followed by 7 or
 * digits). Implements several marker interfaces that allow objects of this type
 * to be used as a search key in various UMLS queries.
 * 
 * @author Michel Mansour
 * 
 */
public final class StringUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, MapToIdQuerySearchUID {
    private static Pattern suidPattern;

    /*
     * An empty SUI, used only within this package when only the key name is
     * required.
     */
    static final StringUID EMPTY_SUI = new StringUID("");

    /*
     * the regex all SUIs must match
     */
    static {
        suidPattern = Pattern.compile("S\\d{7,8}");
    }

    private StringUID(String suid) {
        super(suid);
    }

    /**
     * Creates and returns a new <code>StringUID</code> from the given string.
     * The string must match the SUI format, which is given by the regular
     * expression <code>S\d{7,8}</code> (the letter 'S' followed by 7 or 8
     * digits). If the string does not match the regex, then a
     * <code>MalformedUMLSUniqueIdentifierException</code> is thrown.
     * 
     * @param suid
     *            the string representing the SUI to be created
     * @return a <code>StringUID</code> whose value is the given string
     * @throws MalformedUMLSUniqueIdentifierException
     *             if the string argument does not match the SUI format
     */
    public static StringUID fromString(String suid)
            throws MalformedUMLSUniqueIdentifierException {
        Matcher m = suidPattern.matcher(suid);
        if (m.matches()) {
            return new StringUID(suid);
        } else {
            throw new MalformedUMLSUniqueIdentifierException(
                    "String Unique Identifiers must consist of the letter 'S' "
                            + "followed by 7 or 8 digits: " + suid);
        }
    }

    @Override
    public String getKeyName() {
        return "SUI";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StringUID) {
            return this.getValue().equals(((StringUID) o).getValue());
        }
        return false;
    }
}
