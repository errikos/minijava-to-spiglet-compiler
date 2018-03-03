package minijava.spiglet_generation.visitors;

import minijava.semantic_check.structures.*;
import minijava.semantic_check.visitors.IdentifierCollector;
import minijava.spiglet_generation.structures.BookKeeper;
import minijava.spiglet_generation.structures.Emitter;
import minijava.visitor.GJDepthFirst;

import java.util.Stack;
import java.util.Vector;

/**
 * Spiglet code generation visitor for the MiniJava Expressions.
 */
public class ExpressionEvaluator extends GJDepthFirst<Integer, SymbolTable> {

    private final Emitter emitter;
    private final BookKeeper bookKeeper;

    private final IdentifierCollector identifierCollector;
    private final TypeEvaluator typeEvaluator;

    private final Stack<Vector<Integer>> callArgs;

    public ExpressionEvaluator(Emitter emitter, BookKeeper bookKeeper, IdentifierCollector identifierCollector) {
        this.emitter = emitter;
        this.bookKeeper = bookKeeper;
        this.identifierCollector = identifierCollector;
        this.typeEvaluator = new TypeEvaluator(this.identifierCollector);
        this.callArgs = new Stack<>();
    }

    // Expressions -->

    @Override
    public Integer visit(minijava.syntaxtree.AndExpression n, SymbolTable symbolTable) throws Exception {
        // Get needed labels and temps.
        int skipLabel = this.bookKeeper.getLabel();
        int andReg = this.bookKeeper.getTemp();
        // Evaluate the left clause.
        int leftClauseReg = n.f0.accept(this, symbolTable);
        // Evaluate the right clause only if the left clause is true.
        this.emitter.emitlni("CJUMP TEMP %d L%d", leftClauseReg, skipLabel);
        // Evaluate the right clause.
        int rightClauseReg = n.f2.accept(this, symbolTable);
        this.emitter.emit("L%d", skipLabel);
        this.emitter.emitlni("NOOP");
        // The logical and result is the multiplication of the two clause registers.
        this.emitter.emitlni("MOVE TEMP %d TIMES TEMP %d TEMP %d", andReg, leftClauseReg, rightClauseReg);
        return andReg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.CompareExpression n, SymbolTable symbolTable) throws Exception {
        int rs, rt, rd;
        rs = n.f0.accept(this, symbolTable);
        rt = n.f2.accept(this, symbolTable);
        rd = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d TEMP %d", rd, rs, rt);
        return rd;
    }

    @Override
    public Integer visit(minijava.syntaxtree.PlusExpression n, SymbolTable symbolTable) throws Exception {
        int rs, rt, rd;
        rs = n.f0.accept(this, symbolTable);
        rt = n.f2.accept(this, symbolTable);
        rd = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d TEMP %d", rd, rs, rt);
        return rd;
    }

    @Override
    public Integer visit(minijava.syntaxtree.MinusExpression n, SymbolTable symbolTable) throws Exception {
        int rs, rt, rd;
        rs = n.f0.accept(this, symbolTable);
        rt = n.f2.accept(this, symbolTable);
        rd = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d MINUS TEMP %d TEMP %d", rd, rs, rt);
        return rd;
    }

    @Override
    public Integer visit(minijava.syntaxtree.TimesExpression n, SymbolTable symbolTable) throws Exception {
        int rs, rt, rd;
        rs = n.f0.accept(this, symbolTable);
        rt = n.f2.accept(this, symbolTable);
        rd = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d TIMES TEMP %d TEMP %d", rd, rs, rt);
        return rd;
    }

