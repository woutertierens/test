package org.jpropeller.view.impl;

import java.util.LinkedList;
import java.util.List;

import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.Updatable;

/**
 * Helper class for implementing {@link JView}s that contain
 * other {@link View}s
 * 
 * Provides implementations of {@link JView} methods to which
 * implementations can delegate
 */
public class CompositeViewHelper {

	List<View<?>> views;

	/**
	 * Create a new {@link CompositeViewHelper}
	 * @param views
	 * 		The views in the composite
	 */
	public CompositeViewHelper(List<View<?>> views) {
		this.views = views;
	}
	
	/**
	 * Create a new {@link CompositeViewHelper}
	 * @param viewsToUse
	 * 		The views in the composite
	 */
	public CompositeViewHelper(View<?>... viewsToUse) {
		views = new LinkedList<View<?>>();
		for (View<?> view : viewsToUse) {
			views.add(view);
		}
	}
	
	/**
	 *	Implementation of a {@link JView} method - delegate to this
	 */
	public void cancel() {
		for (View<?> view : views) {
			view.cancel();
		}
	}

	/**
	 *	Implementation of a {@link JView} method - delegate to this
	 * @throws CompletionException
	 * 		If a child view cannot be committed 
	 */
	public void commit() throws CompletionException {
		for (View<?> view : views) {
			view.commit();
		}
	}

	/**
	 *	Implementation of a {@link JView} method - delegate to this
	 *	@return whether editing 
	 */
	public boolean isEditing() {
		boolean editing = false;
		for (View<?> view : views) {
			if (view.isEditing()) editing = true;
		}
		return editing;
	}

	/**
	 *	Implementation of a {@link JView} method - delegate to this
	 */
	public void dispose() {
		for (View<?> view : views) {
			if (view instanceof Updatable) {
				((Updatable)view).dispose();
			}
		}
	}

	/**
	 *	Implementation of a {@link JView} method - delegate to this
	 */
	public void update() {
		//Subeditors will update themselves
	}
}
