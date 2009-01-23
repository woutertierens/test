package org.jpropeller.undo.delegates.impl;

import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.immutable.impl.EditablePropImmutable;
import org.jpropeller.undo.delegates.UndoDelegate;

/**
 * {@link UndoDelegate} for any {@link GenericEditableProp}.
 * To {@link #save(GenericEditableProp)}, just stores the actual 
 * value of the {@link EditablePropImmutable}, and sets it back 
 * into the prop to {@link #restore(GenericEditableProp, Object)}.
 */
public class GenericEditablePropUndoDelegate implements UndoDelegate<GenericEditableProp<?>> {

	@Override
	public Object save(GenericEditableProp<?> changeable) {
		Object value = changeable.get();
		return value;
	}

	//We rely on the undo system to provide us with the same data
	//back that we returned from our save method. If this is the case,
	//we know that we have the right type of data. 
	@SuppressWarnings("unchecked")
	@Override
	public void restore(GenericEditableProp<?> changeable, Object data) {
		((GenericEditableProp<Object>)changeable).set(data);
	}

}
