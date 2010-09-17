package edu.emory.cci.aiw.umls;

/**
 * A class representing the semantic type of a term in UMLS.
 * 
 * @author Michel Mansour
 */
public final class SemanticType {
    private TermUID tui;
    private String semanticType;

    private SemanticType(TermUID tui, String type) {
        this.tui = tui;
        this.semanticType = type;
    }

    static SemanticType withTUIAndType(TermUID tui, String type) {
        return new SemanticType(tui, type);
    }

    public TermUID getTUI() {
        return this.tui;
    }

    public String getType() {
        return this.semanticType;
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + tui.hashCode();
        result = 31 * result + semanticType.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof SemanticType) {
            SemanticType other = (SemanticType) o;
            return this.tui.equals(other.tui)
                    && this.semanticType.equals(other.semanticType);
        } else {
            return false;
        }
    }
    
    public String toString() {
        return tui + ":" + semanticType;
    }
}
