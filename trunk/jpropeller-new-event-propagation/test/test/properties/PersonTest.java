package test.properties;

import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.StoringListener;
import org.junit.Assert;
import org.junit.Test;

import test.example.contacts.Person;
import test.properties.framework.PropertiesAssert;

/**
 * Unit test
 * @author bwebster
 */
public class PersonTest {

	/**
	 * Unit test method
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testPerson() throws Exception {
		Person a = new Person();
		
		StoringListener l = new StoringListener();
		
		a.services().addListener(l);
		
		PropertiesAssert.assertNoEvent(l);
		a.name().set("Alice");
		PropertiesAssert.assertShallowEvent(l, PropEventOrigin.USER, a.name());
		Assert.assertEquals("Alice", a.name().get());
		Assert.assertEquals(1, a.address().get().houseNumber().get());
		Assert.assertEquals("default street value", a.address().get().street().get());
		
		PropertiesAssert.assertNoEvent(l);
		a.name().set("Alicia");
		PropertiesAssert.assertShallowEvent(l, PropEventOrigin.USER, a.name());
		Assert.assertEquals("Alicia", a.name().get());
		Assert.assertEquals(1, a.address().get().houseNumber().get());
		Assert.assertEquals("default street value", a.address().get().street().get());

		PropertiesAssert.assertNoEvent(l);
		a.address().get().houseNumber().set(42);
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, a.address(), a.address().get().houseNumber());
		Assert.assertEquals("Alicia", a.name().get());
		Assert.assertEquals(42, a.address().get().houseNumber().get());
		Assert.assertEquals("default street value", a.address().get().street().get());

	}	
}
