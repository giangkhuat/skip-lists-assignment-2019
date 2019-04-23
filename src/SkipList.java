import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * An implementation of skip lists.
 */
public class SkipList<K, V> implements SimpleMap<K, V> {

  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The initial height of the skip list.
   */
  static final int INITIAL_HEIGHT = 16;


  // +---------------+-----------------------------------------------
  // | Static Fields |
  // +---------------+

  static Random rand = new Random();

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Pointers to all the front elements.
   */
  ArrayList<SLNode<K, V>> front;

  /**
   * The comparator used to determine the ordering in the list.
   */
  Comparator<K> comparator;

  /**
   * The number of values in the list.
   */
  int size;

  /**
   * The current height of the skiplist.
   */
  int height;

  /**
   * The probability used to determine the height of nodes.
   */
  double prob = 0.5;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new skip list that orders values using the specified comparator.
   */
  public SkipList(Comparator<K> comparator) {
    this.front = new ArrayList<SLNode<K, V>>(INITIAL_HEIGHT);
    for (int i = 0; i < INITIAL_HEIGHT; i++) {
      front.add(null);
    } // for
    this.comparator = comparator;
    this.size = 0;
    this.height = INITIAL_HEIGHT;
  } // SkipList(Comparator<K>)

  /**
   * Create a new skip list that orders values using a not-very-clever default comparator.
   */
  public SkipList() {
    this((k1, k2) -> k1.toString().compareTo(k2.toString()));
  } // SkipList()

  public void resizeUpdate(ArrayList<SLNode<K, V>> update, int size) {
    for (int i = 0; i < size; i++) {
      update.add(null);
    } // for
  }
  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  @Override
  public V set(K key, V value) {
    // update is an arary of pointers
    ArrayList<SLNode<K, V>> update = new ArrayList<SLNode<K, V>>(this.height);
    // put something in update
    resizeUpdate(update, this.height);
    
    /*
    for (int i = 0; i < this.height; i++) {
      update.add(null);
    } // for
    */
    // update = (ArrayList<SLNode<K,V>>) this.front.clone();
    // current node points to header
    
    SLNode<K, V> listHeader = this.front.get(this.height - 1);
    SLNode<K, V> current = listHeader;

    // check if current is null ( list is empty)
    if (current == null) {
      int newHeight = randomHeight();
      current = new SLNode(key, value, newHeight);
      for (int i = this.height; i < newHeight; i++) {
        this.front.add(i, current);
      }
      for (int i = 0; i < this.height; i++) {
        this.front.set(i, current);
      }
      this.height = newHeight;
      this.size++;
    } else { // list is not empty
      for (int i = this.height - 1; i >= 0; i--) {
        // we add condition if current.next.get)i != null to not assume it has a next
        while (current.next.get(i) != null
            && this.comparator.compare(current.next.get(i).key, key) < 0) {
          current = current.next.get(i);
        }
        // i is the index/level that update
        update.set(i, current);
      }
      // current is now pointing to the element before the position we are about to set
      // at bottom level.
      // CASE 1: We found an element with matched key, there is a next
      if (current.next.get(0) != null
          && this.comparator.compare(current.next.get(0).key, key) == 0) {
        current = current.next.get(0);
        V val = current.value;
        current.value = value;
        // return previous value of node
        return val;
      } else {
        // Case 2: We INSERTED a new element
        // increment size
        this.size++;
        // generate new height
        int newHeight = randomHeight();
        // create the first columns of listHeaders at new levels
        if (newHeight > this.height) {
          for (int i = this.height; i < newHeight; i++) {
            update.set(i, listHeader);
          }
        }
        this.height = newHeight;
        SLNode<K, V> newNode = new SLNode<K, V>(key, value, newHeight);
        for (int i = 0; i < newHeight; i++) {
          // connect newNode next fields with update next pointers
          newNode.next.set(i, update.get(i).next.get(i));
          // setting the next pointer of current (but all levels) to new Node
          update.get(i).next.set(i, newNode);
        }
      }
    }

    return null;
  } // set(K,V)

  @Override
  public V get(K key) {
    if (key == null) {
      throw new NullPointerException("null key");
    } // if
    // TODO Auto-generated method stub
    return null;
  } // get(K,V)

