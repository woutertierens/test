package org.jpropeller.undo.delegates.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.undo.delegates.UndoDelegate;
import org.jpropeller.util.GeneralUtils;

/**
 * {@link UndoDelegate} for any {@link Prop}.
 * To {@link #save(Prop)}, just stores the actual 
 * value of the {@link Prop}, and sets it back 
 * into the prop to {@link #restore(Prop, Object)}.
 */
public class PropUndoDelegate implements UndoDelegate<Prop<?>> {

	private final static Logger logger = GeneralUtils.logger(PropUndoDelegate.class);
	
	@Override
	public Object save(Prop<?> prop) {
		
		//If the prop is read only, don't get its value
		if (prop.getEditability() == PropEditability.READ_ONLY) {
			return null;
		}
		
		return prop.get();
	}

	//We rely on the undo system to provide us with the same data
	//back that we returned from our save method. If this is the case,
	//we know that we have the right type of data. 
	@SuppressWarnings("unchecked")
	@Override
	public void restore(Prop<?> changeable, Object data) {
		try {
			((Prop<Object>)changeable).set(data);
		} catch (ReadOnlyException e) {
			logger.log(Level.SEVERE, 
					"Read only exception restoring undo state - " +
					"should not be possible since editability " +
					"is checked when saving state.", e);
		} catch (InvalidValueException e) {
			logger.log(Level.SEVERE, 
					"Invalid value exception restoring undo state - " +
					"should not be possible since state was " +
					"valid when stored.", e);			
		}
	}

}
