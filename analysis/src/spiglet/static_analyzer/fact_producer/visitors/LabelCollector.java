package spiglet.static_analyzer.fact_producer.visitors;

import spiglet.visitor.DepthFirstVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple visitor that collects the labels of a procedure and maps them to their instruction count.
 */
public class LabelCollector extends DepthFirstVisitor {

    private final Map<String, Integer> labels;
    private int currentInstruction;

    public LabelCollector() {
        this.labels = new HashMap<>();
        this.currentInstruction = 0;
    }

    public void visit(spiglet.syntaxtree.Procedure n) throws Exception {
        this.currentInstruction = 0;
        n.f4.accept(this);
    }

    public void visit(spiglet.syntaxtree.Stmt n) throws Exception {
        // Just increment instruction counter.
        this.currentInstruction++;
        // Do not visit the statement, as we do not want to visit any Label that may be inside a statement.
    }

    public void visit(spiglet.syntaxtree.Label n) throws Exception {
        String label = n.f0.tokenImage;
        if (label.matches("^L\\d+$")) {
            this.labels.put(label, this.currentInstruction + 1);
        }
    }

    public int getLabelInstruction(String label) {
        return this.labels.get(label);
    }

    public void reset() {
        this.labels.clear();
        this.currentInstruction = 0;
    }

}
