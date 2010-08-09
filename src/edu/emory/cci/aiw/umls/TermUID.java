package edu.emory.cci.aiw.umls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TermUID extends AbstractUMLSSearchUID {

	private static Pattern tuidPattern;

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
	
	public boolean equals(Object o) {
		if (o instanceof TermUID) {
			return this.getValue().equals(((TermUID) o).getValue());
		}
		return false;
	}

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
