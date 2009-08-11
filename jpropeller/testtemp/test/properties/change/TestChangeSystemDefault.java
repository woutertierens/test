package test.properties.change;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.impl.ChangeSystemDefault;
import test.example.contacts.Address;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link ChangeSystemDefault} implementation
 */
public class TestChangeSystemDefault {

	/**
	 * Set up for tests
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tear down after tests
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that changing {@link Changeable} instances (e.g. calling setters)
	 * is prohibited during dispatch of changes
	 * @throws Exception
	 */
	@Test
	public void testNoSetsDuringDispatch() throws Exception {
		final Address address = new Address();
		
		System.out.println("Started");
		
		ChangeListener invalidListener = new ChangeListener() {
			@Override
			public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
				try {
					address.houseNumber().set(42);
					Assert.fail("Succeeded in setting in response to an event - this should not be allowed");
				} catch (Exception e) {
					//Exception is expected
					Assert.assertEquals(IllegalArgumentException.class, e.getClass());
				}
			}
		};
		
		address.features().addListener(invalidListener);
		
		address.houseNumber().set(3);
	}
	
}
