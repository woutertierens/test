package test.properties.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.StoringListener;
import org.jpropeller.util.StoringChangeListener;
import org.junit.Assert;

/**
 * Contains static Assert methods to test properties behaviour
 * @author bwebster
 */
public class PropertiesAssert {

	/**
	 * Assert that exactly one deep event has been received 
	 * by the listener, then remove that event from the listener
	 * @param l
	 * 		Listener to check
	 * @param origin
	 * 		Expected "rootOrigin" value of event
	 * @param prop
	 * 		Expected "prop" value of event
	 * @param rootProp
	 * 		Expected prop for root event in chain
	 * @param extraProps
	 * 		Any additional props in the chain - these are assumed
	 * to be in the chain in order between the prop and the rootProp,
	 * working backwards through the chain from prop to rootProp
	 */
	public static void assertDeepEvent(StoringChangeListener l, 
			PropEventOrigin origin, 
			GeneralProp<?> prop, 
			GeneralProp<?> rootProp,
			GeneralProp<?> ...extraProps) {
		//There should be one event exactly
		Assert.assertEquals(1, l.getEvents().size());

		//Remove the event
		PropEvent<?> event = l.getEvents().remove(0);
		
		assertDeepEvent(event, origin, prop, rootProp, extraProps);
	}

	/**
	 * Assert that an event matches expected properties
	 * @param event
	 * 		Event to check
	 * @param origin
	 * 		Expected "rootOrigin" value of event
	 * @param prop
	 * 		Expected "prop" value of event
	 * @param rootProp
	 * 		Expected prop for root event in chain
	 * @param extraProps
	 * 		Any additional props in the chain - these are assumed
	 * to be in the chain in order between the prop and the rootProp,
	 * working backwards through the chain from prop to rootProp
	 */
	public static void assertDeepEvent(PropEvent<?> event,
			PropEventOrigin origin, GeneralProp<?> prop,
			GeneralProp<?> rootProp, GeneralProp<?>... extraProps) {
		//Make list of extraProps
		List<GeneralProp<?>> props = new LinkedList<GeneralProp<?>>(Arrays.asList(extraProps));
		//Complete the list of props by adding prop and rootProp at either end
		props.add(rootProp);
		props.add(0, prop);
		
		//Make entire set of props in chain
		Set<GeneralProp<?>> propsInChain = new HashSet<GeneralProp<?>>(props);
		
		Assert.assertEquals(origin, event.getRootOrigin());
		Assert.assertEquals(true, event.isDeep());
		Assert.assertEquals(prop, event.getProp());
		Assert.assertEquals(rootProp, event.getRootEvent().getProp());
		Assert.assertEquals(propsInChain, event.getPropsInChain());
		
		//Recurse through the event chain, building list
		List<PropEvent<?>> events = new LinkedList<PropEvent<?>>();
		events.add(event);
		PropEvent<?> currentEvent = event;
		while (currentEvent.isDeep()) {
			currentEvent = currentEvent.getCauseEvent();
			events.add(currentEvent);
		}
		
		//Check the event chain is the same length as the props chain
		Assert.assertEquals(props.size(), events.size());
		
		//Check the event props match up with the expected props
		for (int i = 0; i < props.size(); i++) {
			Assert.assertEquals(props.get(i), events.get(i).getProp());
		}
	}
	
	/**
	 * Assert that exactly one shallow event has been received 
	 * by the listener, then remove that event from the listener
	 * @param l
	 * 		Listener to check
	 * @param origin
	 * 		Expected "rootOrigin" value of event
	 * @param prop
	 * 		Expected "prop" value of event
	 */
	public static void assertShallowEvent(StoringListener l, PropEventOrigin origin, GeneralProp<?> prop) {
		//There should be one event exactly
		Assert.assertEquals(1, l.getEvents().size());

		//Remove the event
		PropEvent<?> event = l.getEvents().remove(0);
		
		assertShallowEvent(event, origin, prop);
	}

	/**
	 * Assert that an event has specified properties
	 * @param event
	 * 		event to check
	 * @param origin
	 * 		Expected "rootOrigin" value of event
	 * @param prop
	 * 		Expected "prop" value of event
	 */
	public static void assertShallowEvent(PropEvent<?> event,
			PropEventOrigin origin, GeneralProp<?> prop) {
		Assert.assertEquals(origin, event.getRootOrigin());
		Assert.assertEquals(false, event.isDeep());
		Assert.assertEquals(prop, event.getProp());
	}
	
	/**
	 * Assert that no events have been received by the listener
	 * @param l
	 * 		Listener to check
	 */
	public static void assertNoEvent(StoringListener l) {
		//There should be one event exactly
		Assert.assertTrue(l.getEvents().isEmpty());
	}

	/**
	 * Assert that an exact count of events have been received by the listener
	 * @param l
	 * 		Listener to check
	 * @param count
	 * 		Number of events to expect
	 */
	public static void assertEventCount(StoringListener l, int count) {
		Assert.assertEquals(count, l.getEvents().size());
	}

}
