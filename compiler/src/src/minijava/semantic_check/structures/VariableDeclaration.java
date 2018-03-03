package minijava.semantic_check.structures;

/**
 *
 */
public class VariableDeclaration {

    private final Type type;
    private final Identifier name;

    public VariableDeclaration(Type type, Identifier name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return this.type;
    }

    public Identifier getName() {
        return this.name;
    }

}
