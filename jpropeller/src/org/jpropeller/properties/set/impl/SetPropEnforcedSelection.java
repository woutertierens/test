package org.jpropeller.properties.set.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CSet;
import org.jpropeller.collection.impl.CSetDefault;
import org.jpropeller.concurrency.CancellableResponse;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.set.SetProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.task.Task;
import org.jpropeller.task.impl.BuildTask;
import org.jpropeller.task.impl.SynchronousTaskExecutor;

/**
 * An implementation of an {@link SetProp}
 */
public class SetPropEnforcedSelection extends SetPropDefault<Integer> {

	//We retain a reference so that the task will be around as long as this Prop
	@SuppressWarnings("unused")
	private final SynchronousTaskExecutor enforceSelectionTask;
	
	/**
	 * Create an {@link SetPropEnforcedSelection}
	 * @param name 
	 * 		Name for prop
	 * @param value
	 * 		The initial value of the {@link SetPropEnforcedSelection}
	 * @param processor 
	 * 		To valid input {@link CSet} values
	 * @param collection
	 * 		The collection we are selecting from
	 * @param response
	 * 		The response to an empty selection from a non-empty collection
	 */
	public SetPropEnforcedSelection(PropName<CSet<Integer>> name, CSet<Integer> value, ValueProcessor<CSet<Integer>> processor, final Prop<? extends CCollection<?>> collection, final EmptySelectionResponse response) {
		super(name, value, processor);
		enforceSelectionTask = makeEnforcedSelectionTask(this, collection, response);
	}

	/**
	 * Create a new {@link SetPropEnforcedSelection},
	 * accepting all new values
	 * @param name 
	 * 		The string value of the property name
	 * @param value
	 * 		The initial value of the {@link Prop}
	 * @param collection
	 * 		The collection we are selecting from
	 * @param response
	 * 		The response to an empty selection from a non-empty collection
	 * @return
	 * 		The new {@link SetPropEnforcedSelection}
	 */
	public static SetPropEnforcedSelection editable(String name, CSet<Integer> value, final Prop<? extends CCollection<?>> collection, final EmptySelectionResponse response) {
		return new SetPropEnforcedSelection(PropName.createSet(Integer.class, name), value, AcceptProcessor.<CSet<Integer>>get(), collection, response);
	}
	

	/**
	 * Create a new {@link SetPropEnforcedSelection} with an
	 * initial value of an empty new {@link CSetDefault},
	 * accepting all new values.
	 * @param name 
	 * 		The string value of the property name
	 * @param collection
	 * 		The collection we are selecting from
	 * @param response
	 * 		The response to an empty selection from a non-empty collection
	 * @return
	 * 		The new {@link SetPropEnforcedSelection}
	 */
	public static SetPropEnforcedSelection editable(String name, final Prop<? extends CCollection<?>> collection, final EmptySelectionResponse response) {
		return editable(name, new CSetDefault<Integer>(), collection, response);
	}

	/**
	 * Create a new {@link SetPropEnforcedSelection},
	 * with read-only behaviour
	 * @param name 
	 * 		The string value of the property name
	 * @param value
	 * 		The initial value of the {@link Prop}
	 * @param collection
	 * 		The collection we are selecting from
	 * @param response
	 * 		The response to an empty selection from a non-empty collection
	 * @return
	 * 		The new {@link SetPropEnforcedSelection}
	 */
	public static SetPropEnforcedSelection create(String name, CSet<Integer> value, final Prop<? extends CCollection<?>> collection, final EmptySelectionResponse response) {
		return new SetPropEnforcedSelection(PropName.createSet(Integer.class, name), value, ReadOnlyProcessor.<CSet<Integer>>get(), collection, response);
	}
	

	/**
	 * Create a new {@link SetPropEnforcedSelection} with an
	 * initial value of an empty new {@link CSetDefault},
	 * with read-only behaviour
	 * @param name 
	 * 		The string value of the property name
	 * @param collection
	 * 		The collection we are selecting from
	 * @param response
	 * 		The response to an empty selection from a non-empty collection
	 * @return
	 * 		The new {@link SetPropEnforcedSelection}
	 */
	public static SetPropEnforcedSelection create(String name, final Prop<? extends CCollection<?>> collection, final EmptySelectionResponse response) {
		return create(name, new CSetDefault<Integer>(), collection, response);
	}

	@Override
	public String toString() {
		return "Enforced Selection Set Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	/**
	 * Response to an empty selection in a non-empty collection 
	 */
	public enum EmptySelectionResponse {
		/**
		 * Select the first index (0)
		 */
		SELECT_FIRST,
		
		/**
		 * Select all indices (0 to collection size - 1)
		 */
		SELECT_ALL
	}
	
	/**
	 * Create a {@link SynchronousTaskExecutor} executing a task that
	 * will update the specified indices to always have a selection
	 * when a collection contains entries.
	 * @param indices			The indices to update
	 * @param collection		The collection indices are a selection within
	 * @param response			The response to empty selection
	 * @return	A new {@link SynchronousTaskExecutor} - make sure to retain a reference
	 * 			to allow the task to keep executing.
	 */
	public final static SynchronousTaskExecutor makeEnforcedSelectionTask(final Prop<? extends CCollection<Integer>> indices, final Prop<? extends CCollection<?>> collection, final EmptySelectionResponse response) {
		
		//Make task to do the resize 
		Task task = BuildTask.on(indices, collection).withResponse(new CancellableResponse() {
			@Override
			public void respond(AtomicBoolean shouldCancel) {
				int size = 0;
				CCollection<?> c = collection.get();
				if (c != null) size = c.size();
				
				CCollection<Integer> sel = indices.get();
				
				if (sel.isEmpty() && size != 0) {
					if (response == EmptySelectionResponse.SELECT_FIRST) {
						indices.get().replace(Arrays.asList(0));
					} else if (response == EmptySelectionResponse.SELECT_ALL) {
						List<Integer> all = new LinkedList<Integer>();
						for (int i = 0; i < size; i++) {
							all.add(i);
						}
						indices.get().replace(all);
					}
				}
			}
		});
		return new SynchronousTaskExecutor(task);
	}
}
