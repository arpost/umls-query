package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AtomUID extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID, ParentsQuerySearchUID,
        NeighborQuerySearchUID, MapToIdQuerySearchUID {
	
	private static Pattern auidPattern;

	static {
		auidPattern = Pattern.compile("A\\d{7}");
	}

	private AtomUID(String auid) {
		super(auid);
	}

	public static AtomUID fromString(String auid)
	        throws MalformedUMLSUniqueIdentifierException {
		Matcher m = auidPattern.matcher(auid);
		if (m.matches()) {
			return new AtomUID(auid);
		} else {
			throw new MalformedUMLSUniqueIdentifierException(
			        "Atom Unique Identifiers must consist of the letter "
			                + "'A' followed by 7 digits: " + auid);
		}
	}

	@Override
	public String getKeyName() {
		// TODO Auto-generated method stub
		return "AUI";
	}

	public static void main(String[] args) {
		testAUID("C1234567");
		testAUID("L1234567");
		testAUID("C0000000");
		testAUID("C123456");
		testAUID("C");
	}

	private static void testAUID(String auid) {
		System.out.print(auid + ": ");
		try {
			System.out.println(AtomUID.fromString(auid));
		} catch (MalformedUMLSUniqueIdentifierException e) {
			System.out.println("bad AUID");
		}
	}
}
