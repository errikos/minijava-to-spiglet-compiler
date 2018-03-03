package spiglet.static_analyzer.fact_producer.visitors;

import spiglet.static_analyzer.fact_producer.structures.Emitter;
import spiglet.syntaxtree.Node;
import spiglet.visitor.DepthFirstVisitor;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * The visitor the generates the facts for the Datalog analysis.
 */
public class FactGenerator extends DepthFirstVisitor {

    private final StringFormatter stringFormatter;
    private final LabelCollector labelCollector;
    private String currentProcedure;
    private final Set<Integer> currentProcedureVars;
    private int currentInstruction;
    private String currentInstructionLabel;
    private int nextIntstruction;

    private final Emitter instructionEmitter;
    private final Emitter varEmitter;
    private final Emitter nextEmitter;
    private final Emitter varMoveEmitter;
    private final Emitter constMoveEmitter;
    private final Emitter varUseEmitter;
    private final Emitter varDefEmitter;

    public FactGenerator(String outputDir) throws Exception {
        this.stringFormatter = new StringFormatter();
        this.labelCollector = new LabelCollector();
        this.currentProcedure = "MAIN";
        this.currentProcedureVars = new HashSet<>();
        this.currentInstruction = 0;
        this.currentInstructionLabel = null;
        this.nextIntstruction = 0;
        this.instructionEmitter = new Emitter(new FileWriter(String.format("%s/instruction.iris", outputDir)));
        this.varEmitter = new Emitter(new FileWriter(String.format("%s/var.iris", outputDir)));
        this.nextEmitter = new Emitter(new FileWriter(String.format("%s/next.iris", outputDir)));
        this.varMoveEmitter = new Emitter(new FileWriter(String.format("%s/varMove.iris", outputDir)));
        this.constMoveEmitter = new Emitter(new FileWriter(String.format("%s/constMove.iris", outputDir)));
        this.varUseEmitter = new Emitter(new FileWriter(String.format("%s/varUse.iris", outputDir)));
        this.varDefEmitter = new Emitter(new FileWriter(String.format("%s/varDef.iris", outputDir)));
    }

    // Emission methods -->

    private void emitInstruction(String instruction) throws Exception {
        if (this.currentInstructionLabel != null) {
            instruction = this.currentInstructionLabel.concat(" ").concat(instruction);
        }
        this.instructionEmitter.emitln("instruction(\"%s\", %d, \"%s\").",
                this.currentProcedure, this.currentInstruction, instruction);
    }

    private void emitVar(int var) throws Exception {
        this.varEmitter.emitln("var(\"%s\", \"TEMP %d\").", this.currentProcedure, var);
    }

    private void emitNext(int instruction, int next_instruction) throws Exception {
        this.nextEmitter.emitln("next(\"%s\", %d, %d).", this.currentProcedure, instruction, next_instruction);
    }

    private void emitVarMove(String rd, String rs) throws Exception {
        this.varMoveEmitter.emitln("varMove(\"%s\", %d, \"%s\", \"%s\").",
                this.currentProcedure, this.currentInstruction, rd, rs);
    }

    private void emitConstMove(String rd, String val) throws Exception {
        this.constMoveEmitter.emitln("constMove(\"%s\", %d, \"%s\", %s).",
                this.currentProcedure, this.currentInstruction, rd, val);
    }

    private void emitVarUse(String var) throws Exception {
        this.varUseEmitter.emitln("varUse(\"%s\", %d, \"%s\").", this.currentProcedure, this.currentInstruction, var);
    }

    private void emitVarDef(String var) throws Exception {
        this.varDefEmitter.emitln("varDef(\"%s\", %d, \"%s\").", this.currentProcedure, this.currentInstruction, var);
    }

    // <-- Emission methods

    // Visit methods -->

    public void visit(spiglet.syntaxtree.Goal n) throws Exception {
        // Collect the (label -> instruction) mappings for the MAIN procedure.
        n.f1.accept(this.labelCollector);
        // Visit the MAIN procedure statements.
        n.f1.accept(this);
        // Visit the rest of the procedures.
        n.f3.accept(this);
        // Close all the emission objects.
        this.instructionEmitter.close();
        this.varEmitter.close();
        this.nextEmitter.close();
        this.varMoveEmitter.close();
        this.constMoveEmitter.close();
        this.varUseEmitter.close();
        this.varDefEmitter.close();
    }

    public void visit(spiglet.syntaxtree.Procedure n) throws Exception {
        this.currentProcedure = n.f0.f0.tokenImage;
        this.currentProcedureVars.clear();
        this.currentInstruction = 0;
        this.nextIntstruction = 0;
        // Collect the (label -> instruction) mappings for the procedure.
        this.labelCollector.reset();
        n.f4.accept(this.labelCollector);
        // Visit and produce the instruction facts for the procedure statements.
        n.f4.accept(this);
    }

    public void visit(spiglet.syntaxtree.StmtList n) throws Exception {
        // Visit the first statement of the statement list.
        if (n.f0.size() > 0) {
            n.f0.elementAt(0).accept(this);
            for (int i = 1; i != n.f0.size(); ++i) {
                this.emitNext(this.currentInstruction, this.nextIntstruction);
                n.f0.elementAt(i).accept(this);
            }
        }
    }

