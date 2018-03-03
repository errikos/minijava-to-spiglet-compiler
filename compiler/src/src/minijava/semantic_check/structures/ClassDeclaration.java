package minijava.semantic_check.structures;

import minijava.semantic_check.SemanticCheckException;

import java.util.Iterator;

/**
 *
 */
public class ClassDeclaration implements Type, Iterable<Identifier> {

    protected final Identifier typeName;

    protected IndexedHashMap<Identifier, VariableDeclaration> variables;
    protected IndexedHashMap<Identifier, MethodDeclaration> methods;

    public ClassDeclaration(Identifier typeName) {
        this.typeName = typeName;
        this.variables = new IndexedHashMap<>();
        this.methods = new IndexedHashMap<>();
    }

    public void registerVariable(Type type, Identifier name) throws SemanticCheckException {
        if (this.variables.get(name) != null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] Redefinition of class variable \"%s\"",
                    name.getLine(), name.getColumn(), name.getName()
            ));
        } else {
            this.variables.put(name, new VariableDeclaration(type, name));
        }
    }

    public void registerMethod(Type returnType, Identifier name, ClassDeclaration ownerClass)
            throws SemanticCheckException {
        if (this.methods.get(name) != null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] A method with name \"%s\" already exists within class \"%s\"",
                    name.getLine(), name.getColumn(), name.getName(), this.typeName
            ));
        } else {
            this.methods.put(name, new MethodDeclaration(returnType, name, ownerClass));
        }
    }

    public VariableDeclarationInfo getVariableInfo(Identifier identifier) {
        VariableDeclaration variableDeclaration = this.variables.get(identifier);
        int declarationIndex = this.variables.getKeyIndex(identifier);
        return new VariableDeclarationInfo(variableDeclaration, declarationIndex);
    }

    public MethodDeclarationInfo getMethodInfo(Identifier identifier) {
        MethodDeclaration methodDeclaration = this.methods.get(identifier);
        int declarationIndex = this.methods.getKeyIndex(identifier);
        return new MethodDeclarationInfo(methodDeclaration, declarationIndex);
    }

    public int getVariableCount() {
        return this.variables.size();
    }

    public int getMethodCount() {
        return this.methods.size();
    }

    @Override
    public Iterator<Identifier> iterator() {
        return this.methods.iterator();
    }

    @Override
    public String toString() {
        return this.typeName.toString();
    }

}
