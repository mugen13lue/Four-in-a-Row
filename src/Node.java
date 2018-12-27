
public class Node implements Comparable<Node>
{
   private Point pt;
   private Node prior;
   private int h;//hueristic
   private int g;//distance from start
   private int f;//total dist. g+h

   public Node(Point pt, Node prior, int g, int h, int f)
   {
      this.pt = pt;
      this.prior = prior;
      this.h = h;
      this.g = g;
      this.f = f;
   }

   public int compareTo(Node neighbor)
   {
      return f - neighbor.getF();
   }

   public String toString()
   {
      return "Point: " + pt + "Prior: " + prior + "h: " + h + "g: " + g + "f: " + f;
   }

   public Node getPrior() {return prior;}
   public Point getPt() {return pt;}
   public int getG() {return g;}
   public int getF() {return f;}
}

