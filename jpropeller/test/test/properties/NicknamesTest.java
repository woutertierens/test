package test.properties;

import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.StoringListener;
import org.junit.Assert;
import org.junit.Test;

import test.example.contacts.Nicknames;
import test.example.contacts.Person;
import test.properties.framework.PropertiesAssert;

/**
 * Unit test
 */
public class NicknamesTest {

	/**
	 * Unit test method
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testFriendList() throws Exception {
		Person a = new Person();
		Assert.assertEquals(1, a.address().get().houseNumber().get());
		Assert.assertEquals("default street value", a.address().get().street().get());

		Nicknames n = new Nicknames();
		StoringListener l = new StoringListener();
		
		n.services().addListener(l);
		
		//Note that the "modifications" property is how we see a map change, so this
		//chain represents:
		//property of Person -> property of ObservableMap<Person> -> property of Nicknames
		PropertiesAssert.assertNoEvent(l);
		n.nicknames().put("Al", a);
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, n.nicknames(), n.nicknames().get().modifications());
		
		//Now change the name of a - this reaches the FriendList via:
		//a.name() -> n.nicknames().get().modifications() -> n.nicknames()
		//Note that the "modifications" property is how we see a list change, so this
		//chain represents:
		//property of Person -> property of ObservableList<Person> -> property of FriendList
		PropertiesAssert.assertNoEvent(l);
		a.name().set("Alice");
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, n.nicknames(), a.name(), n.nicknames().get().modifications());
		Assert.assertEquals("Alice", a.name().get());
		Assert.assertEquals(1, a.address().get().houseNumber().get());
		Assert.assertEquals("default street value", a.address().get().street().get());

		//Add a to the map again
		n.nicknames().put("Allie", a);
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, n.nicknames(), n.nicknames().get().modifications());
		
		//Check we receive only a single event from a - it is in the map twice, but only listened to once
		a.name().set("Alicia");
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, n.nicknames(), a.name(), n.nicknames().get().modifications());		
		
	}	
}
