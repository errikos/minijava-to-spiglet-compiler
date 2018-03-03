//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package minijava.syntaxtree;

/**
 * Grammar production:
 * f0 -> ArrayType()
 *       | BooleanType()
 *       | IntegerType()
 *       | Identifier()
 */
public class Type implements Node {
   public NodeChoice f0;

   public Type(NodeChoice n0) {
      f0 = n0;
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

