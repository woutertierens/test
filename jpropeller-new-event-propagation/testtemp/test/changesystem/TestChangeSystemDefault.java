package test.changesystem;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.impl.ChangeSystemDefault;
import test.example.contacts.Address;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link ChangeSystemDefault} implementation
 * @author shingoki
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
				System.out.println("About to set houseNumber");
				try {
					address.houseNumber().set(42);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		address.features().addListener(invalidListener);
		
		address.houseNumber().set(3);
	}

	/**
	 * Simple test case
	 * @param args
	 */
	public static void main(String[] args) {
		final Address address = new Address();
		
		System.out.println("Started");
		
		
		ChangeListener invalidListener = new ChangeListener() {
			@Override
			public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
				System.out.println("About to set houseNumber");
				try {
					System.out.println(address);
					System.out.println(address.houseNumber().get());
					address.features().removeListener(this);
					System.out.println("Removed self as listener");
					//address.houseNumber().set(42);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		address.features().addListener(invalidListener);
		
		address.houseNumber().set(3);
		address.houseNumber().set(4);

	}
	
}
