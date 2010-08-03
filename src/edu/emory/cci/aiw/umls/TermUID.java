package edu.emory.cci.aiw.umls;

public final class TermUID extends AbstractUMLSSearchUID {

	private char tui1;
	private char tui2;
	private char tui3;
	private char tui4;
	
	private TermUID(char tui1, char tui2, char tui3, char tui4) {
		super(new String(new char[]{tui1, tui2, tui3, tui4}));
		this.tui1 = tui1;
		this.tui2 = tui2;
		this.tui3 = tui3;
		this.tui4 = tui4;
	}
		
	@Override
	public String getKeyName() {
		// TODO Auto-generated method stub
		return "TUI";
	}

	public static TermUID fromChars(char tui1, char tui2,
									char tui3, char tui4) {
		return new TermUID(tui1, tui2, tui3, tui4);
	}
	
	public static TermUID fromString(String tui) 
		throws MalformedUMLSUniqueIdentifierException {
		if (tui.length() != 4) {
			throw new MalformedUMLSUniqueIdentifierException(
					"TUIs must consist of exactly 4 characters");
		} else {
			return fromChars(tui.charAt(0), tui.charAt(1), tui.charAt(2),
								tui.charAt(3));
		}
	}
}
