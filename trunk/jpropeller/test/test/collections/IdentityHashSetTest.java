package test.collections;

import java.util.HashSet;
import java.util.List;

import org.junit.Assert;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link IdentityHashSet}
 */
public class IdentityHashSetTest {

	private CList<String> firstList;
	private CList<String> secondList;
	private HashSet<List<String>> normalSet;
	private IdentityHashSet<List<String>> identitySet;

	/**
	 * Prepare for tests
	 * @throws Exception	On any error
	 */
	@Before
	public void setUp() throws Exception {
		firstList = new CListDefault<String>();
		secondList = new CListDefault<String>();
		normalSet = new HashSet<List<String>>();		
		identitySet = new IdentityHashSet<List<String>>();
	}

	/**
	 * Clean up after tests
	 * @throws Exception	On any error
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * General test of set behaviour
	 * @throws Exception	On any error
	 */
	@Test
	public void generalTest() throws Exception {
		//The two empty lists should be equal but not the same (not identical / reference-equal)
		Assert.assertEquals(firstList, secondList);
		Assert.assertNotSame(firstList, secondList);
		
		//Adding them both to a normal HashSet should add only the first
		Assert.assertTrue("normalSet should add firstList", normalSet.add(firstList));
		Assert.assertEquals(1, normalSet.size());
		Assert.assertTrue("normalSet should contain firstList", normalSet.contains(firstList));

		//Second should fail to add
		Assert.assertFalse("normalSet should not add secondList", normalSet.add(secondList));
		Assert.assertEquals(1, normalSet.size());
		Assert.assertTrue("normalSet should contain firstList", normalSet.contains(firstList));

		//By contrast, they should both add to the identity set
		Assert.assertTrue("identitySet should add firstList", identitySet.add(firstList));
		Assert.assertEquals(1, identitySet.size());
		Assert.assertTrue("identitySet should contain firstList", identitySet.contains(firstList));

		//Second should also add
		Assert.assertTrue("identitySet should add secondList", identitySet.add(secondList));
		Assert.assertEquals(2, identitySet.size());
		Assert.assertTrue("identitySet should contain firstList", identitySet.contains(firstList));
		Assert.assertTrue("identitySet should contain secondList", identitySet.contains(secondList));
		
		//We can now add a String to the lists
		firstList.add("first");
		secondList.add("second");
		
		//Second list should now add to normal map - note that since hashes have changed,
		//the set is now somewhat broken
		Assert.assertTrue("normalSet should add secondList", normalSet.add(secondList));
		Assert.assertEquals(2, normalSet.size());
		
		//Check hashes all match up - list hashes of one element should be that element + 31, 
		//see List interface for reason
		Assert.assertEquals("first".hashCode() + 31, firstList.hashCode());
		Assert.assertEquals("second".hashCode() + 31, secondList.hashCode());
		
		//The identitySet hash however should be the sum of the identity hashes of the lists,
		//not their object hashes
		Assert.assertEquals(System.identityHashCode(firstList) + System.identityHashCode(secondList), identitySet.hashCode());
		
		//Make a new normal set to make sure that it sums the object hashes and so is 
		//analogous to identitySet but not the same
		HashSet<List<String>> newNormalSet = new HashSet<List<String>>();
		newNormalSet.add(firstList);
		newNormalSet.add(secondList);
		
		//Check we have both lists in map now, since they are non-equal from the start
		Assert.assertEquals(2, newNormalSet.size());
		Assert.assertTrue("newNormalSet should contain firstList", newNormalSet.contains(firstList));
		Assert.assertTrue("newNormalSet should contain secondList", newNormalSet.contains(secondList));

		//Check the new normal set hashes
		Assert.assertEquals(firstList.hashCode() + secondList.hashCode(), newNormalSet.hashCode());
		
		//Change the contents of first and second list - this breaks the new normal set
		//but leaves the identity set working since it only relies on unchanging object hashes
		//Actually not entirely sure it even uses them.
		firstList.add("first - second entry");
		secondList.add("second - second entry");
		
		//List hashes will almost certainly change - only risks a false failure if not
		Assert.assertTrue("firstList hash should have changed", "first".hashCode() + 31 != firstList.hashCode());
		Assert.assertTrue("secondList hash should have changed", "second".hashCode() + 31 != secondList.hashCode());

		//newNormalSet should be broken
		Assert.assertFalse("newNormalSet should not know it contains firstList", newNormalSet.contains(firstList));
		Assert.assertFalse("newNormalSet should not know it contains secondList", newNormalSet.contains(secondList));

		//identitySet should NOT be broken - hash should update, and should still know
		//it contains both lists
		Assert.assertEquals(System.identityHashCode(firstList) + System.identityHashCode(secondList), identitySet.hashCode());
		Assert.assertTrue("identitySet should know it contains firstList", identitySet.contains(firstList));
		Assert.assertTrue("identitySet should know it contains secondList", identitySet.contains(secondList));

		//Check removing works
		Assert.assertTrue("identitySet should remove firstList", identitySet.remove(firstList));
		Assert.assertFalse("identitySet should not contain firstList", identitySet.contains(firstList));

		//Make a new list that is equal to second list, but not same, and check we can't remove it
		//from identitySet
		CListDefault<String> fakeSecondList = new CListDefault<String>();
		fakeSecondList.addAll(secondList);
		Assert.assertEquals(secondList, fakeSecondList);
		Assert.assertNotSame(secondList, fakeSecondList);

		Assert.assertFalse("fakeSecondList should not be in identitySet", identitySet.contains(fakeSecondList));

		Assert.assertFalse("Should not be able to remove fakeSecondList from identitySet", identitySet.remove(fakeSecondList));

		//Check removing works
		Assert.assertTrue("identitySet should remove secondList", identitySet.remove(secondList));
		Assert.assertFalse("identitySet should not contain secondList", identitySet.contains(secondList));
		
	}
	
}
