package minijava.semantic_check.structures;

import minijava.semantic_check.SemanticCheckException;

/**
 *
 */
public class ClassExtendsDeclaration extends ClassDeclaration {

    private final ClassDeclaration extendedClass;

    public ClassExtendsDeclaration(Identifier identifier, ClassDeclaration extendedClass) {
        super(identifier);
        this.extendedClass = extendedClass;
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

    public void inheritVariableKeys() {
        for (Identifier varId: this.extendedClass.variables) {
            this.variables.put(varId, null);
        }
    }

    public void inheritVariables() {
        for (Identifier varId: this.extendedClass.variables) {
            if (this.variables.get(varId) == null) {
                this.variables.put(varId, this.extendedClass.variables.get(varId));
            }
        }
    }

    public void inheritMethodKeys() {
        for (Identifier methodId: this.extendedClass.methods) {
            this.methods.put(methodId, null);
        }
    }

    public void inheritMethods() {
        for (Identifier methodId: this.extendedClass.methods) {
            if (this.methods.get(methodId) == null) {
                this.methods.put(methodId, this.extendedClass.methods.get(methodId));
            }
        }
    }

}
