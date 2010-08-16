package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConceptUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, ParentsQuerySearchUID,
        NeighborQuerySearchUID, MapToIdQuerySearchUID {

    private static Pattern cuidPattern;

    static final ConceptUID EMPTY_CUI = new ConceptUID("");

    static {
        cuidPattern = Pattern.compile("C\\d{7}");
    }

    private ConceptUID(String cuid) {
        super(cuid);
    }

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

    public String getKeyName() {
        return "CUI";
    }

    public boolean equals(Object o) {
        if (o instanceof ConceptUID) {
            return this.getValue().equals(((ConceptUID) o).getValue());
        }
        return false;
    }

    public static void main(String[] args) {
        testCUID("C1234567");
        testCUID("L1234567");
        testCUID("C0000000");
        testCUID("C123456");
        testCUID("C");
    }

    private static void testCUID(String cuid) {
        System.out.print(cuid + ": ");
        try {
            System.out.println(ConceptUID.fromString(cuid));
        } catch (MalformedUMLSUniqueIdentifierException e) {
            System.out.println("bad CUID");
        }
    }
}
