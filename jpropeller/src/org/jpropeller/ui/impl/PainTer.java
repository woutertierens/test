package org.jpropeller.ui.impl;

import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.util.GeneralUtils;

/**
 * Hacks access to the paint method of either a com.sun.java.swing.Painter or javax.swing.Painter
 */
public class PainTer {

	private final static Logger logger = GeneralUtils.logger(PainTer.class);
	
	private final Object delegate;
	private final Method paintMethod;

	/**
	 * Create a {@link PainTer} that paints using a delegate
	 * Note that if the delegate is not a com.sun.java.swing.Painter or javax.swing.Painter
	 * this {@link PainTer} will do nothing.
	 * @param delegate	The delegate which which to try to paint
	 */
	public PainTer(Object delegate) {
		super();
		this.delegate = delegate;
	    paintMethod = paintMethod(delegate.getClass());
	    if (paintMethod == null) {
	    	logger.warning("Can't find paint method for PainTer");
	    }
	}

	/**
	 * Paint like the underlying painter. Note that component
	 * is omitted since we don't use it.
	 * @param g			{@link Graphics2D} to which to draw
	 * @param width		Width to paint
	 * @param height	Height to paint
	 */
	public void paint(Graphics2D g, int width, int height) {
		try {
			if (paintMethod != null) {
				paintMethod.invoke(delegate, g, null, width, height);
			}
		} catch (IllegalArgumentException e) {
	    	logger.log(Level.WARNING, "Illegal argument to paint method for PainTer", e);
		} catch (IllegalAccessException e) {
	    	logger.log(Level.WARNING, "Illegal access on paint method for PainTer", e);
		} catch (InvocationTargetException e) {
	    	logger.log(Level.WARNING, "Ivoncation target exception on paint method for PainTer", e);
		}
	}
	
	private static Method paintMethod(Class<?> c) {
		System.out.println("Class " + c);
	    Method[] allMethods = c.getMethods();
	    for (Method m : allMethods) {
	    	System.out.println("Method " + m);
	    	Class<?>[] parameterTypes = m.getParameterTypes();
	    	//Look for public void paint(Graphics2D g, _, int width, int height);
	    	//Where the "_" indicates we don't care about that parameter - we will pass
	    	//null.
			if (m.getName().equals("paint") && 
					m.getReturnType().equals(Void.TYPE) &&
					parameterTypes.length == 4 &&
					parameterTypes[0].equals(Graphics2D.class) && 
					parameterTypes[2].equals(java.lang.Integer.TYPE) && 
					parameterTypes[3].equals(java.lang.Integer.TYPE) &&
					m.getExceptionTypes().length == 0) {
			    return m;
			}
	    }
	    return null;
	}
}
