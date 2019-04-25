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
  public ArrayList<SLNode<K, V>> front() {
    return front.next;
  }

  /**
   * The comparator used to determine the ordering in the list.
   */
  Comparator<K> comparator;

  /**
   * The number of values in the list.
   */
  int size;
  /**
   * The number of steps
   */
  int stepNum;

  /**
   * Current height / Current heighest level in list
   */
  int height;

  /**
   * The probability used to determine the height of nodes.
   */
  double prob = 0.5;

  /**
   * front is the head pointer of the list (pointing to other elements)
   */
  SLNode<K, V> front;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new skip list that orders values using the specified comparator.
   */
  public SkipList(Comparator<K> comparator) {
    // create the front of the list, which will point to all elements in the list
    this.front = new SLNode<K, V>(null, null, INITIAL_HEIGHT);
    // set all next pointers of frontNode to null
    for (int i = 0; i < INITIAL_HEIGHT; i++) {
      this.front.next.set(i, null);
    }
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
  @SuppressWarnings("unchecked")
  public V set(K key, V value) {
    // if key is null, throw exception
    if (key == null) {
      throw new NullPointerException("null key");
    }

    // CASE 1: List is either empty or newNode precedes first Node (which front is pointing to)
    // Check whether front.next(0) is null first because this may fail first
    if (this.front.next(0) == null || precede(key, this.front.next(0).key)) {
      // make newNode with random height
      SLNode<K, V> newNode = new SLNode<K, V>(key, value, randomHeight());
      // setting next pointers of newNode to what front used to points to
      // front points to newNode at all levels
      for (int i = 0; i < newNode.getHeight(); i++) {
        newNode.setNext(i, front.next(i));
        // front points to newNode
        front.setNext(i, newNode);
      }
      // update height if newNode's height is higher than current height
      if (newNode.getHeight() > this.height) {
        this.height = newNode.getHeight();
      }
      this.size++;
      return null;
    } else {
      // CASE 2: Unempty List
      // update arrayList contains previous pointers of element will be inserted/updated
      ArrayList<SLNode<K, V>> update = (ArrayList<SLNode<K, V>>) this.front.next.clone();
      // start iterating from front
      SLNode<K, V> finger = this.front;
      // level starts from height - 1 to 0
      // Case 2A: Check if Node with key exists
      for (int level = this.height - 1; level >= 0; level--) {
        // iterating while current != null
        // next node after current at level != null
        // next node's key precedes current key
        while (finger != null && finger.next(level) != null
            && precede(finger.next(level).key, key)) {
          finger = finger.next(level);
        }

<<<<<<< HEAD
        // going down level when not found a match or cant keep going (next = null)
=======
        // we found a matched element (next != null) and key matched finger.next(level).key
>>>>>>> 11b3e05443343dcb6a6cadda0a971c9eb100dfbc
        if (finger.next(level) != null && key.equals(finger.next(level).key)) {
          V returnValue = finger.next(level).value;
          finger.next(level).value = value;
          return returnValue;
        } else {
<<<<<<< HEAD
          // if we change level, we add finger to update 
=======
          // change level, we add previous pointers to update
>>>>>>> 11b3e05443343dcb6a6cadda0a971c9eb100dfbc
          update.set(level, finger);
        }
      }
      // Case 2B: no key existed, insert a new element
      SLNode<K, V> newNode = new SLNode<K, V>(key, value, randomHeight());
      this.size++;
<<<<<<< HEAD
      if (newNode.getHeight() > this.height) {
        this.height = newNode.getHeight();
      }
      // Wire old nodes with new node
=======
      // update skip list's height
      if (newNode.getHeight() > this.height) {
        this.height = newNode.getHeight();
      }
      // resetting pointers all levels from 0 to newNode's height
>>>>>>> 11b3e05443343dcb6a6cadda0a971c9eb100dfbc
      for (int i = 0; i < newNode.getHeight(); i++) {
        if (update.get(i) == null) {
          front.setNext(i, newNode);
        } else {
          newNode.setNext(i, update.get(i).next(i));
          update.get(i).setNext(i, newNode);
        }
      }
      return null;
    }

  } // set(K,V)

  @Override
  public V get(K key) {
    if (this.height == 0) {
      throw new IndexOutOfBoundsException("The key was not found.");
    }
    if (key == null) {
      throw new NullPointerException("Inavlid key is null");
    }
    SLNode<K, V> finger = this.front;
    for (int level = this.height - 1; level >= 0; level--) {
      while (finger.next(level) != null && precede(finger.next(level).key, key)) {
        finger = finger.next(level);
      }

      if (finger.next(level) != null && finger.next(level).key.equals(key)) {
        return finger.next(level).value;
      }
    }
    // break out of the loop, we reached the bottom, key is not in the list
    throw new IndexOutOfBoundsException("The key was not found.");
  } // get(K,V)

  @Override
  public int size() {
    return this.size;
  } // size()

  @Override
  public boolean containsKey(K key) {

    try {
      get(key);
      return true;
    } catch (Exception e) {
      return false;
    }
  } // containsKey(K)

  @SuppressWarnings("unchecked")
  @Override
  public V remove(K key) {
    // if key == null, throw exception
    if (key == null) {
      throw new NullPointerException("null key");
    }
    // if list empty, nothing to remove
    if (front.next(0) == null) {
      return null;
    }
    // update is arraylist of previouis pointers pointing to thing will be removed
    ArrayList<SLNode<K, V>> update = (ArrayList<SLNode<K, V>>) this.front.next.clone();
    SLNode<K, V> temp = this.front;
    for (int currentLevel = this.height - 1; currentLevel >= 0; currentLevel--) {
      while (temp != null && temp.next(currentLevel) != null
          && precede(temp.next(currentLevel).key, key)) {
        temp = temp.next(currentLevel);
      }
      update.set(currentLevel, temp);
    }

    // if there are no node with key in the list, return null
    if (temp.next(0) == null || precede(key, temp.next(0).key)) {
      return null;
    } else {
      // wire nodes before and after the deleted node, update size, update height
      SLNode<K, V> toDelete = temp.next(0);
      this.size--;
      // save the height of the deleted node before we delete that node.
      int oldHeight = temp.next(0).getHeight();
      for (int i = 0; i < oldHeight; i++) {
        // wire things together
        if (update.get(i) == toDelete) {
          // wire dummy to whatever behind deleted node;
          this.front.setNext(i, update.get(i).next(i).next(i));
        } else {
          update.get(i).setNext(i, update.get(i).next(i).next(i));
        }
      }

      if (oldHeight >= this.height) {
        int newHeight = 0;

        while (newHeight <= INITIAL_HEIGHT && this.front.next(newHeight) != null) {
          newHeight++;
        }
        this.height = newHeight;
      }
      return toDelete.value;
    }
  } // remove(K)

  @Override
  public Iterator<K> keys() {
    return new Iterator<K>() {
      Iterator<SLNode<K, V>> nodeIterator = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nodeIterator.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nodeIterator.next().key;
      } // next()

      @Override
      public void remove() {
        nodeIterator.remove();
      } // remove()
    };
  } // keys()

  @Override
  public Iterator<V> values() {
    return new Iterator<V>() {
      Iterator<SLNode<K, V>> valIterator = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return valIterator.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return valIterator.next().value;
      } // next()

      @Override
      public void remove() {
        valIterator.remove();
      } // remove()
    };
  } // values()

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Iterator<SLNode<K, V>> nodes = this.nodes();
    SLNode<K, V> current;
    while (nodes.hasNext()) {
      current = nodes.next();
      action.accept(current.key, current.value);
    }

  } // forEach

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+

  /**
   * Dump the tree to some output location.
   */

  public String toString() {
    String builder = "";
    Iterator<SLNode<K, V>> it = this.nodes();
    while (it.hasNext()) {
      SLNode<K, V> current = it.next();
      builder += ", (" + current.key.toString() + " " + current.value.toString() + ")";
    }
    return builder;
  }

  public void dump(PrintWriter pen) {
    String leading = "          ";

    SLNode<K, V> current = front().get(0);

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
    return Math.min(result, INITIAL_HEIGHT);
  } // random\Height()

  /**
   * Get an iterator for all of the nodes. (Useful for implementing the other iterators.)
   */
  Iterator<SLNode<K, V>> nodes() {
    return new Iterator<SLNode<K, V>>() {

      /**
       * A reference to the next node to return.
       */
      SLNode<K, V> next = SkipList.this.front().get(0);

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
  }

  private boolean precede(K key1, K key2) {
    return this.comparator.compare(key1, key2) < 0;
  }

  // nodes()
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
  /*
   * The number of steps
   */


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
  /* Taken from Sam Rebelsky's Eboard */
  public SLNode<K, V> next(int i) {
    return this.next.get(i);
  }

  /* taken from Sam Rebelsky's eboard */
  public void setNext(int i, SLNode<K, V> newNode) {
    this.next.set(i, newNode);
  }

  public int getHeight() {
    return this.next.size();
  }
} // SLNode<K,V>
