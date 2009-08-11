package org.jpropeller.view;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.jpropeller.view.update.UpdatableView;

/**
 * An {@link UpdatableView} which has a UI component ( {@link JComponent} )
 * to be displayed in a GUI
 */
public interface JView extends UpdatableView {

	/**
	 * This describes the format of the JView, for
	 * example it may be a small single line component,
	 * a medium sized component that spans several "lines",
	 * or a large component that users may wish to hide
	 */
	public enum Format {
		/**
		 * Component is essentially a single line - for example
		 * a text field, a checkbox, etc.
		 */
		SINGLE_LINE,
		
		/**
		 * Component is not a single line, but is still suitable
		 * for direct display grouped with other editors, for
		 * example in a list, etc.
		 */
		MEDIUM,
		
		/**
		 * Component is large - it is probably essentially an
		 * entire panel, and so should be displayed separately,
		 * or with provision for hiding the component if it
		 * is grouped with other editors, for example in a list
		 */
		LARGE
	}
	
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
	 *  
	 * Should not change after construction of the {@link JView}.
	 * 
	 * @return		Whether view is self-naming
	 */
	public boolean selfNaming();
	
	/**
	 * This describes the format of the JView, for
	 * example it may be a small single line component,
	 * a medium sized component that spans several "lines",
	 * or a large component that users may wish to hide
	 *  
	 * Should not change after construction of the {@link JView}.
	 * 
	 * @return		The {@link Format} of the {@link JView}
	 */
	public Format format();
}
