package minijava.semantic_check.visitors;

import minijava.semantic_check.structures.ClassExtendsDeclaration;
import minijava.semantic_check.structures.Identifier;
import minijava.semantic_check.structures.SymbolTable;
import minijava.semantic_check.structures.Type;
import minijava.visitor.GJVoidDepthFirst;

/**
 * Specialized visitor that collects the data members (variables) of each user defined class.
 * Runs just after ClassDeclarationCollector.
 */
public class ClassVariableDeclarationCollector extends GJVoidDepthFirst<SymbolTable> {

    private final IdentifierCollector identifierCollector;
    private final TypeNameCollector typeNameCollector;

    public ClassVariableDeclarationCollector() {
        this.identifierCollector = new IdentifierCollector();
        this.typeNameCollector = new TypeNameCollector();
    }

    /**
     * Overridden just to make sure that the VarDeclarations of the main method will not be visited.
     */
    @Override
    public void visit(minijava.syntaxtree.MainClass n, SymbolTable symbolTable) throws Exception {}

    @Override
    public void visit(minijava.syntaxtree.ClassDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the current class identifier.
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        // Enter the current class scope.
        symbolTable.enterClass(classIdentifier);
        // Visit and process the variable declarations of the current class.
        n.f3.accept(this, symbolTable);
        // Job done. Leave the current class scope.
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.ClassExtendsDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the current class identifier.
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        // Enter the current class scope.
        symbolTable.enterClass(classIdentifier);
        // Inherit the superclass variable keys.
        ((ClassExtendsDeclaration)symbolTable.getCurrentClass()).inheritVariableKeys();
        // Visit and process the variable declarations of the current class.
        n.f5.accept(this, symbolTable);
        // Inherit the (non-redefined) superclass variables.
        ((ClassExtendsDeclaration)symbolTable.getCurrentClass()).inheritVariables();
        // Job done. Leave the current class scope.
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.VarDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the variable type.
        Type type = n.f0.accept(this.typeNameCollector, symbolTable);
        // Get the variable name.
        Identifier name = n.f1.accept(this.identifierCollector);
        // Register the variable to the current class.
        symbolTable.getCurrentClass().registerVariable(type, name);
    }

}
