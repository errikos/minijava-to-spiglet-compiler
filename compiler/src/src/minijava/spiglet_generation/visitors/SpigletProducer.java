package minijava.spiglet_generation.visitors;

import minijava.semantic_check.structures.*;
import minijava.semantic_check.visitors.IdentifierCollector;
import minijava.spiglet_generation.structures.BookKeeper;
import minijava.spiglet_generation.structures.Emitter;
import minijava.visitor.GJVoidDepthFirst;

import java.io.FileOutputStream;

/**
 * Main Spiglet code generating visitor.
 * Visits the main class and its main method, as well as the user defined classes and their methods.
 * Delegates certain Expressions to the ExpressionEvaluator visitor.
 */
public class SpigletProducer extends GJVoidDepthFirst<SymbolTable> {

    private final Emitter emitter;
    private final BookKeeper bookKeeper;

    private final IdentifierCollector identifierCollector;
    private final ExpressionEvaluator expressionEvaluator;

    public SpigletProducer(FileOutputStream outputStream) {
        this.emitter = new Emitter(outputStream);
        this.bookKeeper = new BookKeeper();
        this.identifierCollector = new IdentifierCollector();
        this.expressionEvaluator = new ExpressionEvaluator(this.emitter, this.bookKeeper, this.identifierCollector);
    }

    @Override
    public void visit(minijava.syntaxtree.MainClass n, SymbolTable symbolTable) throws Exception {
        // Get the current class identifier.
        Identifier classIdentifier = n.f1.accept(this.identifierCollector);
        // Enter the main class scope.
        symbolTable.enterClass(classIdentifier);
        // Create an Identifier object for the main method name (main).
        Identifier mainId = new Identifier(n.f6.beginLine, n.f6.beginColumn, n.f6.tokenImage);
        // Enter the main method scope.
        MethodDeclaration mainMethod = symbolTable.enterMainMethod();
        // Get the local variables count
        int localVariablesCount = mainMethod.getLocalVariablesCount();
        // Reset the book keeper.
        // TEMP 1 is for the main method argument of type "String[]",
        // TEMP 2, TEMP 3, ... are for the main method local variables (if any exist).
        this.bookKeeper.resetLabel();
        this.bookKeeper.resetTemp(localVariablesCount + 2);
        this.emitter.emitln("MAIN");
        // Construct the virtual table for each class of the input program in the heap using global labels.
        for (Identifier classId: symbolTable) {
            ClassDeclaration classDeclaration = symbolTable.lookupClass(classId);
                if (classDeclaration.getMethodCount() > 0) {
                    int vtLabelTemp = this.bookKeeper.getTemp();
                    int vtTemp = this.bookKeeper.getTemp();
                    int methodCount = classDeclaration.getMethodCount();
                    this.emitter.emitlni("// Virtual table for %s", classId);
                    this.emitter.emitlni("MOVE TEMP %d %s_vTable", vtLabelTemp, classId);
                    this.emitter.emitlni("MOVE TEMP %d HALLOCATE %d", vtTemp, methodCount << 2);
                    this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d", vtLabelTemp, vtTemp);
                    int offset = 0;
                    for (Identifier methodId: classDeclaration) {
                        MethodDeclarationInfo methodInfo = classDeclaration.getMethodInfo(methodId);
                        MethodDeclaration method = methodInfo.getMethodDeclaration();
                        int methodTemp = this.bookKeeper.getTemp();
                        this.emitter.emitlni("MOVE TEMP %d %s_%s", methodTemp, method.getOwnerClass(), methodId);
                        this.emitter.emitlni("HSTORE TEMP %d %d TEMP %d", vtTemp, offset, methodTemp);
                        offset += 4;
                    }
                }
        }
        // Visit and generate code for the main method statements.
        n.f15.accept(this, symbolTable);
        this.emitter.emitln("END");
        // Job done. Leave the main method and main class scope.
        symbolTable.leaveMainMethod();
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.ClassDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the class type name (identifier)
        Identifier typeName = n.f1.accept(this.identifierCollector);
        // Enter the class scope within the symbol table.
        symbolTable.enterClass(typeName);
        // Visit the method declarations of the current class.
        n.f4.accept(this, symbolTable);
        // Job done. Leave the class scope.
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.ClassExtendsDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the class type name (identifier)
        Identifier typeName = n.f1.accept(this.identifierCollector);
        // Enter the class scope within the symbol table.
        symbolTable.enterClass(typeName);
        // Visit the method declarations of the current class.
        n.f6.accept(this, symbolTable);
        // Job done. Leave the class scope.
        symbolTable.leaveClass();
    }

