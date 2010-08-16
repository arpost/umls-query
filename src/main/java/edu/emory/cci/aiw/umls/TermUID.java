package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Term Unique Identifier (TUI) in a UMLS query. TUIs must match
 * the regular expression <code>T\d{3}</code> (the letter 'T' followed by 3
 * digits). Implements several marker interfaces that allow objects of this type
 * to be used as a search key in various UMLS queries.
 * 
 * @author Michel Mansour
 * 
 */
public final class TermUID extends AbstractUMLSSearchUID {

    private static Pattern tuidPattern;

    /*
     * the regex all TUIs must match
     */
    static {
        tuidPattern = Pattern.compile("T\\d{3}");
    }

    private TermUID(String tui) {
        super(tui);
    }

    @Override
    public String getKeyName() {
        return "TUI";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TermUID) {
            return this.getValue().equals(((TermUID) o).getValue());
        }
        return false;
    }

    /**
     * Creates and returns a new <code>TermUID</code> from the given string. The
     * string must match the TUI format, which is given by the regular
     * expression <code>T\d{3}</code> (the letter 'T' followed by 3 digits). If
     * the string does not match the regex, then a
     * <code>MalformedUMLSUniqueIdentifierException</code> is thrown.
     * 
     * @param tui
     *            the string representing the TUI to be created
     * @return a <code>TermUID</code> whose value is the given string
     * @throws MalformedUMLSUniqueIdentifierException
     *             if the string argument does not match the TUI format
     */
    public static TermUID fromString(String tui)
            throws MalformedUMLSUniqueIdentifierException {
        Matcher m = tuidPattern.matcher(tui);
        if (m.matches()) {
            return new TermUID(tui);
        } else {
            throw new MalformedUMLSUniqueIdentifierException(
                    "Term Unique Identifiers must consist of the letter 'T' "
                            + "followed by 3 digits");
        }
    }
}
