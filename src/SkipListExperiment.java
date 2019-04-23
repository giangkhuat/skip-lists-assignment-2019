import java.io.PrintWriter;

public class SkipListExperiment {
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    SkipList<Integer, String> list = new SkipList<Integer, String> ();
    list.set(0, "hello");
    list.set(3, "abc");
    list.set(2, "haha");
    list.set(2, "gg");
  //  list.set(1, "jjj");
    list.dump(pen);
    //System.out.println(list.toString());
 //   list.set(1, "jjj");
    
  }
}

