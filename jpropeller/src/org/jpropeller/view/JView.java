package org.jpropeller.view;

import javax.swing.JComponent;

import org.jpropeller.view.update.UpdatableView;

/**
 * An {@link UpdatableView} which has a UI component ( {@link JComponent} )
 * to be displayed in a GUI
 * @author bwebster
 *
 * @param <M>
 * 		The type of model in the {@link View}
 */
public interface JView<M> extends UpdatableView<M> {

	/**
	 * Get the {@link JComponent} that displays the view
	 * @return
	 * 		The view {@link JComponent}
	 */
	public JComponent getComponent();
	
}
