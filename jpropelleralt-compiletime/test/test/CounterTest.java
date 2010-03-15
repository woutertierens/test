package test;

import java.util.HashSet;
import java.util.Set;

import org.jpropelleralt.utils.impl.Counter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Counter}
 */
public class CounterTest {

	private Object a;
	private Object b;
	private Set<Object> justA;
	private Counter<Object> counter;
	private HashSet<Object> empty;
	private HashSet<Object> justB;
	private HashSet<Object> both;
	private VeryEqual va;
	private VeryEqual vb;
	private HashSet<Object> justVA;
	private HashSet<Object> justVB;
	private HashSet<Object> bothV;

	/**
	 * JUnit setup
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		a = new Object();
		b = new Object();

		empty = new HashSet<Object>();
		
		justA = new HashSet<Object>();
		justA.add(a);

		justB = new HashSet<Object>();
		justB.add(b);

		both = new HashSet<Object>();
		both.add(a);
		both.add(b);

		va = new VeryEqual();
		vb = new VeryEqual();

		justVA = new HashSet<Object>();
		justVA.add(va);

		justVB = new HashSet<Object>();
		justVB.add(vb);

		bothV = new HashSet<Object>();
		bothV.add(va);
		bothV.add(vb);

		counter = new Counter<Object>();
	}

	/**
	 * Junit test
	 */
	@Test
	public void test1() {
		counter.add(a);
		assertCounts(justA, counter);
		counter.remove(a);
		assertCounts(empty, counter);
	}

	/**
	 * Junit test
	 */
	@Test
	public void test2() {
		counter.add(a);
		counter.add(a);
		assertCounts(justA, counter);
		counter.remove(a);
		assertCounts(justA, counter);
		counter.remove(a);
		assertCounts(empty, counter);
	}

	/**
	 * Junit test
	 */
	@Test
	public void test3() {
		counter.add(a);
		assertCounts(justA, counter);
		counter.add(b);
		assertCounts(both, counter);
		counter.remove(a);
		assertCounts(justB, counter);
		counter.remove(b);
		assertCounts(empty, counter);
	}
	
	/**
	 * Junit test
	 */
	@Test
	public void testIdenticalityVersusEquality() {
		counter.add(va);
		assertCounts(justVA, counter);
		counter.add(vb);
		assertCounts(bothV, counter);
		counter.remove(va);
		assertCounts(justVB, counter);
		counter.remove(vb);
		assertCounts(empty, counter);
	}

	/**
	 * Junit test
	 */
	@Test
	public void test1v() {
		counter.add(va);
		assertCounts(justVA, counter);
		counter.remove(va);
		assertCounts(empty, counter);
	}

	/**
	 * Junit test
	 */
	@Test
	public void test2v() {
		counter.add(va);
		counter.add(va);
		assertCounts(justVA, counter);
		counter.remove(va);
		assertCounts(justVA, counter);
		counter.remove(va);
		assertCounts(empty, counter);
	}

	/**
	 * Junit test
	 */
	@Test
	public void test3v() {
		counter.add(va);
		assertCounts(justVA, counter);
		counter.add(vb);
		assertCounts(bothV, counter);
		counter.remove(va);
		assertCounts(justVB, counter);
		counter.remove(vb);
		assertCounts(empty, counter);
	}
	
	/**
	 * Junit test
	 */
	@Test
	public void testGC() {
		Object tempInstance = new Object();
		counter.add(tempInstance);
		Set<Object> tempInstanceSet = new HashSet<Object>();
		tempInstanceSet.add(tempInstance);
		assertCounts(tempInstanceSet, counter);

		//Show we DON'T allow collection yet
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		
		//Now "lose" the temp instance
		tempInstanceSet.clear();
		tempInstanceSet = null;
		tempInstance = null;
		
		//Try to get temp instance collected
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		assertCounts(empty, counter);
	}
	
	private void assertCounts(Set<Object> set, Counter<Object> counter) {
		Assert.assertEquals("Wrong counted instances", set, iterateToSet(counter));
	}
	
	private Set<Object> iterateToSet(Counter<Object> counter) {
		Set<Object> set = new HashSet<Object>();
		for (Object o : counter) {
			set.add(o);
		}
		return set;
	}

	/**
	 * {@link VeryEqual} instances are equal to anything.
	 * Hence this checks the behaviour of Count in terms
	 * of equality versus identicality {@link Object#equals(Object)}
	 * versus "==".
	 */
	private static class VeryEqual {
		@Override
		public boolean equals(Object obj) {
			return true;
		}
		@Override
		public int hashCode() {
			return 0;
		}
	}
	
}
