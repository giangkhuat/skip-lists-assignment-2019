
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import java.lang.*;

/**
 * Some tests of skip lists.
 *
 * @author Samuel A. Rebelsky
 */
public class SkipListTests {

  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * Names of some numbers.
   */
  static final String numbers[] = {"zero", "one", "two", "three", "four", "five", "six", "seven",
      "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
      "seventeen", "eighteen", "nineteen"};

  /**
   * Names of more numbers.
   */
  static final String tens[] =
      {"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * A of strings for tests. (Gets set by the subclasses.)
   */
  SkipList<String, String> strings;

  /**
   * A sorted list of integers for tests. (Gets set by the subclasses.)
   */
  SkipList<Integer, String> ints;

  /**
   * A random number generator for the randomized tests.
   */
  Random random = new Random();

  /**
   * For reporting errors: a list of the operations we performed.
   */
  ArrayList<String> operations;



  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Set up everything. Unfortunately, @BeforeEach doesn't seem to be working, so we do this
   * manually.
   */
  @BeforeEach
  public void setup() {
    this.ints = new SkipList<Integer, String>((i, j) -> i - j);
    this.strings = new SkipList<String, String>((s, t) -> s.compareTo(t));
    this.operations = new ArrayList<String>();
  } // setup

  /**
   * Dump a SkipList to stderr.
   */
  static <K, V> void dump(SkipList<K, V> map) {
    System.err.print("[");
    map.forEach((key, value) -> System.err.println(key + ":" + value + " "));
    System.err.println("]");
  } // dump

  /**
   * Determine if an iterator only returns values in non-decreasing order.
   */
  static <T extends Comparable<T>> boolean inOrder(Iterator<T> it) {
    // Simple case: The empty iterator is in order.
    if (!it.hasNext()) {
      return true;
    }
    // Otherwise, we need to compare neighboring elements, so
    // grab the first element.
    T current = it.next();
    // Step through the remaining elements
    while (it.hasNext()) {
      // Get the next element
      T next = it.next();
      // Verify that the current node <= next
      if (current.compareTo(next) > 0) {
        return false;
      } // if (current > next)
      // Update the current node
      current = next;
    } // while
    // If we've made it this far, everything is in order
    return true;
  } // inOrder(Iterator<T> it)

  /**
   * Generate a value from a string.
   */
  static String value(String str) {
    return str.toUpperCase();
  } // key(String)

  /**
   * Generate a value from a non-negative integer.
   */
  static String value(Integer i) {
    return value(i, false);
  } // value(integer)

  /**
   * Generate a value from a non-negative integer; if skipZero is true, returns "" for zero.
   */
  static String value(Integer i, boolean skipZero) {
    if ((i == 0) && (skipZero)) {
      return "";
    } else if (i < 20) {
      return numbers[i];
    } else if (i < 100) {
      return (tens[i / 10] + " " + value(i % 10, true)).trim();
    } else if (i < 1000) {
      return (numbers[i / 100] + " hundred " + value(i % 100, true)).trim();
    } else if (i < 1000000) {
      return (numbers[i / 1000] + " thousand " + value(i % 1000, true)).trim();
    } else {
      return "really big";
    }
  } // value(i, skipZero)

  // +--------------------+------------------------------------------
  // | Logging operations |
  // +--------------------+

  /**
   * Set an entry in the ints list.
   */
  void set(Integer i) {
    operations.add("set(" + i + ");");
    ints.set(i, value(i));
  } // set(Integer)

  /**
   * Set an entry in the strings list.
   */
  void set(String str) {
    operations.add("set(\"" + str + "\");");
    strings.set(str, value(str));
  } // set(String)

  /**
   * Remove an integer from the ints list.
   */
  void remove(Integer i) {
    operations.add("remove(" + i + ");");
    ints.remove(i);
  } // remove(Integer)

  /**
   * Remove a string from the strings list.
   */
  void remove(String str) {
    operations.add("remove(\"" + str + "\");");
    strings.remove(str);
  } // remove(String)

  /**
   * Log a failure.
   */
  void log(String str) {
    System.err.println(str);
    operations.add("// " + str);
  } // log

  /**
   * Print code from a failing test.
   */
  void printTest() {
    System.err.println("@Test");
    System.err.println("  public void test" + random.nextInt(1000) + "() {");
    for (String op : operations) {
      System.err.println("    " + op);
    } // for
    System.err.println("  }");
    System.err.println();
  } // printTest()

  // +-------------+-----------------------------------------------------
  // | Basic Tests |
  // +-------------+

  /**
   * A really simple test. Add an element and make sure that it's there.
   */
  @Test
  public void simpleTest() {
    setup();
    set("hello");
    assertTrue(strings.containsKey("hello"));
    assertFalse(strings.containsKey("goodbye"));
  } // simpleTest()

  /**
   * Another simple test. The list should not contain anything when we start out.
   */
  @Test
  public void emptyTest() {
    setup();
    assertFalse(strings.containsKey("hello"));
  } // emptyTest()

  // +-----------------+-------------------------------------------------
  // | RandomizedTests |
  // +-----------------+

  /**
   * Verify that a randomly created list is sorted.
   */
  @Test
  public void testOrdered() {
    setup();
    // Add a bunch of values
    for (int i = 0; i < 100; i++) {
      int rand = random.nextInt(1000);
      set(rand);
    } // for
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // if the elements are not in order.
  } // testOrdered()

  /**
   * Verify that a randomly created list contains all the values we added to the list.
   */
  @Test
  public void testContainsOnlyAdd() {
    setup();
    ArrayList<Integer> keys = new ArrayList<Integer>();

    // Add a bunch of values
    for (int i = 0; i < 100; i++) {
      int rand = random.nextInt(200);
      keys.add(rand);
      set(rand);
    } // for i
    // Make sure that they are all there.
    for (Integer key : keys) {
      if (!ints.containsKey(key)) {
        log("contains(" + key + ") failed");
        printTest();
        dump(ints);
        fail(key + " is not in the skip list");
      } // if (!ints.contains(val))
    } // for key
  } // testContainsOnlyAdd()

  /**
   * An extensive randomized test.
   */
  @Test
  public void randomTest() {
    setup();
    // Keep track of the values that are currently in the sorted list.
    ArrayList<Integer> keys = new ArrayList<Integer>();

    // Add a bunch of values
    boolean ok = true;
    for (int i = 0; ok && i < 1000; i++) {
      int rand = random.nextInt(1000);
      // Half the time we add
      if (random.nextBoolean()) {
        if (!ints.containsKey(rand)) {
          set(rand);
        } // if it's not already there.
        if (!ints.containsKey(rand)) {
          log("After adding " + rand + ", contains(" + rand + ") fails");
          ok = false;
        } // if (!ints.contains(rand))
      } // if we add
      // Half the time we remove
      else {
        remove(rand);
        keys.remove((Integer) rand);
        if (ints.containsKey(rand)) {
          log("After removing " + rand + ", contains(" + rand + ") succeeds");
          ok = false;
        } // if ints.contains(rand)
      } // if we remove
      // See if all of the appropriate elements are still there
      for (Integer key : keys) {
        if (!ints.containsKey(key)) {
          log("ints no longer contains " + key);
          ok = false;
          break;
        } // if the value is no longer contained
      } // for each key
    } // for i
    // Dump the instructions if we've encountered an error
    if (!ok) {
      printTest();
      dump(ints);
      fail("Operations failed");
    } // if (!ok)
  } // randomTest()

  @Test
  public void removeDifferentPositions() {
    setup();
    // remove empty list
    assertEquals(null, ints.remove(8));

    // Remove from back empty
    for (int i = 0; i < 20; i++) {
      set(i);
    }
    for (int i = 19; i >= 0; i--) {
      assertEquals(ints.remove(i), numbers[i]);
    }
    // Remove from front until empty
    for (int i = 0; i < 20; i++) {
      set(i);
    }
    for (int i = 0; i < 20; i++) {
      assertEquals(ints.remove(i), numbers[i]);
    }

    // Remove from middle
    for (int i = 0; i < 20; i++) {
      set(i);
    }
    for (int i = 10; i >= 5; i--) {
      assertEquals(ints.remove(i), numbers[i]);
    }
  }

  // test for set
  // set when it is empty
  // set element in the front in a row
  // set in the end
  // set in the middle

  @Test
  public void testSetAtfront() {
    setup();

    // set when list is empty
    set(0);
    assertTrue(ints.containsKey(0));
    set("banana");
    assertTrue(strings.containsKey("banana"));

    // remove with one element
    ints.remove(0);

    // Test whether set throws Exception
    assertThrows(NullPointerException.class, () -> ints.get(null));

    // insert elements precede first element in list
    for (int i = 10; i < 20; i++) {
      set(i);
    }
    // these numbers should precede the above ones
    for (int i = 0; i < 10; i++) {
      set(i);
    }

    for (int i = 0; i < 20; i++) {
      assertEquals(ints.get(i), numbers[i]);
    }
  }

  // Testing set function to insert at the end of the list
  @Test
  public void testSetandOverwrite() {
    setup();
    // insert elements
    for (int i = 0; i < 100; i++) {
      set(i);
    }
    // insert and overwrite elements
    for (int i = 0; i < 200; i++) {
      set(i);
    }
    // check if all the values matched their keys to see if the list is in order
    for (int i = 0; i < 200; i++) {
      assertEquals(ints.get(i), value(i));
    }
  }

  // Test remove and then insert elements
  @Test
  public void testRemoveThenInsert() {
    setup();
    for (int i = 0; i < 200; i++) {
      set(i);
    }
    // remove then insert at different positions
    for (int i = 0; i < 200; i++) {
      if (i % 2 == 0) {
        ints.remove(Integer.valueOf(i));
      }
    }
    for (int i = 0; i < 200; i++) {
      if (i % 2 == 0)
        set(i);
    }
    for (int i = 0; i < 200; i++) {
      assertEquals(ints.get(i), value(i));
    }

  }


  @Test
  public void testRemoveEmpty() {
    setup();

    // remove when list is empty
    assertEquals(null, ints.remove(7));
    // remove with invalid key
    assertThrows(NullPointerException.class, () -> ints.remove(null));

    // remove with key not in the list
    set(9);
    assertEquals(null, ints.remove(7));
    remove(9);

    // test list is empty
    assertEquals(0, ints.size);
  }


  // test for get
  // get element when list is empty
  // get when list is unempty
  // get element not in the list
  // how do we assert exception ?

  @Test
  public void testGetExceptions() {
    setup();
    // Test get with null key
    assertThrows(NullPointerException.class, () -> ints.get(null));
    // Test get with key not in the list
    assertThrows(IndexOutOfBoundsException.class, () -> ints.get(7));
    set(5);
    // Test get with key not in the list
    assertThrows(IndexOutOfBoundsException.class, () -> ints.get(7));


  }

  @Test
  public void testGet() {
    setup();
    // get with one element
    ints.set(1, "hello");
    assertEquals("hello", ints.get(1));

    // get with more elements
    for (int i = 0; i < 20; i++) {
      set(i);
    }
    assertEquals(ints.get(5), numbers[5]);
    // last element
    assertEquals(ints.get(19), numbers[19]);


  }

  @Test
  public void testBigArray() {
    setup();
    int n = 1;
    while (n < 10) {
      // inserting elements
      for (int i = 0; i < Math.pow(2, n); i++) {
        set(i);
      }
      // checking everything in order
      for (int i = 0; i < Math.pow(2, n); i++) {
        assertEquals(ints.get(i), value(i));
      }
      // removing elements
      for (int i = 0; i < Math.pow(2, n); i++) {
        assertEquals(value(i), ints.remove(i));
      }
      n++;
    }
    // check list is empty
    assertEquals(ints.size, 0);
  }


  public static void main(String[] args) {
    SkipListTests slt = new SkipListTests();

    slt.setup();

    slt.simpleTest();


    SkipList<Integer, Integer> sl = new SkipList<Integer, Integer>();
    for (int size = 1; size < 2000; size *= 2) {
      sl = new SkipList<Integer,Integer>();
      for (int i = 0; i < size; i++) {
        sl.set(i,i);
      } // for
      sl.set(size/2,size/2);
      sl.get(size/2);
      System.out.println( "List " + i + " elements. setCounter = " + sl.setCounter);
      System.out.println("List " + i + " elements. GetCounter = " + sl.getCounter);
    }

    System.out.println("Finish printing getCounter");
    SkipList<Integer, Integer> slr = new SkipList<Integer, Integer>();
    for (int i = 1; i < 1000; i++) {
      slr.set(i, i);
      slr.remove(i);
      slr.set(i, i);
      System.out.println("List size " + i + " removeCounter = " + slr.removeCounter);
    }

    /*
     *
List 1 elements. setCounter = 0
List 1 elements. GetCounter = 12
List 2 elements. setCounter = 17
List 2 elements. GetCounter = 11
List 4 elements. setCounter = 17
List 4 elements. GetCounter = 9
List 8 elements. setCounter = 17
List 8 elements. GetCounter = 16
List 16 elements. setCounter = 17
List 16 elements. GetCounter = 16
List 32 elements. setCounter = 17
List 32 elements. GetCounter = 15
List 64 elements. setCounter = 17
List 64 elements. GetCounter = 14
List 128 elements. setCounter = 17
List 128 elements. GetCounter = 16
List 256 elements. setCounter = 17
List 256 elements. GetCounter = 15
List 512 elements. setCounter = 17
List 512 elements. GetCounter = 16
Finish printing getCounter
List size 1 removeCounter = 16
List size 2 removeCounter = 17
List size 4 removeCounter = 17
List size 8 removeCounter = 17
List size 16 removeCounter = 17
List size 32 removeCounter = 17
List size 64 removeCounter = 17
List size 128 removeCounter = 17
List size 256 removeCounter = 17
List size 512 removeCounter = 17



     */


  } // main
} // class SkipListTests
