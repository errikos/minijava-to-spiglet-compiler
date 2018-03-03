//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package spiglet.syntaxtree;

/**
 * Grammar production:
 * f0 -> "HLOAD"
 * f1 -> Temp()
 * f2 -> Temp()
 * f3 -> IntegerLiteral()
 */
public class HLoadStmt implements Node {
   public NodeToken f0;
   public Temp f1;
   public Temp f2;
   public IntegerLiteral f3;

   public HLoadStmt(NodeToken n0, Temp n1, Temp n2, IntegerLiteral n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public HLoadStmt(Temp n0, Temp n1, IntegerLiteral n2) {
      f0 = new NodeToken("HLOAD");
      f1 = n0;
      f2 = n1;
      f3 = n2;
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

