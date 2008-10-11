package test.properties;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.StoringListener;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.path.impl.PathPropException;
import org.junit.Assert;
import org.junit.Test;

import test.example.settings.Group;
import test.example.settings.User;
import test.properties.framework.PropertiesAssert;

/**
 * Unit test
 * @author bwebster
 */
public class GroupTest {

	/**
	 * Test the {@link PathProp} in {@link User} with null elements of path
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testUserPathPropWithNull() throws Exception {
		User a = new User();
		try {
			a.groupPermissions().get();
			
			//We should not get here, since there is a null in the path (user has no group)
			Assert.fail("Succeeded in looking up prop via invalid path (containing a null)");
			
		} catch (PathPropException ppe) {
			//We expect this exception
		}
	}
	
	/**
	 * Test the {@link PathProp} in {@link User}
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testUserPathProp() throws Exception {
		
		//Make a named group
		Group f = new Group();
		Assert.assertEquals("default group name", f.name().get());
		f.name().set("f group");
		Assert.assertEquals("f group", f.name().get());
		Assert.assertEquals("default group permissions", f.permissions().get());

		//Make a user in group f
		User a = new User();
		a.group().set(f);
		
		//Listen to user
		StoringListener l = new StoringListener();
		a.props().addListener(l);
		
		//User should mirror group permissions from group
		Assert.assertEquals("default group permissions", a.groupPermissions().get());

		//Change the group permissions, and user's permissions will still mirror
		f.permissions().set("f group new permissions");
		Assert.assertEquals("f group new permissions", f.permissions().get());
		Assert.assertEquals("f group new permissions", a.groupPermissions().get());

		//Check that the f group's permission change causes a consequent change to user a's groupPermissions(),
		//and also a deep change to user a's group()
		PropertiesAssert.assertEventCount(l, 2);
		//Get the two events, and identify them by origin
		PropEvent<?> e0 = l.getEvents().get(0);
		PropEvent<?> e1 = l.getEvents().get(1);
		PropEvent<?> consequentEvent;
		PropEvent<?> deepEvent;
		if (e0.getRootOrigin() == PropEventOrigin.CONSEQUENCE) {
			consequentEvent = e0;
			deepEvent = e1;
		} else {
			consequentEvent = e1;
			deepEvent = e0;			
		}
		PropertiesAssert.assertShallowEvent(consequentEvent, PropEventOrigin.CONSEQUENCE, a.groupPermissions());
		PropertiesAssert.assertDeepEvent(deepEvent, PropEventOrigin.USER, a.group(), f.permissions());
		
		//Make a new group with different permissions
		Group g = new Group();
		g.name().set("g group");
		g.permissions().set("g group initial permissions");

		//Set user's group to g, and it will mirror g's permissions
		a.group().set(g);
		Assert.assertEquals("g group initial permissions", a.groupPermissions().get());

		//Change group g and check user mirrors
		g.permissions().set("g group new permissions");
		Assert.assertEquals("g group new permissions", a.groupPermissions().get());

		//Change group f and check user does not change
		f.permissions().set("incorrect permissions");
		Assert.assertEquals("g group new permissions", a.groupPermissions().get());

	}	
}
