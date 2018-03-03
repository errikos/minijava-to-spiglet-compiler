package minijava.semantic_check.visitors;

import minijava.semantic_check.structures.*;
import minijava.visitor.GJVoidDepthFirst;

/**
 * Specialized visitor that collects the methods of each user defined class.
 * Runs just after ClassVariableDeclarationCollector.
 */
public class ClassMethodDeclarationCollector extends GJVoidDepthFirst<SymbolTable> {

    private final IdentifierCollector identifierCollector;
    private final TypeNameCollector typeNameCollector;

    public ClassMethodDeclarationCollector() {
        this.identifierCollector = new IdentifierCollector();
        this.typeNameCollector = new TypeNameCollector();
    }

    @Override
    public void visit(minijava.syntaxtree.MainClass n, SymbolTable symbolTable) throws Exception {
        // Get the main class identifier.
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        // Enter the main class scope.
        symbolTable.enterClass(classIdentifier);
        // Create a Type object for the main method return type (void).
        Type mainReturnType = new PrimitiveType(new Identifier(n.f5.beginLine, n.f5.beginColumn, n.f5.tokenImage));
        // Create an Identifier object for the main method name (main).
        Identifier mainId = new Identifier(n.f6.beginLine, n.f6.beginColumn, n.f6.tokenImage);
        // Register the main method into the symbol table.
        symbolTable.registerMainMethod(mainReturnType, mainId, symbolTable.getCurrentClass());
        // Enter the main method scope.
        symbolTable.enterMainMethod();
        // Insert the main argument into the main method.
        Type mainArgType = new PrimitiveType(new Identifier(n.f8.beginLine, n.f8.beginColumn, "String[]"));
        Identifier mainArgId = n.f11.accept(this.identifierCollector);
        symbolTable.getCurrentMethod().registerArgument(mainArgType, mainArgId);
        // Visit and process the main method's local variable declarations.
        n.f14.accept(this, symbolTable);
        // Job done. Leave the main method and main class scope.
        symbolTable.leaveMainMethod();
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.ClassDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the current class identifier.
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        // Enter the current class scope.
        symbolTable.enterClass(classIdentifier);
        // Visit and process the method declarations of the current class.
        n.f4.accept(this, symbolTable);
        // Job done. Leave the current class scope.
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.ClassExtendsDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the current class identifier.
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        // Enter the current class scope.
        symbolTable.enterClass(classIdentifier);
        // Inherit the superclass method keys.
        ((ClassExtendsDeclaration)symbolTable.getCurrentClass()).inheritMethodKeys();
        // Visit and process the method declarations of the current class.
        n.f6.accept(this, symbolTable);
        // Inherit the (non-redefined) superclass methods.
        ((ClassExtendsDeclaration)symbolTable.getCurrentClass()).inheritMethods();
        // Job done. Leave the current class scope.
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.MethodDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the method return type.
        Type returnType = n.f1.accept(this.typeNameCollector, symbolTable);
        // Get the method name.
        Identifier name = n.f2.accept(this.identifierCollector);
        // Register the method to the current class.
        symbolTable.getCurrentClass().registerMethod(returnType, name, symbolTable.getCurrentClass());
        // Enter the newly created method scope.
        symbolTable.enterMethod(name);
        // Visit and process the method's arguments (formal parameters).
        n.f4.accept(this, symbolTable);
//        // Check whether the method properly overrides any other methods with the class hierarchy.
//        symbolTable.getCurrentClass().ensureValidOverride(name);
        // Visit and process the method's local variable declarations.
        n.f7.accept(this, symbolTable);
        // Job done. Leave the current method scope.
        symbolTable.leaveMethod();
    }

    @Override
    public void visit(minijava.syntaxtree.FormalParameter n, SymbolTable symbolTable) throws Exception {
        // Get the parameter type.
        Type type = n.f0.accept(this.typeNameCollector, symbolTable);
        // Get the parameter name.
        Identifier name = n.f1.accept(this.identifierCollector);
        // Register the parameter to the current method.
        symbolTable.getCurrentMethod().registerArgument(type, name);
    }

    @Override
    public void visit(minijava.syntaxtree.VarDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the variable type.
        Type type = n.f0.accept(this.typeNameCollector, symbolTable);
        // Get the variable name.
        Identifier name = n.f1.accept(this.identifierCollector);
        // Register the variable to the current method.
        symbolTable.getCurrentMethod().registerLocal(type, name);
    }

}
