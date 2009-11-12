package org.jpropeller.util;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jpropeller.ui.impl.PaneBottom;
import org.jpropeller.ui.impl.PaneTopSection;
import org.jpropeller.view.View;

import com.jgoodies.forms.factories.Borders;

/**
 * Utility methods for {@link View}s
 */
public class ViewUtils {

	private ViewUtils(){}

	/**
	 * Create a group pane, with a title component and content
	 * component
	 * @param title		String to display as a title
	 * @param content	{@link JComponent} to display as content (unaltered)
	 * @return			A new {@link JPanel}
	 */
	public final static JPanel groupPane(String title, JComponent content) {
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return groupPane(label, content);
	}
	
	/**
	 * Create a group pane, with a title component and content
	 * component
	 * @param title		{@link JComponent} to display as a title, will have
	 * 					its border replaced
	 * @param content	{@link JComponent} to display as content (unaltered)
	 * @return			A new {@link JPanel}
	 */
	public final static JPanel groupPane(JComponent title, JComponent content) {

		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new PaneTopSection(6, true, true, false));
		top.add(title);
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(new PaneBottom(6));
		center.add(content);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(top, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		
		return panel;
	}
	
	/**
	 * Return a panel, containing specified component, with
	 * a {@link Borders#DIALOG_BORDER}
	 * @param component		The component to contain
	 * @return				Panel containing component, with border
	 */
	public final static JPanel dialogPanel(JComponent component) {
		JPanel panel = backgroundPanel(component);
		panel.setBorder(Borders.DIALOG_BORDER);
		return panel;
	}
	
	/**
	 * Return a panel, containing specified component, with
	 * a {@link Borders#DLU4_BORDER}
	 * @param component		The component to contain
	 * @return				Panel containing component, with border
	 */
	public final static JPanel smallBorderPanel(JComponent component) {
		JPanel panel = backgroundPanel(component);
		panel.setBorder(Borders.DLU4_BORDER);
		return panel;
	}
	
	/**
	 * Return a panel, containing specified component.
	 * @param component		The component to contain
	 * @return				Panel containing component
	 */
	public final static JPanel backgroundPanel(JComponent component) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component);
		return panel;
	}
	
}
