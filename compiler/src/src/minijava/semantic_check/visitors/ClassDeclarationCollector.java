package minijava.semantic_check.visitors;

import minijava.semantic_check.structures.Identifier;
import minijava.semantic_check.structures.SymbolTable;
import minijava.visitor.GJVoidDepthFirst;

/**
 * Simple visitor that collects the class names of the MiniJava input.
 * This is the very first visitor to be called, in order obtain all user defined types.
 */
public class ClassDeclarationCollector extends GJVoidDepthFirst<SymbolTable> {

    private final IdentifierCollector identifierCollector;

    public ClassDeclarationCollector() {
        this.identifierCollector = new IdentifierCollector();
    }

    @Override
    public void visit(minijava.syntaxtree.MainClass n, SymbolTable symbolTable) throws Exception {
        Identifier mainClassIdentifier = n.f1.accept(this.identifierCollector);
        symbolTable.registerClass(mainClassIdentifier);
    }

    @Override
    public void visit(minijava.syntaxtree.ClassDeclaration n, SymbolTable symbolTable) throws Exception {
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        symbolTable.registerClass(classIdentifier);
    }

    @Override
    public void visit(minijava.syntaxtree.ClassExtendsDeclaration n, SymbolTable symbolTable) throws Exception {
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        Identifier extendedClassIdentifier = n.f3.accept(this.identifierCollector);
        symbolTable.registerClassExtension(classIdentifier, extendedClassIdentifier);
    }

}
