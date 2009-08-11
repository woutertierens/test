package org.jpropeller.util;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.view.View;

import com.jgoodies.forms.factories.Borders;

/**
 * Utility methods for {@link View}s
 */
public class ViewUtils {

	private ViewUtils(){}
	
	/**
	 * Return a panel, containing specified component, with
	 * a {@link Borders#DIALOG_BORDER}
	 * @param component		The component to contain
	 * @return				Panel containing component, with border
	 */
	public final static JPanel dialogPanel(JComponent component) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(Borders.DIALOG_BORDER);
		panel.add(component);
		return panel;
	}
	
}
