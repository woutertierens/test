package test.properties;

import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.StoringListener;
import org.junit.Assert;
import org.junit.Test;

import test.example.contacts.FriendList;
import test.example.contacts.Person;
import test.properties.framework.PropertiesAssert;

/**
 * Unit test
 * @author bwebster
 */
public class FriendListTest {

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

		FriendList fl = new FriendList();
		StoringListener l = new StoringListener();
		
		fl.props().addListener(l);
		
		//Note that the "modifications" property is how we see a list change, so this
		//chain represents:
		//property of Person -> property of ObservableList<Person> -> property of FriendList
		PropertiesAssert.assertNoEvent(l);
		fl.friends().add(a);
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, fl.friends(), fl.friends().get().modifications());
		
		//Now change the name of alice - this reaches the FriendList via:
		//a.name() -> fl.friends().get().modifications() -> fl.friends()
		//Note that the "modifications" property is how we see a list change, so this
		//chain represents:
		//property of Person -> property of ObservableList<Person> -> property of FriendList
		PropertiesAssert.assertNoEvent(l);
		a.name().set("Alice");
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, fl.friends(), a.name(), fl.friends().get().modifications());
		Assert.assertEquals("Alice", a.name().get());
		Assert.assertEquals(1, a.address().get().houseNumber().get());
		Assert.assertEquals("default street value", a.address().get().street().get());

		//Add a to the list again
		fl.friends().add(a);
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, fl.friends(), fl.friends().get().modifications());
		
		//Check we receive only a single event from a - it is in the list twice, but only listened to once
		a.name().set("Alicia");
		PropertiesAssert.assertDeepEvent(l, PropEventOrigin.USER, fl.friends(), a.name(), fl.friends().get().modifications());
		
		
	}	
}
