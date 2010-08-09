package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, MapToIdQuerySearchUID {
	private static Pattern suidPattern;

	static final StringUID EMPTY_SUI = new StringUID("");
	
	static {
		suidPattern = Pattern.compile("S\\d{7}");
	}

	private StringUID(String suid) {
		super(suid);
	}

	public static StringUID fromString(String suid)
	        throws MalformedUMLSUniqueIdentifierException {
		Matcher m = suidPattern.matcher(suid);
		if (m.matches()) {
			return new StringUID(suid);
		} else {
			throw new MalformedUMLSUniqueIdentifierException(
			        "String Unique Identifiers must consist of the letter 'S' "
			                + "followed by 7 digits: " + suid);
		}
	}

	public String getKeyName() {
		return "SUI";
	}
	
	public boolean equals(Object o) {
		if (o instanceof StringUID) {
			return this.getValue().equals(((StringUID) o).getValue());
		}
		return false;
	}

	public static void main(String[] args) {
		testSUID("C1234567");
		testSUID("L1234567");
		testSUID("C0000000");
		testSUID("C123456");
		testSUID("C");
	}

	private static void testSUID(String suid) {
		System.out.print(suid + ": ");
		try {
			System.out.println(StringUID.fromString(suid));
		} catch (MalformedUMLSUniqueIdentifierException e) {
			System.out.println("bad SUID");
		}
	}
}
