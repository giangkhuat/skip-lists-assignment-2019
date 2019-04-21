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


  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  @Override
  public V set(K key, V value) {
    // update is an arary of pointers
    ArrayList<SLNode<K, V>> update = new ArrayList<SLNode<K, V>>(INITIAL_HEIGHT);
    // current node points to header
    SLNode<K, V> listHeader = this.front.get(this.height - 1);
    SLNode<K,V> current = listHeader;
    for (int i = this.height; i >= 0; i--) {
      while (this.comparator.compare(current.next.get(i).key, key) < 0 && current.next.get(i) != null) {
        current = current.next.get(i);
        
      } 
      update.add(current);
    }
    // current is now pointing to the element before the position we are about to set
    // at bottoom level.
    current = current.next.get(0);
    if (this.comparator.compare(current.key, key) == 0) {
      V val = current.value;
      current.value = value;
      // return previous value of node
      return val;
    } else {
      int newHeight = randomHeight();
      if (newHeight > this.height) {
        for (int i = this.height ; i <= newHeight; i++) {
          
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
    // Forthcoming
  } // dump(PrintWriter)

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
