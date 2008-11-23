package org.jpropeller.view.update;

import org.jpropeller.view.View;

/**
 * A {@link View} that must have its updates triggered externally
 * The view will not update unless this is called, but just store
 * any required updates until they can be carried out.
 * 
 * Generally implementors of this interface will automatically 
 * register themselves with the system {@link UpdateManager}
 *
 * @param <M>
 * 		The type of model
 */
public interface UpdatableView<M> extends View<M>, Updatable {
	
}
