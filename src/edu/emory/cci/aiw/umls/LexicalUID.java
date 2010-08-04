package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LexicalUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, MapToIdQuerySearchUID {
	private static Pattern luidPattern;

	static {
		luidPattern = Pattern.compile("L\\d{7}");
	}

	private LexicalUID(String luid) {
		super(luid);
	}

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

	public String getKeyName() {
		return "LUI";
	}

	public static void main(String[] args) {
		testLUID("C1234567");
		testLUID("L1234567");
		testLUID("L0000000");
		testLUID("L123456");
		testLUID("L");
	}

	private static void testLUID(String luid) {
		System.out.print(luid + ": ");
		try {
			System.out.println(LexicalUID.fromString(luid));
		} catch (MalformedUMLSUniqueIdentifierException e) {
			System.out.println("bad LUID");
		}
	}
}