    @Override
    public void visit(minijava.syntaxtree.MethodDeclaration n, SymbolTable symbolTable) throws Exception {
        // Get the method identifier
        Identifier methodIdentifier = n.f2.accept(this.identifierCollector);
        // Enter the method scope within the symbol table.
        MethodDeclaration currentMethod = symbolTable.enterMethod(methodIdentifier);
        // Get arguments count. Add 1 for this.
        int argumentsCount = currentMethod.getArgumentsCount() + 1;
        // Get local variables count.
        int localVariablesCount = currentMethod.getLocalVariablesCount();
        // Reset the book keeper.
        this.bookKeeper.resetLabel();
        this.bookKeeper.resetTemp(argumentsCount + localVariablesCount);
        this.emitter.nl();
        this.emitter.emitln("%s_%s [%d]", symbolTable.getCurrentClass(), currentMethod.getName(), argumentsCount);
        this.emitter.emitln("BEGIN");
        // Visit and generate code for the method statements.
        n.f8.accept(this, symbolTable);
        // Visit and evaluate the method return expression.
        int retReg = n.f10.accept(this.expressionEvaluator, symbolTable);
        this.emitter.emitln("RETURN");
        this.emitter.emitlni("TEMP %d", retReg);
        this.emitter.emitln("END");
        // Job done. Leave the method scope.
        symbolTable.leaveMethod();
    }

    // Statements -->

    @Override
    public void visit(minijava.syntaxtree.AssignmentStatement n, SymbolTable symbolTable) throws Exception {
        // Visit the assignment right operand (which is an Expression).
        int reg = n.f2.accept(this.expressionEvaluator, symbolTable);
        // Visit the assignment left operand (which is an Identifier).
        n.f0.accept(this, symbolTable);
        this.emitter.emitln("TEMP %d", reg);
    }

    @Override
    public void visit(minijava.syntaxtree.ArrayAssignmentStatement n, SymbolTable symbolTable) throws Exception {
        this.emitter.emitlni("// Start of array assignment -->");
        int arrayReg = n.f0.accept(this.expressionEvaluator, symbolTable);
        // Visit the assignment right operand (which is an Expression).
        int reg = n.f5.accept(this.expressionEvaluator, symbolTable);
        // Evaluate the array access.
        int reqIndexReg = n.f2.accept(this.expressionEvaluator, symbolTable);
        // Get needed labels and temps.
        int escapeNegativeLabel = this.bookKeeper.getLabel();
        int escapeOutOfBoundsLabel = this.bookKeeper.getLabel();
        int lengthReg = this.bookKeeper.getTemp();
        int condReg = this.bookKeeper.getTemp();
        // Check whether the requested index is positive.
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d 0", condReg, reqIndexReg);
        this.emitter.emitlni("CJUMP TEMP %d L%d", condReg, escapeNegativeLabel);
        this.emitter.emitlni("ERROR");
        this.emitter.emit("L%d", escapeNegativeLabel);
        // Extract the array length.
        this.emitter.emitlni("HLOAD TEMP %d TEMP %d 0", lengthReg, arrayReg);
        // Check whether the requested index is within the array bounds.
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d TEMP %d", condReg, lengthReg, reqIndexReg);
        this.emitter.emitlni("CJUMP TEMP %d L%d", condReg, escapeOutOfBoundsLabel);
        this.emitter.emitlni("ERROR");
        this.emitter.emit("L%d", escapeOutOfBoundsLabel);
        // Compute the requested index address.
        int offsetReg = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d 1", offsetReg, reqIndexReg);
        this.emitter.emitlni("MOVE TEMP %d TIMES TEMP %d 4", offsetReg, offsetReg);
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d TEMP %d", offsetReg, offsetReg, arrayReg);
        this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d", offsetReg, reg);
        this.emitter.emitlni("// <-- End of array assignment");
    }

