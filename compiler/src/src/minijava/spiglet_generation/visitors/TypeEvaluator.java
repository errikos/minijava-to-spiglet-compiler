package minijava.spiglet_generation.visitors;

import minijava.semantic_check.structures.*;
import minijava.semantic_check.visitors.IdentifierCollector;
import minijava.visitor.GJDepthFirst;

/**
 * The type evaluator is used for several nodes of the MiniJava syntax tree,
 * for which the Expression evaluator needs to know their (class) type.
 */
public class TypeEvaluator extends GJDepthFirst<Type, SymbolTable> {

    private final IdentifierCollector identifierCollector;

    public TypeEvaluator(IdentifierCollector identifierCollector) {
        this.identifierCollector = identifierCollector;
    }

    @Override
    public Type visit(minijava.syntaxtree.MessageSend n, SymbolTable symbolTable) throws Exception {
        // Resolve the type of the object the call is applied to.
        ClassDeclaration objTypeName = (ClassDeclaration)(n.f0.accept(this, symbolTable));
        // Get the method identifier.
        Identifier methodId = n.f2.accept(this.identifierCollector);
        // Get the method information.
        MethodDeclarationInfo methodInfo = objTypeName.getMethodInfo(methodId);
        return methodInfo.getMethodDeclaration().getReturnType();
    }

    @Override
    public Type visit(minijava.syntaxtree.Identifier n, SymbolTable symbolTable) throws Exception {
        Type idTypeName = null;
        VariableDeclarationInfo varInfo;
        // Get an Identifier object for the identifier we visit.
        Identifier identifier = n.accept(this.identifierCollector);
        // Check whether the resembling variable is defined within the current method.
        varInfo = symbolTable.getCurrentMethod().getVariableInfo(identifier);
        if (varInfo.getVariableDeclaration() == null) {
            varInfo = symbolTable.getCurrentClass().getVariableInfo(identifier);
        }
        idTypeName = varInfo.getVariableDeclaration().getType();
        return idTypeName;
    }

    @Override
    public Type visit(minijava.syntaxtree.ThisExpression n, SymbolTable symbolTable) throws Exception {
        return symbolTable.getCurrentClass();
    }

    @Override
    public Type visit(minijava.syntaxtree.AllocationExpression n, SymbolTable symbolTable)
            throws Exception {
        Identifier classId = n.f1.accept(this.identifierCollector);
        return symbolTable.lookupClass(classId);
    }

    @Override
    public Type visit(minijava.syntaxtree.BracketExpression n, SymbolTable symbolTable) throws Exception {
        return n.f1.accept(this, symbolTable);
    }

}
