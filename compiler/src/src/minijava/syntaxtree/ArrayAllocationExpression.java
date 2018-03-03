//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package minijava.syntaxtree;

/**
 * Grammar production:
 * f0 -> "new"
 * f1 -> "int"
 * f2 -> "["
 * f3 -> Expression()
 * f4 -> "]"
 */
public class ArrayAllocationExpression implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeToken f2;
   public Expression f3;
   public NodeToken f4;

   public ArrayAllocationExpression(NodeToken n0, NodeToken n1, NodeToken n2, Expression n3, NodeToken n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public ArrayAllocationExpression(Expression n0) {
      f0 = new NodeToken("new");
      f1 = new NodeToken("int");
      f2 = new NodeToken("[");
      f3 = n0;
      f4 = new NodeToken("]");
   }

   public void accept(minijava.visitor.Visitor v) throws Exception {
      v.visit(this);
   }
   public <R,A> R accept(minijava.visitor.GJVisitor<R,A> v, A argu) throws Exception {
      return v.visit(this,argu);
   }
   public <R> R accept(minijava.visitor.GJNoArguVisitor<R> v) throws Exception {
      return v.visit(this);
   }
   public <A> void accept(minijava.visitor.GJVoidVisitor<A> v, A argu) throws Exception {
      v.visit(this,argu);
   }
}

