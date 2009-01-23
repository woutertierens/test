package org.jpropeller.undo.delegates.impl;

import java.util.HashMap;
import java.util.Map;

import org.jpropeller.collection.ObservableMap;
import org.jpropeller.undo.delegates.UndoDelegate;

/**
 * An {@link UndoDelegate} suitable for any {@link ObservableMap} that
 * supports {@link ObservableMap#replace(Map)} in order to restore it.
 * Stores a shallow (mapping only) copy of the map when asked to
 * {@link #save(ObservableMap)}, and sets the {@link ObservableMap} back
 * to those contents using {@link ObservableMap#replace(Map)} when
 * asked to {@link #restore(ObservableMap, Object)}.
 */
public class ObservableMapUndoDelegate implements UndoDelegate<ObservableMap<?, ?>> {

	@Override
	public Object save(ObservableMap<?, ?> changeable) {
		return new HashMap<Object, Object>(changeable);
	}

	//We rely on the undo system to provide us with the same data
	//back that we returned from our save method. If this is the case,
	//we know that we have the right type of data. 
	@SuppressWarnings({ "unchecked" })
	@Override
	public void restore(ObservableMap<?, ?> changeable, Object data) {
		((ObservableMap<Object, Object>)changeable).replace((Map<Object, Object>)data);
	}

}
