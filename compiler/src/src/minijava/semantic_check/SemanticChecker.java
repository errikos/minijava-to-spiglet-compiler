package minijava.semantic_check;

import minijava.semantic_check.structures.SymbolTable;
import minijava.semantic_check.visitors.ClassDeclarationCollector;
import minijava.semantic_check.visitors.ClassMethodDeclarationCollector;
import minijava.semantic_check.visitors.ClassVariableDeclarationCollector;
import minijava.syntaxtree.Goal;

/**
 * Wrapper object for the semantic checking process.
 */
public class SemanticChecker {

    private final Goal syntaxTree;

    private final SymbolTable symbolTable;

    private final ClassDeclarationCollector classDeclarationCollector;
    private final ClassVariableDeclarationCollector classVariableDeclarationCollector;
    private final ClassMethodDeclarationCollector classMethodDeclarationCollector;

    public SemanticChecker(Goal syntaxTree) {
        this.syntaxTree = syntaxTree;
        this.symbolTable = new SymbolTable();
        this.classDeclarationCollector = new ClassDeclarationCollector();
        this.classVariableDeclarationCollector = new ClassVariableDeclarationCollector();
        this.classMethodDeclarationCollector = new ClassMethodDeclarationCollector();
    }

    public SymbolTable run() throws Exception {
        this.syntaxTree.accept(this.classDeclarationCollector, this.symbolTable);
        this.syntaxTree.accept(this.classVariableDeclarationCollector, this.symbolTable);
        this.syntaxTree.accept(this.classMethodDeclarationCollector, this.symbolTable);
        return this.symbolTable;
    }
}
