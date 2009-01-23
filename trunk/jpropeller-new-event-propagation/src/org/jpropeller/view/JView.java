package org.jpropeller.view;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

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
	
	/**
	 * True if the view contains its own name - false if the name should be displayed
	 * alongside it (as appropriate)
	 * For example, this is true for {@link JCheckBox}-type controls that contain the
	 * name of the edited model as part of the view, and false for controls like
	 * {@link JTextField} that only display the actual value of the model, not its name. 
	 * @return
	 * 		Whether view is self-naming
	 */
	public boolean selfNaming();
}
