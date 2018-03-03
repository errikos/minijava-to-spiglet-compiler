package minijava.semantic_check.structures;

/**
 *
 */
public class MethodDeclarationInfo {

    private final MethodDeclaration methodDeclaration;
    private final int declarationIndex;

    public MethodDeclarationInfo(MethodDeclaration methodDeclaration, int declarationIndex) {
        this.methodDeclaration = methodDeclaration;
        this.declarationIndex = declarationIndex;
    }

    public MethodDeclaration getMethodDeclaration() {
        return this.methodDeclaration;
    }

    public int getDeclarationIndex() {
        return this.declarationIndex;
    }

}
