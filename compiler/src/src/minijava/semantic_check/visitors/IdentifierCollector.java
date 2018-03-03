package minijava.semantic_check.visitors;

import minijava.semantic_check.structures.Identifier;
import minijava.visitor.GJNoArguDepthFirst;

/**
 * Super specialized visitor that collects identifiers, eg. variable names, method names.
 */
public class IdentifierCollector extends GJNoArguDepthFirst<Identifier> {

    @Override
    public Identifier visit(minijava.syntaxtree.Identifier n) throws Exception {
        return new Identifier(n.f0.beginLine, n.f0.beginColumn, n.f0.tokenImage);
    }

}
