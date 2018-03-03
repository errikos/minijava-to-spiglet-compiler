package minijava.semantic_check.structures;

/**
 *
 */
public class Identifier {

    private final int line;
    private final int column;
    private final String name;

    public Identifier(int line, int column, String name) {
        this.line = line;
        this.column = column;
        this.name = name;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identifier) {
            Identifier other = (Identifier)obj;
            return this.name.equals(other.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

}
