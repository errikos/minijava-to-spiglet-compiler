package minijava.semantic_check.structures;

import minijava.semantic_check.SemanticCheckException;

import java.util.Iterator;

/**
 *
 */
public class MethodDeclaration {

    private final Type returnType;
    private final Identifier name;
    private final ClassDeclaration ownerClass;

    private final IndexedHashMap<Identifier, VariableDeclaration> arguments;
    private final IndexedHashMap<Identifier, VariableDeclaration> locals;

    public MethodDeclaration(Type returnType, Identifier name, ClassDeclaration ownerClass) {
        this.returnType = returnType;
        this.name = name;
        this.ownerClass = ownerClass;
        this.arguments = new IndexedHashMap<>();
        this.locals = new IndexedHashMap<>();
    }

    public void registerArgument(Type type, Identifier name) throws SemanticCheckException {
        if (this.arguments.get(name) != null || this.locals.get(name) != null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] Redefinition of method variable \"%s\"",
                    name.getLine(), name.getColumn(), name.getName()
            ));
        } else {
            VariableDeclaration variableDeclaration = new VariableDeclaration(type, name);
            this.arguments.put(name, variableDeclaration);
        }
    }

    public void registerLocal(Type type, Identifier name) throws SemanticCheckException {
        if (this.arguments.get(name) != null || this.locals.get(name) != null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] Redefinition of method variable \"%s\"",
                    name.getLine(), name.getColumn(), name.getName()
            ));
        } else {
            VariableDeclaration variableDeclaration = new VariableDeclaration(type, name);
            this.locals.put(name, variableDeclaration);
        }
    }

    public boolean argumentsMatch(MethodDeclaration other) {
        if (this.arguments.size() != other.arguments.size())
            return false;
        Iterator<VariableDeclaration> it1 = this.arguments.values().iterator();
        Iterator<VariableDeclaration> it2 = other.arguments.values().iterator();
        while (it1.hasNext() && it2.hasNext()) {
            if (!it1.next().getType().equals(it2.next().getType()))
                return false;
        }
        return true;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public Identifier getName() {
        return this.name;
    }

    public ClassDeclaration getOwnerClass() {
        return this.ownerClass;
    }

    public int getArgumentsCount() {
        return this.arguments.size();
    }

    public int getLocalVariablesCount() {
        return this.locals.size();
    }

    /**
     * Returns a pair containing the variable declaration information for identifier.
     * The pair is a VariableDeclarationInfo object containing the VariableDeclaration object and its declaration index.
     */
    public VariableDeclarationInfo getVariableInfo(Identifier identifier) {
        VariableDeclaration variableDeclaration = null;
        int declarationIndex = -1;
        variableDeclaration = this.arguments.get(identifier);
        if (variableDeclaration == null) {
            variableDeclaration = this.locals.get(identifier);
            declarationIndex = this.locals.getKeyIndex(identifier) + this.arguments.size();
        } else {
            declarationIndex = this.arguments.getKeyIndex(identifier);
        }
        return new VariableDeclarationInfo(variableDeclaration, declarationIndex);
    }

    @Override
    public String toString() {
        return String.format("%s %s()", this.returnType, this.name);
    }

}