    @Override
    public Integer visit(minijava.syntaxtree.ArrayLookup n, SymbolTable symbolTable) throws Exception {
        int arrayReg = n.f0.accept(this, symbolTable);
        int reqIndexReg = n.f2.accept(this, symbolTable);
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
        // Load the requested value.
        int offsetReg = this.bookKeeper.getTemp();
        int valueReg = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d 1", offsetReg, reqIndexReg);
        this.emitter.emitlni("MOVE TEMP %d TIMES TEMP %d 4", offsetReg, offsetReg);
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d TEMP %d", offsetReg, offsetReg, arrayReg);
        this.emitter.emitlni("HLOAD TEMP %d TEMP %d 0", valueReg, offsetReg);
        return valueReg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.ArrayLength n, SymbolTable symbolTable) throws Exception {
        int arrayReg = n.f0.accept(this, symbolTable);
        int lengthReg = this.bookKeeper.getTemp();
        this.emitter.emitlni("HLOAD TEMP %d TEMP %d 0", lengthReg, arrayReg);
        return lengthReg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.MessageSend n, SymbolTable symbolTable) throws Exception {
        // Get the object location and type.
        int objReg = n.f0.accept(this, symbolTable);
        ClassDeclaration objType = (ClassDeclaration)(n.f0.accept(this.typeEvaluator, symbolTable));
        Identifier methodId = n.f2.accept(this.identifierCollector);
        this.emitter.emitlni("// Requested call on TEMP %d of type %s", objReg, objType);
        // Check for null pointer.
        int elseLabel = this.bookKeeper.getLabel();
        // If objReg < 1
        int nullCheckTemp = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d 1", nullCheckTemp, objReg);
        this.emitter.emitlni("CJUMP TEMP %d L%d", nullCheckTemp, elseLabel);
        // Object pointer is null.
        this.emitter.emitlni("ERROR");
        // Object pointer is not null.
        this.emitter.emit("L%d", elseLabel);
        // Get needed temps.
        int vtReg = this.bookKeeper.getTemp();
        int methodReg = this.bookKeeper.getTemp();
        // Extract method offset.
        MethodDeclarationInfo methodInfo = objType.getMethodInfo(methodId);
        int methodOffset = (methodInfo.getDeclarationIndex()) << 2;
        // Go through the virtual table and load the appropriate method address.
        this.emitter.emitlni("// Load %s.%s()", objType, methodId);
        this.emitter.emitlni("HLOAD TEMP %d TEMP %d 0", vtReg, objReg);
        this.emitter.emitlni("HLOAD TEMP %d TEMP %d %d", methodReg, vtReg, methodOffset);
        // Evaluate arguments.
        this.emitter.emitlni("// Evaluate arguments");
        this.callArgs.push(new Vector<>());
        n.f4.accept(this, symbolTable);
        // Make the call.
        int reg = this.bookKeeper.getTemp();
        this.emitter.emitlni("// Call %s.%s()", objType, methodId);
        this.emitter.emiti("MOVE TEMP %d CALL TEMP %d (TEMP %d", reg, methodReg, objReg);
        // Emit the arguments TEMPs.
        Vector<Integer> args = this.callArgs.pop();
        for (int argReg: args) {
            this.emitter.emit(" TEMP %d", argReg);
        }
        this.emitter.emitln(")");
        return reg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.ExpressionList n, SymbolTable symbolTable) throws Exception {
        this.emitter.emitlni("// Argument %d", this.callArgs.peek().size() + 1);
        int reg = n.f0.accept(this, symbolTable);
        this.callArgs.peek().add(reg);
        n.f1.accept(this, symbolTable);
        return null;
    }

    @Override
    public Integer visit(minijava.syntaxtree.ExpressionTerm n, SymbolTable symbolTable) throws Exception {
        this.emitter.emitlni("// Argument %d", this.callArgs.peek().size() + 1);
        int reg = n.f1.accept(this, symbolTable);
        this.callArgs.peek().add(reg);
        return null;
    }

    // <-- Expressions

    // PrimaryExpressions -->

