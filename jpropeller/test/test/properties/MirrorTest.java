package test.properties;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.StoringListener;
import org.jpropeller.properties.path.impl.PathProp;
import org.junit.Assert;
import org.junit.Test;

import test.example.Mirror;
import test.properties.framework.PropertiesAssert;

/**
 * Unit test
 * @author bwebster
 */
public class MirrorTest {

	/**
	 * Test the {@link PathProp} in {@link Mirror} that mirrors 
	 * s to sMirror
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testMirrorValue() throws Exception {
		Mirror m = new Mirror();
		Assert.assertEquals("default s value", m.s().get());
		Assert.assertEquals("default s value", m.sMirror().get());
		
		//Change the original, check the mirror changes
		m.s().set("new s value");
		Assert.assertEquals("new s value", m.s().get());
		Assert.assertEquals("new s value", m.sMirror().get());
		
		//Change the mirror, check the original changes
		m.sMirror().set("even newer s value");
		Assert.assertEquals("even newer s value", m.s().get());
		Assert.assertEquals("even newer s value", m.sMirror().get());

	}
	
	/**
	 * Test events from {@link Mirror}
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testMirrorEvents() throws Exception {
		
		Mirror m = new Mirror();
		Assert.assertEquals("default s value", m.s().get());
		Assert.assertEquals("default s value", m.sMirror().get());
		
		//Listen to mirror
		StoringListener l = new StoringListener();
		m.props().addListener(l);
		
		//Change the original, we expect changes from it and the original
		m.s().set("new s value");

		//Check that the change causes a consequent change to sMirror
		//and also a shallow change to s
		PropertiesAssert.assertEventCount(l, 2);
		//Get the two events, and identify them by origin
		PropEvent<?> e0 = l.getEvents().get(0);
		PropEvent<?> e1 = l.getEvents().get(1);
		PropEvent<?> consequentEvent;
		PropEvent<?> shallowEvent;
		if (e0.getRootOrigin() == PropEventOrigin.CONSEQUENCE) {
			consequentEvent = e0;
			shallowEvent = e1;
		} else {
			consequentEvent = e1;
			shallowEvent = e0;			
		}
		PropertiesAssert.assertShallowEvent(consequentEvent, PropEventOrigin.CONSEQUENCE, m.sMirror());
		PropertiesAssert.assertShallowEvent(shallowEvent, PropEventOrigin.USER, m.s());
		l.getEvents().clear();

		//Change the mirror, we expect changes from it and the original
		m.sMirror().set("even newer s value");
		//Check that the change causes a consequent change to sMirror
		//and also a shallow change to s
		PropertiesAssert.assertEventCount(l, 2);
		//Get the two events, and identify them by origin
		e0 = l.getEvents().get(0);
		e1 = l.getEvents().get(1);
		if (e0.getRootOrigin() == PropEventOrigin.CONSEQUENCE) {
			consequentEvent = e0;
			shallowEvent = e1;
		} else {
			consequentEvent = e1;
			shallowEvent = e0;			
		}
		
		//These changes are actually exactly the same as when we change s() directly - when we change
		//sMirror() it changes s(), which is the "USER" change - we see the change to sMirror() as a
		//"CONSEQUENCE"
		PropertiesAssert.assertShallowEvent(consequentEvent, PropEventOrigin.CONSEQUENCE, m.sMirror());
		PropertiesAssert.assertShallowEvent(shallowEvent, PropEventOrigin.USER, m.s());
		l.getEvents().clear();
		
		
	}	
}
