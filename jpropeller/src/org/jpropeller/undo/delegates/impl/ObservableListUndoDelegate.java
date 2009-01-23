package org.jpropeller.undo.delegates.impl;

import java.util.LinkedList;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.undo.delegates.UndoDelegate;

/**
 * An {@link UndoDelegate} suitable for any {@link ObservableList} that
 * supports modification of the list in order to restore it.
 * Stores a shallow (reference only) copy of the list when asked to
 * {@link #save(ObservableList)}, and sets the {@link ObservableList} back
 * to those contents using {@link ObservableList#replace(Iterable)} when
 * asked to {@link #restore(ObservableList, Object)}.
 */
public class ObservableListUndoDelegate implements UndoDelegate<ObservableList<?>> {

	@Override
	public Object save(ObservableList<?> changeable) {
		return new LinkedList<Object>(changeable);
	}

	//We rely on the undo system to provide us with the same data
	//back that we returned from our save method. If this is the case,
	//we know that we have the right type of data. 
	@SuppressWarnings({ "unchecked" })
	@Override
	public void restore(ObservableList<?> changeable, Object data) {
		((ObservableList<Object>)changeable).replace((Iterable<Object>)data);
	}

}