    @Override
    public Integer visit(minijava.syntaxtree.IntegerLiteral n, SymbolTable symbolTable) throws Exception {
        int reg = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d %s", reg, n.f0.tokenImage);
        return reg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.TrueLiteral n, SymbolTable symbolTable) throws Exception {
        int reg = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d 1", reg);
        return reg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.FalseLiteral n, SymbolTable symbolTable) throws Exception {
        int reg = this.bookKeeper.getTemp();
        this.emitter.emitlni("MOVE TEMP %d 0", reg);
        return reg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.Identifier n, SymbolTable symbolTable) throws Exception {
        int rd;
        VariableDeclarationInfo varInfo;
        // Get an Identifier object for the identifier we visit.
        Identifier identifier = n.accept(this.identifierCollector);
        // Check whether the resembling variable is defined within the current method.
        varInfo = symbolTable.getCurrentMethod().getVariableInfo(identifier);
        if (varInfo.getVariableDeclaration() != null) {  // If it is...
            rd = varInfo.getDeclarationIndex() + 1;
        } else {  // If it's not, then it will be found within the current object.
            varInfo = symbolTable.getCurrentClass().getVariableInfo(identifier);
            int offset = (varInfo.getDeclarationIndex() + 1) << 2;
            rd = this.bookKeeper.getTemp();
            this.emitter.emitlni("HLOAD TEMP %d TEMP 0 %d", rd, offset);
        }
        return rd;
    }

