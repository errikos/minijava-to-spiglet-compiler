package minijava.semantic_check.visitors;

import minijava.semantic_check.structures.Identifier;
import minijava.semantic_check.structures.PrimitiveType;
import minijava.semantic_check.structures.SymbolTable;
import minijava.semantic_check.structures.Type;
import minijava.visitor.GJDepthFirst;

/**
 * Collects the type names, ie. informs about the type of type literals
 * such as "int", "int[], "boolean", and user defined type names.
 */
public class TypeNameCollector extends GJDepthFirst<Type, SymbolTable> {

    @Override
    public Type visit(minijava.syntaxtree.Type n, SymbolTable symbolTable) throws Exception {
        return n.f0.accept(this, symbolTable);
    }

    @Override
    public Type visit(minijava.syntaxtree.ArrayType n, SymbolTable symbolTable) throws Exception {
        Identifier identifier = new Identifier(n.f0.beginLine, n.f0.beginColumn, "int[]");
        return new PrimitiveType(identifier);
    }

    @Override
    public Type visit(minijava.syntaxtree.BooleanType n, SymbolTable symbolTable) throws Exception {
        Identifier identifier = new Identifier(n.f0.beginLine, n.f0.beginColumn, "boolean");
        return new PrimitiveType(identifier);
    }

    @Override
    public Type visit(minijava.syntaxtree.IntegerType n, SymbolTable symbolTable) throws Exception {
        Identifier identifier = new Identifier(n.f0.beginLine, n.f0.beginColumn, "int");
        return new PrimitiveType(identifier);
    }

    @Override
    public Type visit(minijava.syntaxtree.Identifier n, SymbolTable symbolTable) throws Exception {
        Identifier identifier = new Identifier(n.f0.beginLine, n.f0.beginColumn, n.f0.tokenImage);
        return symbolTable.lookupClass(identifier);
    }

}