  @Override
  public int size() {
    return this.size;
  } // size()

  @Override
  public boolean containsKey(K key) {
    SLNode<K, V> current = this.front.get(this.height - 1);
    // loop invariant: current.key < key
    for (int i = this.height - 1; i >= 0; i--) {
      while (this.comparator.compare(current.next.get(i).key, key) < 0
          && current.next.get(i) != null) {
        current = current.next.get(i);
      }
    }
    // we reach bottom level and should be in front of the element that we are searching
    current = current.next.get(0);
    // if key of next element matches search key, return true
    if (this.comparator.compare(current.key, key) == 0) {
      return true;
    }
    return false;
  } // containsKey(K)

  @Override
  public V remove(K key) {
    // TODO Auto-generated method stub
    return null;
  } // remove(K)

  @Override
  public Iterator<K> keys() {
    return new Iterator<K>() {
      Iterator<SLNode<K, V>> nit = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nit.next().key;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // keys()

  @Override
  public Iterator<V> values() {
    return new Iterator<V>() {
      Iterator<SLNode<K, V>> nit = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return nit.next().value;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // values()

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    // TODO Auto-generated method stub

  } // forEach

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+

  /**
   * Dump the tree to some output location.
   */


  public void dump(PrintWriter pen) {
    String leading = "          ";

    SLNode<K, V> current = front.get(0);

    // Print some X's at the start
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" X");
    } // for
    pen.println();
    printLinks(pen, leading);

    while (current != null) {
      // Print out the key as a fixed-width field.
      // (There's probably a better way to do this.)
      String str;
      if (current.key == null) {
        str = "<null>";
      } else {
        str = current.key.toString();
      } // if/else
      if (str.length() < leading.length()) {
        pen.print(leading.substring(str.length()) + str);
      } else {
        pen.print(str.substring(0, leading.length()));
      } // if/else

      // Print an indication for the links it has.
      for (int level = 0; level < current.next.size(); level++) {
       pen.print(" " + current.value + " ");
        pen.print("-*");
      } // for
      // Print an indication for the links it lacks.
      for (int level = current.next.size(); level < this.height; level++) {
        pen.print(" |");
      } // for
      pen.println();
      printLinks(pen, leading);

      current = current.next.get(0);
    } // while

    // Print some O's at the start
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" O");
    } // for
    pen.println();

  } // dump(PrintWriter)

  /**
   * Print some links (for dump).
   */
  void printLinks(PrintWriter pen, String leading) {
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" |");
    } // for
    pen.println();
  } // printLinks


  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Pick a random height for a new node.
   */
  int randomHeight() {
    int result = 1;
    while (rand.nextDouble() < prob) {
      result = result + 1;
    }
    return result;
  } // randomHeight()

  /**
   * Get an iterator for all of the nodes. (Useful for implementing the other iterators.)
   */
  Iterator<SLNode<K, V>> nodes() {
    return new Iterator<SLNode<K, V>>() {

      /**
       * A reference to the next node to return.
       */
      SLNode<K, V> next = SkipList.this.front.get(0);

      @Override
      public boolean hasNext() {
        return this.next != null;
      } // hasNext()

      @Override
      public SLNode<K, V> next() {
        if (this.next == null) {
          throw new IllegalStateException();
        }
        SLNode<K, V> temp = this.next;
        this.next = this.next.next.get(0);
        return temp;
      } // next();
    }; // new Iterator
  } // nodes()

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

} // class SkipList


/**
 * Nodes in the skip list.
 */
class SLNode<K, V> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The key.
   */
  K key;

  /**
   * The value.
   */
  V value;

  /**
   * Pointers to the next nodes.
   */
  ArrayList<SLNode<K, V>> next;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new node of height n with the specified key and value.
   */
  public SLNode(K key, V value, int n) {
    this.key = key;
    this.value = value;
    this.next = new ArrayList<SLNode<K, V>>(n);
    for (int i = 0; i < n; i++) {
      this.next.add(null);
    } // for
  } // SLNode(K, V, int)

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+


} // SLNode<K,V>