    @Override
    public Integer visit(minijava.syntaxtree.ThisExpression n, SymbolTable symbolTable) throws Exception {
        int reg;
        reg = 0;
        return reg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.ArrayAllocationExpression n, SymbolTable symbolTable) throws Exception {
        int reqSizeReg = n.f3.accept(this, symbolTable);
        // Check whether requested size is < 1.
        int elseLabel = this.bookKeeper.getLabel();
        // If indexReg < 1
        int indexCheckTemp = this.bookKeeper.getTemp();
        this.emitter.emitlni("// Check whether array allocation size is positive");
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d 1", indexCheckTemp, reqSizeReg);
        this.emitter.emitlni("CJUMP TEMP %d L%d", indexCheckTemp, elseLabel);
        // Requested size is < 1.
        this.emitter.emitlni("ERROR");
        // Requested size is not < 1.
        this.emitter.emit("L%d", elseLabel);
        this.emitter.emitlni("// Start of array allocation -->");
        // Get required temps.
        int reqBytesReg = this.bookKeeper.getTemp();
        int allocSizeReg = this.bookKeeper.getTemp();
        int allocBytesReg = this.bookKeeper.getTemp();
        int arrayReg = this.bookKeeper.getTemp();
        // Increment requested size by one, to keep the array length.
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d 1", allocSizeReg, reqSizeReg);
        // Calculate bytes to be allocated (count length entry).
        this.emitter.emitlni("MOVE TEMP %d TIMES TEMP %d 4", allocBytesReg, allocSizeReg);
        // Allocate array into arrayReg.
        this.emitter.emitlni("MOVE TEMP %d HALLOCATE TEMP %d", arrayReg, allocBytesReg);
        // Calculate actual array size in bytes.
        this.emitter.emitlni("MOVE TEMP %d TIMES TEMP %d 4", reqBytesReg, reqSizeReg);
        // Initialize array with zeros.
        this.emitter.emitlni("// Initialize allocated array");
        // Get required labels and temps.
        int startLabel = this.bookKeeper.getLabel();
        int endLabel = this.bookKeeper.getLabel();
        int zeroReg = this.bookKeeper.getTemp();
        int offsetReg = this.bookKeeper.getTemp();
        int condReg = this.bookKeeper.getTemp();
        int writeAddrReg = this.bookKeeper.getTemp();
        // zeroReg = 0.
        this.emitter.emitlni("MOVE TEMP %d 0  // Zero value register", zeroReg);
        // offsetReg = 4.
        this.emitter.emitlni("MOVE TEMP %d 4  // Offset register", offsetReg);
        this.emitter.emit("L%d", startLabel);
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d TEMP %d", condReg, offsetReg, allocBytesReg);
        this.emitter.emitlni("CJUMP TEMP %d L%d", condReg, endLabel);
        // writeAddrReg = arrayReg + offsetReg.
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d TEMP %d", writeAddrReg, arrayReg, offsetReg);
        this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d", writeAddrReg, zeroReg);
        // offsetReg = offsetReg + 4.
        this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d 4", offsetReg, offsetReg);
        this.emitter.emitlni("JUMP L%d", startLabel);
        this.emitter.emit("L%d", endLabel);
        // Store array length in the first array position (index = 0).
        this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d  // Store array length", arrayReg, reqSizeReg);
        this.emitter.emitlni("// <-- End of array allocation");
        return arrayReg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.AllocationExpression n, SymbolTable symbolTable) throws Exception {
        Identifier identifier = n.f1.accept(this.identifierCollector);
        ClassDeclaration classDeclaration = symbolTable.lookupClass(identifier);
        int variableCount = classDeclaration.getVariableCount();
        int objectSize = (variableCount + 1) << 2;
        // Get needed temps.
        int reg = this.bookKeeper.getTemp();
        int vtReg = this.bookKeeper.getTemp();
        int vtAddrReg = this.bookKeeper.getTemp();
        // Allocate memory for the newly allocated object.
        this.emitter.emitlni("// new %s()", identifier);
        this.emitter.emitlni("MOVE TEMP %d HALLOCATE %d", reg, objectSize);
        if (classDeclaration.getMethodCount() > 0) {
            // Put the virtual table label address into vtReg.
            this.emitter.emitlni("MOVE TEMP %d %s_vTable", vtReg, classDeclaration);
            // Load the virtual table address into vtAddrReg.
            this.emitter.emitlni("HLOAD TEMP %d TEMP %d 0", vtAddrReg, vtReg);
            // Store the virtual table address in the beginning of the newly created object.
            this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d", reg, vtAddrReg);
        } else {
            // The object class has no methods, just write null to the virtual table pointer.
            int zeroReg = this.bookKeeper.getTemp();
            this.emitter.emitlni("MOVE TEMP %d 0", zeroReg);
            this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d", reg, zeroReg);
        }
        // Initialize object fields to null.
        if (objectSize > 4) {
            // Get required labels and temps.
            int startLabel = this.bookKeeper.getLabel();
            int endLabel = this.bookKeeper.getLabel();
            int zeroReg = this.bookKeeper.getTemp();
            int offsetReg = this.bookKeeper.getTemp();
            int condReg = this.bookKeeper.getTemp();
            int writeAddrReg = this.bookKeeper.getTemp();
            this.emitter.emitlni("// Start of object fields initialization -->");
            // zeroReg = 0.
            this.emitter.emitlni("MOVE TEMP %d 0  // Zero value register", zeroReg);
            // offsetReg = 4.
            this.emitter.emitlni("MOVE TEMP %d 4  // Offset register", offsetReg);
            this.emitter.emit("L%d", startLabel);
            this.emitter.emitlni("MOVE TEMP %d LT TEMP %d %d", condReg, offsetReg, objectSize);
            this.emitter.emitlni("CJUMP TEMP %d L%d", condReg, endLabel);
            // writeAddrReg = reg + offsetReg.
            this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d TEMP %d", writeAddrReg, reg, offsetReg);
            this.emitter.emitlni("HSTORE TEMP %d 0 TEMP %d", writeAddrReg, zeroReg);
            // offsetReg = offsetReg + 4.
            this.emitter.emitlni("MOVE TEMP %d PLUS TEMP %d 4", offsetReg, offsetReg);
            this.emitter.emitlni("JUMP L%d", startLabel);
            this.emitter.emit("L%d", endLabel);
            this.emitter.emitlni("NOOP");
            this.emitter.emitlni("// <-- End of object fields initialization");
        }
        return reg;
    }

    @Override
    public Integer visit(minijava.syntaxtree.BracketExpression n, SymbolTable symbolTable) throws Exception {
        return n.f1.accept(this, symbolTable);
    }

    // <-- PrimaryExpressions

    @Override
    public Integer visit(minijava.syntaxtree.NotExpression n, SymbolTable symbolTable) throws Exception {
        int reg, clauseReg;
        reg = this.bookKeeper.getTemp();
        clauseReg = n.f1.accept(this, symbolTable);
        this.emitter.emitlni("MOVE TEMP %d LT TEMP %d 1", reg, clauseReg);
        return reg;
    }

}