    @Override
    public void visit(minijava.syntaxtree.IfStatement n, SymbolTable symbolTable) throws Exception {
        // Get needed labels and temps.
        int elseLabel = this.bookKeeper.getLabel();
        int escapeLabel = this.bookKeeper.getLabel();
        this.emitter.emitlni("// Start of if statement -->");
        // Evaluate condition.
        int condReg = n.f2.accept(this.expressionEvaluator, symbolTable);
        // If <condition>
        this.emitter.emitlni("CJUMP TEMP %d L%d", condReg, elseLabel);
        // Visit and produce code for the if block statements.
        n.f4.accept(this, symbolTable);
        // Skip the else part.
        this.emitter.emitlni("JUMP L%d", escapeLabel);
        // Else...
        this.emitter.emit("L%d", elseLabel);
        // Visit and produce code for the else block statements.
        n.f6.accept(this, symbolTable);
        // End of if statement.
        this.emitter.emit("L%d", escapeLabel);
        this.emitter.emitlni("NOOP");
        this.emitter.emitlni("// <-- End of if statement");
    }

    @Override
    public void visit(minijava.syntaxtree.WhileStatement n, SymbolTable symbolTable) throws Exception {
        // Get needed labels and temps.
        int loopLabel = this.bookKeeper.getLabel();
        int breakLabel = this.bookKeeper.getLabel();
        this.emitter.emitlni("// Start of while statement -->");
        // While...
        this.emitter.emit("L%d", loopLabel);
        // Evaluate condition.
        int condReg = n.f2.accept(this.expressionEvaluator, symbolTable);
        // If condition is false, goto break label.
        this.emitter.emitlni("CJUMP TEMP %d L%d", condReg, breakLabel);
        // Visit and produce code for the while loop statements.
        n.f4.accept(this, symbolTable);
        // Loop again.
        this.emitter.emitlni("JUMP L%d", loopLabel);
        // Loop broken.
        this.emitter.emit("L%d", breakLabel);
        this.emitter.emitlni("NOOP");
        this.emitter.emitlni("// <-- End of while statement");
    }

    @Override
    public void visit(minijava.syntaxtree.PrintStatement n, SymbolTable symbolTable) throws Exception {
        int reg = n.f2.accept(this.expressionEvaluator, symbolTable);
        this.emitter.emitlni("PRINT TEMP %d", reg);
    }

    /**
     * This visitor will only visit lvalue assignment identifiers.
     * This means that the Identifier visited will represent the memory location where the rvalue needs to be stored.
     */
    @Override
    public void visit(minijava.syntaxtree.Identifier n, SymbolTable symbolTable) throws Exception {
        VariableDeclarationInfo varInfo;
        // Get an Identifier object for the identifier we visit.
        Identifier identifier = n.accept(this.identifierCollector);
        // Check whether the resembling variable is defined within the current method.
        varInfo = symbolTable.getCurrentMethod().getVariableInfo(identifier);
        if (varInfo.getVariableDeclaration() != null) {  // If it is...
            this.emitter.emiti("MOVE TEMP %d ", varInfo.getDeclarationIndex() + 1);
        } else {
            varInfo = symbolTable.getCurrentClass().getVariableInfo(identifier);
            int offset = (varInfo.getDeclarationIndex() + 1) << 2;
            this.emitter.emiti("HSTORE TEMP 0 %d ", offset);
        }
    }

    // <-- Statements

}
