package org.jpropeller.task.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.task.Task;
import org.jpropeller.util.Source;

/**
 * This {@link Task} will update a {@link Prop} containing a {@link CList} 
 * so that the {@link CList} contains the number of entries in
 * a {@link Prop} of {@link Integer}.
 * 
 * That is, any entries after that size will be removed,
 * and if there are too few entries, new values will be 
 * added from a {@link Source}.
 * 
 * The update is performed only when the required size changes,
 * not when the list changes.
 * 
 * @param <T>	The type of element in the list
 */
public class ListLengthSyncTask<T> implements Task {

	private final Prop<? extends CList<T>> list;
	private final Prop<Integer> size;
	private final Set<Prop<?>> sources;
	private final Source<T> valueSource;

	/**
	 * Create a {@link ListLengthSyncTask}
	 * @param list			The list to be updated
	 * @param size			The required size
	 * @param valueSource	Source of new values for the list
	 */
	public ListLengthSyncTask(
			Prop<? extends CList<T>> list,
			Prop<Integer> size,
			Source<T> valueSource) {
		
		super();
		this.list = list;
		this.size = size;
		this.valueSource = valueSource;
		
		IdentityHashSet<Prop<?>> sourcesM = new IdentityHashSet<Prop<?>>();
		sourcesM.add(size);
		sources = Collections.unmodifiableSet(sourcesM);
	}

	@Override
	public Set<? extends Changeable> getSources() {
		return sources;
	}

	@Override
	public void respond(AtomicBoolean shouldCancel) {
		Props.acquire();
		try {
			int currentSize = size.get();
			List<T> listTemp = new ArrayList<T>(list.get());
			if (listTemp.size() != currentSize) {
				//Trim
				if (listTemp.size() > currentSize) {
					listTemp = listTemp.subList(0, currentSize);
					
				//Grow
				} else if (listTemp.size() < currentSize) {
					int toAdd = currentSize - listTemp.size();
					for (int i = 0; i < toAdd; i++) {
						listTemp.add(valueSource.get());
					}
				}
				list.get().replace(listTemp);
			}
		} finally {
			Props.release();
		}
	}

}
