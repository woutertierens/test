package org.jpropeller.task.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jpropeller.collection.CCollection;
import org.jpropeller.concurrency.CancellableResponse;
import org.jpropeller.properties.Prop;
import org.jpropeller.system.Props;
import org.jpropeller.task.Task;
import org.jpropeller.util.StringUtilities;
import org.jpropeller.view.info.Named;

/**
 * Enforces unique naming of {@link Named} elements of
 * a {@link CCollection}, by appending bracketed, incrementing
 * integers starting from 2 to the end of the names. 
 * 
 * See {@link StringUtilities#incrementName(String)} for details
 * of the exact name string alteration performed.
 * 
 * Should have reasonable performance on small lists - please test
 * performance if using this on larger lists, especially if the
 * {@link Task} is used synchronously.
 */
public class UniqueNamesTask {

	private UniqueNamesTask(){};

	/**
	 * Get a task
	 * @param collection		The collection on which to enforce unique naming
	 * @return					A {@link Task} that will rename elements of the
	 * 							collection to ensure they are uniquely named.
	 */
	public final static Task get(final Prop<? extends CCollection<? extends Named>> collection) {
		return BuildTask.on(collection).withResponse(new CancellableResponse() {
			@Override
			public void respond(AtomicBoolean shouldCancel) {
				Props.acquire();
				try {
					Set<String> names = new HashSet<String>();
					for (Named s : collection.get()) {
						String name = s.name().get();
						boolean changed = false;
						while (names.contains(name)) {
							name = StringUtilities.incrementName(name);
							changed = true;
						}
						names.add(name);
						if (changed) {
							s.name().set(name);
						}
					}
				} finally {
					Props.release();
				}
			}
		});		
	}
	
}
