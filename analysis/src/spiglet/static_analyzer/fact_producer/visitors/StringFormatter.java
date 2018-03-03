package spiglet.static_analyzer.fact_producer.visitors;

import spiglet.visitor.GJNoArguDepthFirst;

/**
 * Simple visitor that returns the String representation of a Spiglet expression.
 */
public class StringFormatter extends GJNoArguDepthFirst<String> {

    public String visit(spiglet.syntaxtree.Call n) throws Exception {
        String simpleExp = n.f1.accept(this);
        String args = n.f3.nodes.elementAt(0).accept(this);
        int len = n.f3.nodes.size();
        for (int i = 1; i != len; ++i) {
            args = args.concat(" ").concat(n.f3.nodes.elementAt(i).accept(this));
        }
        return String.format("CALL %s (%s)", simpleExp, args);
    }

    public String visit(spiglet.syntaxtree.HAllocate n) throws Exception {
        String simpleExp = n.f1.accept(this);
        return String.format("HALLOCATE %s", simpleExp);
    }

    public String visit(spiglet.syntaxtree.BinOp n) throws Exception {
        String operator = n.f0.accept(this);
        String temp = n.f1.accept(this);
        String simpleExp = n.f2.accept(this);
        return String.format("%s %s %s", operator, temp, simpleExp);
    }

    public String visit(spiglet.syntaxtree.Temp n) throws Exception {
        String tempStr = "TEMP " + n.f1.accept(this);
        return tempStr;
    }

    public String visit(spiglet.syntaxtree.IntegerLiteral n) throws Exception {
        return n.f0.accept(this);
    }

    public String visit(spiglet.syntaxtree.Label n) throws Exception {
        return n.f0.accept(this);
    }

    public String visit(spiglet.syntaxtree.NodeToken n) throws Exception {
        return n.tokenImage;
    }

}
