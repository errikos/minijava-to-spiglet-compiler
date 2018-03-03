package minijava.semantic_check.structures;

import minijava.semantic_check.SemanticCheckException;

import java.util.Iterator;

/**
 *
 */
public class SymbolTable implements Iterable<Identifier> {

    private final IndexedHashMap<Identifier, ClassDeclaration> classSymbols;
    private MethodDeclaration mainMethod;

    private ClassDeclaration currentClass;
    private MethodDeclaration currentMethod;

    public SymbolTable() {
        this.classSymbols = new IndexedHashMap<>();
        this.mainMethod = null;
        this.currentClass = null;
        this.currentMethod = null;
    }

    public void registerClass(Identifier classIdentifier) throws SemanticCheckException {
        if (this.classSymbols.get(classIdentifier) != null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] A class with name \"%s\" has already been defined",
                    classIdentifier.getLine(), classIdentifier.getColumn(), classIdentifier.getName()
            ));  // A class with the same identifier has already been defined
        } else {
            ClassDeclaration classDeclaration = new ClassDeclaration(classIdentifier);
            this.classSymbols.put(classIdentifier, classDeclaration);
        }
    }

    public void registerClassExtension(Identifier classIdentifier,
                                       Identifier extendedClassIdentifier) throws  SemanticCheckException {
        ClassDeclaration extendedClassDeclaration = null;
        if (this.classSymbols.get(classIdentifier) != null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] A class with name \"%s\" has already been defined",
                    classIdentifier.getLine(), classIdentifier.getColumn(), classIdentifier.getName()
            ));  // A class with the same identifier has already been defined
        } else if ((extendedClassDeclaration = this.classSymbols.get(extendedClassIdentifier)) == null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] Extended class \"%s\" has not been defined",
                    extendedClassIdentifier.getLine(), extendedClassIdentifier.getColumn(),
                    extendedClassIdentifier.getName()
            ));  // Extended class not found
        } else {
            ClassExtendsDeclaration classExtendsDeclaration =
                    new ClassExtendsDeclaration(classIdentifier, extendedClassDeclaration);
            this.classSymbols.put(classIdentifier, classExtendsDeclaration);
        }
    }

    public void registerMainMethod(Type returnType, Identifier name, ClassDeclaration ownerClass) {
        this.mainMethod = new MethodDeclaration(returnType, name, ownerClass);
    }

    public ClassDeclaration lookupClass(Identifier identifier) throws SemanticCheckException {
        ClassDeclaration classDeclaration = this.classSymbols.get(identifier);
        if (classDeclaration == null) {
            throw new SemanticCheckException(String.format(
                    "-> [%d,%d] \"%s\" does not name a type",
                    identifier.getLine(), identifier.getColumn(), identifier.getName()
            ));
        }
        return classDeclaration;
    }

    public ClassDeclaration enterClass(Identifier identifier) {
        if (this.currentClass != null) {
            System.err.println("(!) Symbol table warning: Entering a class scope from within a class scope.");
        }
        this.currentClass = this.classSymbols.get(identifier);
        return this.currentClass;
    }

    public void leaveClass() {
        if (this.currentClass == null) {
            System.err.println("(!) Symbol table warning: Leaving a class scope while not in a class scope.");
        }
        this.currentClass = null;
    }

    public MethodDeclaration enterMethod(Identifier identifier) {
        if (this.currentMethod != null) {
            System.err.println("(!) Symbol table warning: Entering a method scope from within a method scope.");
        }
        this.currentMethod = this.currentClass.methods.get(identifier);
        return this.currentMethod;
    }

    public void leaveMethod() {
        if (this.currentMethod == null) {
            System.err.println("(!) Symbol table warning: Leaving a method scope while not in a method scope.");
        }
        this.currentMethod = null;
    }

    public MethodDeclaration enterMainMethod() {
        this.currentMethod = this.mainMethod;
        return this.currentMethod;
    }

    public void leaveMainMethod() {
        this.currentMethod = null;
    }

    @Override
    public Iterator<Identifier> iterator() {
        return this.classSymbols.iterator();
    }

    public ClassDeclaration getCurrentClass() {
        return this.currentClass;
    }

    public MethodDeclaration getCurrentMethod() {
        return this.currentMethod;
    }

}
