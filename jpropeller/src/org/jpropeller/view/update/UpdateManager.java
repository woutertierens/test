package org.jpropeller.view.update;

import org.jpropeller.view.View;


/**
 * An {@link UpdateManager} provides a central point of coordination for 
 * updating the display of views.
 * 
 * There is expected to be one central instance of this interface, which views
 * may use to coordinate their updates.
 * 
 * Where views do NOT use an {@link UpdateManager}, they generally just listen to the 
 * relevant bean and update immediately when they receive a (relevant) event.
 * This can lead to extra updates, which are at worst unnecessary or at best 
 * merely too frequent. This behaviour can still be implemented for simplicity, 
 * however the {@link UpdateManager} will provide for a interoperable optional new 
 * system - the changes required in a view are as follows:
 * 
 *	1) Views register themselves with the central {@link UpdateManager} instance 
 *  2) Views still listen to the relevant bean, but do not update when they 
 *  receive relevant events. Instead they notify the {@link UpdateManager} that they 
 *  need to be updated. They may also want to store any relevant information 
 *  they will need when they do update. 
 *  3) At some point in the future, the {@link UpdateManager} will tell the view to 
 *  update, at which point it will refresh with any changes since the last update.
 *  
 *  The implementation in the view can be as simple as just notifying the 
 *  manager when any event is received, and always repainting/updating contents 
 *  when the manager triggers an update.
 *  
 *  NOTE: A setting a new model into a {@link View} is NOT considered to be a change
 *  handled by the {@link UpdateManager} - views should just accept and immediately
 *  display the new model. The {@link UpdateManager} system handles deep changes to
 *  the model only, NOT setting of a new model. This is because in a well designed
 *  system, the vast majority of changes will be deep changes handled by the
 *  {@link UpdateManager}, so this is where intelligent management is needed. Changing
 *  the model instance displayed by a {@link View} should be infrequent - happening only
 *  for example when a new model is loaded from a file, etc. In addition, setting of a model
 *  should cause relatively little trouble - that is, the view should update once, and
 *  tell each child model to change model once, which is not comparable to the large
 *  number of changes that can be caused by deep changes to the model.
 *  
 *  {@link UpdateManager} implementations are expected to be optimised so as to only 
 *  update the views that have notified it of changes since their last update, 
 *  but Views should accept "extra" updates if they occur. Equally, a view 
 *  cannot expect to receive one update request per change - a key feature 
 *  of the {@link UpdateManager} system is that it can coalesce multiple changes 
 *  into one update, for example if they occur very close together in time. 
 *  This can greatly improve application efficiency and responsiveness. 
 */
public interface UpdateManager {

	/**
	 * Register an {@link UpdatableView}
	 * This view will be updated when appropriate. It must
	 * notify the {@link UpdateManager} when it requires an update
	 * by calling {@link #updateRequiredBy(UpdatableView)}. 
	 * The {@link UpdatableView} will be updated at some point after this notification,
	 * possibly with one update after several notifications.
	 * 
	 * Implementations must be thread safe, and accept calls to this
	 * method from any thread
	 * 
	 * @param view
	 * 		The view to register
	 */
	public void registerView(UpdatableView<?> view);

	/**
	 * Deregister an {@link UpdatableView}
	 * This view will now be ignored by the update manager
	 * @param view
	 * 		The view to deregister
	 */
	public void deregisterView(UpdatableView<?> view);

	/**
	 * Notify the {@link UpdateManager} that a view requires
	 * an update
	 * 
	 * Implementations must be thread safe, and accept calls to this
	 * method from any thread
	 * 
	 * @param view
	 * 		The view that requires an update
	 */
	public void updateRequiredBy(UpdatableView<?> view);
	
}
