package minijava.semantic_check.structures;

/**
 *
 */
public class VariableDeclarationInfo {

    private final VariableDeclaration variableDeclaration;
    private final int declarationIndex;

    public VariableDeclarationInfo(VariableDeclaration variableDeclaration, int declarationIndex) {
        this.variableDeclaration = variableDeclaration;
        this.declarationIndex = declarationIndex;
    }

    public VariableDeclaration getVariableDeclaration() {
        return this.variableDeclaration;
    }

    public int getDeclarationIndex() {
        return this.declarationIndex;
    }

}
