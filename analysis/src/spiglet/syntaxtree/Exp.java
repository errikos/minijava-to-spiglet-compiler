//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package spiglet.syntaxtree;

/**
 * Grammar production:
 * f0 -> Call()
 *       | HAllocate()
 *       | BinOp()
 *       | SimpleExp()
 */
public class Exp implements Node {
   public NodeChoice f0;

   public Exp(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(spiglet.visitor.Visitor v) throws Exception {
      v.visit(this);
   }
   public <R,A> R accept(spiglet.visitor.GJVisitor<R,A> v, A argu) throws Exception {
      return v.visit(this,argu);
   }
   public <R> R accept(spiglet.visitor.GJNoArguVisitor<R> v) throws Exception {
      return v.visit(this);
   }
   public <A> void accept(spiglet.visitor.GJVoidVisitor<A> v, A argu) throws Exception {
      v.visit(this,argu);
   }
}