    public void visit(spiglet.syntaxtree.Stmt n) throws Exception {
        this.currentInstruction++;
        n.f0.accept(this);
        this.currentInstructionLabel = null;
    }

    public void visit(spiglet.syntaxtree.NoOpStmt n) throws Exception {
        // instruction
        this.emitInstruction("NOOP");
        // next
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.ErrorStmt n) throws Exception {
        // instruction
        this.emitInstruction("ERROR");
        // next
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.CJumpStmt n) throws Exception {
        // instruction
        String temp = n.f1.accept(this.stringFormatter);
        String label = n.f2.accept(this.stringFormatter);
        this.emitInstruction(String.format("CJUMP %s %s", temp, label));
        // var
        n.f1.accept(this);
        // var_use
        this.emitVarUse(temp);
        // next
        this.emitNext(this.currentInstruction, this.labelCollector.getLabelInstruction(label));
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.JumpStmt n) throws Exception {
        // instruction
        String label = n.f1.accept(this.stringFormatter);
        this.emitInstruction(String.format("JUMP %s", label));
        // next
        this.nextIntstruction = this.labelCollector.getLabelInstruction(label);
    }

    public void visit(spiglet.syntaxtree.HStoreStmt n) throws Exception {
        // instruction
        String base = n.f1.accept(this.stringFormatter);
        String offset = n.f2.accept(this.stringFormatter);
        String rs = n.f3.accept(this.stringFormatter);
        this.emitInstruction(String.format("HSTORE %s %s %s", base, offset, rs));
        // var
        n.f1.accept(this);
        n.f3.accept(this);
        // var_use
        this.emitVarUse(base);
        this.emitVarUse(rs);
        // next
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.HLoadStmt n) throws Exception {
        // instruction
        String rd = n.f1.accept(this.stringFormatter);
        String base = n.f2.accept(this.stringFormatter);
        String offset = n.f3.accept(this.stringFormatter);
        this.emitInstruction(String.format("HLOAD %s %s %s", rd, base, offset));
        // var
        n.f1.accept(this);
        n.f2.accept(this);
        // var_use
        this.emitVarUse(base);
        // var_def
        this.emitVarDef(rd);
        // next
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.MoveStmt n) throws Exception {
        // instruction
        String rs = n.f2.accept(this.stringFormatter);
        String rd = n.f1.accept(this.stringFormatter);
        this.emitInstruction(String.format("MOVE %s %s", rd, rs));
        // var
        n.f1.accept(this);
        n.f2.accept(this);
        // var_move - const_move
        if (rs.matches("^TEMP \\d+$")) {  // rs is a variable.
            this.emitVarMove(rd, rs);
            // var_use
            this.emitVarUse(rs);
        } else if (rs.matches("^\\d+$")) {  // rs is an integer.
            this.emitConstMove(rd, rs);
        }
        // var_def
        this.emitVarDef(rd);
        // next
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.PrintStmt n) throws Exception {
        // instruction
        String simpleExp = n.f1.accept(this.stringFormatter);
        this.emitInstruction(String.format("PRINT %s", simpleExp));
        // var
        n.f1.accept(this);
        // var_use
        if (simpleExp.matches("^TEMP \\d+$")) {  // simpleExp is a variable.
            this.emitVarUse(simpleExp);
        }
        // next
        this.nextIntstruction = this.currentInstruction + 1;
    }

    public void visit(spiglet.syntaxtree.StmtExp n) throws Exception {
        // Visit and produce the StmtList instruction facts.
        n.f1.accept(this);
        // Produce the RETURN instruction fact.
        this.currentInstruction++;
        String retSimpleExp = n.f3.accept(this.stringFormatter);
        this.emitInstruction(String.format("RETURN %s", retSimpleExp));
        // next
        this.emitNext(this.currentInstruction - 1, this.currentInstruction);
    }

    public void visit(spiglet.syntaxtree.Call n) throws Exception {
        // var_use
        String simpleExp = n.f1.accept(this.stringFormatter);
        if (simpleExp.matches("^TEMP \\d+$")) {
            this.emitVarUse(simpleExp);
        }
        for (Node node: n.f3.nodes) {
            String var = node.accept(this.stringFormatter);
            this.emitVarUse(var);
        }
    }

    public void visit(spiglet.syntaxtree.HAllocate n) throws Exception {
        // var_use
        String simpleExp = n.f1.accept(this.stringFormatter);
        if (simpleExp.matches("^TEMP \\d+$")) {
            this.emitVarUse(simpleExp);
        }
    }

    public void visit(spiglet.syntaxtree.BinOp n) throws Exception {
        // var_use
        String temp = n.f1.accept(this.stringFormatter);
        String simpleExp = n.f2.accept(this.stringFormatter);
        this.emitVarUse(temp);
        if (simpleExp.matches("^TEMP \\d+$")) {
            this.emitVarUse(simpleExp);
        }
    }

    public void visit(spiglet.syntaxtree.Temp n) throws Exception {
        int var = Integer.parseInt(n.f1.f0.tokenImage);
        if (this.currentProcedureVars.add(var) == true) {
            this.emitVar(var);
        }
    }

    public void visit(spiglet.syntaxtree.Label n) throws Exception {
        String label = n.f0.tokenImage;
        if (label.matches("^L\\d+$")) {
            this.currentInstructionLabel = label;
        }
    }

    // <-- Visit methods

}
