package org.jpropeller.undo.delegates.impl;

import java.util.HashMap;
import java.util.Map;

import org.jpropeller.collection.CMap;
import org.jpropeller.undo.delegates.UndoDelegate;

/**
 * An {@link UndoDelegate} suitable for any {@link CMap} that
 * supports {@link CMap#replace(Map)} in order to restore it.
 * Stores a shallow (mapping only) copy of the map when asked to
 * {@link #save(CMap)}, and sets the {@link CMap} back
 * to those contents using {@link CMap#replace(Map)} when
 * asked to {@link #restore(CMap, Object)}.
 */
public class ObservableMapUndoDelegate implements UndoDelegate<CMap<?, ?>> {

	@Override
	public Object save(CMap<?, ?> changeable) {
		//FIXME a hashmap may not be suitable for all CMaps, since the
		//behaviour may be different - should work for all valid "normal" Map
		//implementations, but not for example for IdentityHashMap
		return new HashMap<Object, Object>(changeable);
	}

	//We rely on the undo system to provide us with the same data
	//back that we returned from our save method. If this is the case,
	//we know that we have the right type of data. 
	@SuppressWarnings({ "unchecked" })
	@Override
	public void restore(CMap<?, ?> changeable, Object data) {
		((CMap<Object, Object>)changeable).replace((Map<Object, Object>)data);
	}

}
