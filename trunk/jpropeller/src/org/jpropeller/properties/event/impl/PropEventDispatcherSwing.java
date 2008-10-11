package org.jpropeller.properties.event.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.jpropeller.properties.event.PropEventDispatch;
import org.jpropeller.properties.event.PropEventDispatcher;

/**
 * Implementation of {@link PropEventDispatcher} for compatibility
 * with Swing, this invokes a {@link Runnable} in the Swing thread (EDT)
 * to fire all events, then waits for it to complete.
 * @author shingoki
 */
public class PropEventDispatcherSwing implements PropEventDispatcher {

	private final static String NONRUNTIME_EXCEPTION_MESSAGE = "Encountered non-runtime exception during event dispatch on Swing thread (EDT)";
	private final static Logger logger = Logger.getLogger(PropEventDispatcherSwing.class.toString());
	
	@Override
	public void dispatch(final List<PropEventDispatch> dispatches) {
		
		Runnable performDispatch = new Runnable() {
			@Override
			public void run() {
				for (PropEventDispatch dispatch : dispatches) {
					dispatch.getListener().propChanged(dispatch.getEvent());
				}
			}
		};

		//If we are in the swing thread, dispatch directly
		if (SwingUtilities.isEventDispatchThread()) {
			performDispatch.run();
			
		//Otherwise, we need to dispatch in the swing thread
		} else {
			try {
				SwingUtilities.invokeAndWait(performDispatch);
			} catch (InterruptedException e) {
				//This should not occur
				logger.severe("Interrupted while performing event dispatch to Swing Thread (EDT)");
			} catch (InvocationTargetException e) {
				//This exception will be thrown if the performDispatch Runnable
				//throws an exception. This will only occur for runtime exceptions,
				//so we re-throw the exception as a runtime exception.
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException)e.getCause();
					
				//If for whatever reason the exception is not runtime, log this 
				//and re-throw as a runtime exception
				} else {
					logger.severe(NONRUNTIME_EXCEPTION_MESSAGE);
					throw new RuntimeException(NONRUNTIME_EXCEPTION_MESSAGE, e);
				}
			}
			
		}
		
	}

}
