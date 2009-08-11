package org.jpropeller.undo.delegates.impl;

import java.util.LinkedList;

import org.jpropeller.collection.CList;
import org.jpropeller.undo.delegates.UndoDelegate;

/**
 * An {@link UndoDelegate} suitable for any {@link CList} that
 * supports modification of the list in order to restore it.
 * Stores a shallow (reference only) copy of the list when asked to
 * {@link #save(CList)}, and sets the {@link CList} back
 * to those contents using {@link CList#replace(Iterable)} when
 * asked to {@link #restore(CList, Object)}.
 */
public class ObservableListUndoDelegate implements UndoDelegate<CList<?>> {

	@Override
	public Object save(CList<?> changeable) {
		return new LinkedList<Object>(changeable);
	}

	//We rely on the undo system to provide us with the same data
	//back that we returned from our save method. If this is the case,
	//we know that we have the right type of data. 
	@SuppressWarnings({ "unchecked" })
	@Override
	public void restore(CList<?> changeable, Object data) {
		((CList<Object>)changeable).replace((Iterable<Object>)data);
	}

}
