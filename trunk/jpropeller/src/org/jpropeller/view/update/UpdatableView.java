package org.jpropeller.view.update;

import org.jpropeller.view.View;

/**
 * A {@link View} that is also {@link Updatable}, and so 
 * must have its updates triggered externally
 * The {@link View} will not update unless this is called, but just store
 * any required updates until they can be carried out.
 * 
 * Generally implementors of this interface will automatically 
 * register themselves with the system {@link UpdateManager}
 */
public interface UpdatableView extends View, Updatable {
}
