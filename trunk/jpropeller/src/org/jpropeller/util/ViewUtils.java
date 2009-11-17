package org.jpropeller.util;

import java.awt.BorderLayout;
import java.awt.Color;
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
		top.setBorder(new PaneTopSection(6, true, true, true));
		top.add(title);
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(new PaneBottom(6));
		center.add(content);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(top, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		
		top.setOpaque(false);
		center.setOpaque(false);
		panel.setOpaque(false);
		
		return panel;
	}

	/**
	 * Create a group pane, with no title, only a content component
	 * @param content	{@link JComponent} to display as content (unaltered)
	 * @return			A new {@link JPanel}
	 */
	public final static JPanel groupPane(JComponent content) {

		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(new PaneBottom(6, true));
		center.add(content);
		
		return center;
	}
	
	//FIXME move this to a proper UI color
	private final static Color BG = new Color(190, 200, 220);
	
	/**
	 * Format a {@link JComponent} as an "outer" component, which does not contain
	 * View components
	 * @param component		The {@link JComponent}
	 */
	public final static void outerise(JComponent component) {
		component.setBackground(BG);
		//component.setOpaque(false);
	}

	/**
	 * Format a list of {@link JComponent}s as "outer" components, which do not contain
	 * View components
	 * @param components		The {@link JComponent}s
	 */
	public final static void outerise(JComponent... components) {
		for (JComponent c : components) {
			outerise(c);
		}
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
		outerise(panel);
		return panel;
	}
	
	
	/**
	 * Return a panel, containing specified component, with
	 * a {@link Borders#DIALOG_BORDER}
	 * This is for inner padding, so will not be {@link #outerise(JComponent)}d
	 * @param component		The component to contain
	 * @return				Panel containing component, with border
	 */
	public final static JPanel dialogPanelInner(JComponent component) {
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
		outerise(panel);
		return panel;
	}

	/**
	 * Return a panel, containing specified component, with
	 * a {@link Borders#DLU4_BORDER}
	 * This is for inner padding, so will not be {@link #outerise(JComponent)}d
	 * @param component		The component to contain
	 * @return				Panel containing component, with border
	 */
	public final static JPanel smallBorderPanelInner(JComponent component) {
		JPanel panel = backgroundPanel(component);
		panel.setBorder(Borders.DLU4_BORDER);
		return panel;
	}

	/**
	 * Return a panel, containing specified component.
	 * @param component		The component to contain
	 * @return				Panel containing component
	 */
	private final static JPanel backgroundPanel(JComponent component) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component);
		return panel;
	}
	
}
