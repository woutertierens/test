package org.jpropeller.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.jpropeller.ui.impl.PaneBottom;
import org.jpropeller.ui.impl.PaneTopSection;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Utility methods for {@link View}s
 */
public class ViewUtils {

	/**
	 * Height of tabs on group panes and cards
	 */
	public final static int TAB_HEIGHT = 26; 
	
	private ViewUtils(){}

	/**
	 * Get the recommended height for a row that displays
	 * an icon and two lines of text
	 * @return	Row height for icon and two lines of text
	 */
	public final static int iconAndTwoLinesRowHeight() {
		return 48;
	}
	
	/**
	 * Create a group pane, with a title component and content
	 * component
	 * @param title		String to display as a title
	 * @param content	{@link JComponent} to display as content (unaltered)
	 * @return			A new {@link JPanel}
	 */
	public final static JPanel groupPane(String title, JComponent content) {
		final Color selectedForeground = UIManager.getColor("itis.foreground.selected");

		JLabel label = new JLabel(title);
		label.setForeground(selectedForeground);
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
		top.setPreferredSize(new Dimension(10, TAB_HEIGHT));
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
	
	private static Color outerColor;

	/**
	 * Get the "outer" color, used in the outermost panel that
	 * groups together normal panels.
	 * @return	Outer color
	 */
	public final static Color outerColor() {
		if (outerColor == null) {
			outerColor = UIManager.getColor("itis.outer.color");
		}
		return outerColor;
	}
	
	/**
	 * Format a {@link JComponent} as an "outer" component, which does not contain
	 * View components
	 * @param component		The {@link JComponent}
	 */
	public final static void outerise(JComponent component) {
		component.setBackground(outerColor());
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

	/**
	 * Create a modal {@link JDialog} to display a {@link JView}
	 * @param view		The {@link JView} to display
	 * @param title		The title of the dialog
	 * @param icon		The window icon of the dialog
	 * @param size		The {@link Dimension} of the dialog 
	 * 					(used for initial, preferred and also minimum)
	 * @return			New {@link JDialog} displaying the component from 
	 * 					{@link JView#getComponent()}
	 */
	public static JDialog modalDialogFromView(JView view, String title, Image icon, Dimension size) {
		JComponent pluginPanel = view.getComponent();
		pluginPanel.setBorder(Borders.DIALOG_BORDER);
	
		final JDialog pluginDialog = new JDialog();
		pluginDialog.setTitle(title);
		pluginDialog.setIconImage(icon);
		pluginDialog.setModal(true);
		
		pluginDialog.setMinimumSize(size);
		pluginDialog.setPreferredSize(size);
		pluginDialog.setSize(size);
	
		pluginDialog.setLocationRelativeTo(null);
		pluginDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pluginDialog.getContentPane().add(pluginPanel);
		return pluginDialog;
	}

	/**
	 * Create a modal {@link JDialog} to display a {@link JView}
	 * This will also have a close button to hide the dialog.
	 * @param view		The {@link JView} to display
	 * @param title		The title of the dialog
	 * @param icon		The window icon of the dialog
	 * @param size		The {@link Dimension} of the dialog 
	 * 					(used for initial, preferred and also minimum)
	 * @return			New {@link JDialog} displaying the component from 
	 * 					{@link JView#getComponent()}
	 */
	public static JDialog closableModalDialogFromView(JView view, String title, Image icon, Dimension size) {
		JComponent viewComp = view.getComponent();
			
		final JDialog dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setIconImage(icon);
		dialog.setModal(true);
		
		dialog.setMinimumSize(size);
		dialog.setPreferredSize(size);
		dialog.setSize(size);
	
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		JButton closeButton = new JButton(new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		
		// Build the panel
		FormLayout layout = new FormLayout(
				"fill:10dlu:grow",
			//   view                  close
				"fill:10dlu:grow, 3dlu, pref");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(viewComp, 		cc.xy(1, 1));

		builder.add(closeButton, 	cc.xy(1, 3));

		JPanel panel = builder.getPanel();
		panel.setBorder(Borders.DIALOG_BORDER);

		dialog.getContentPane().add(panel);
		return dialog;
	}
	
	/**
	 * Combines the 3 JComponents into JSplitPanes, in a useful layout with a top left, a top right and a bottom component.
	 * draggingEnabled should typically be a global setting for enabling or disabling split panes.
	 * @param topLeft
	 * @param topRight
	 * @param bottom
	 * @param draggingEnabled
	 * @return the resulting JSplitPane
	 */
	public static final JSplitPane make3Panes(JComponent topLeft, JComponent topRight, JComponent bottom, boolean draggingEnabled)
	{
		JSplitPane topPanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				topLeft, topRight);
		topPanel.setOneTouchExpandable(draggingEnabled);
		topPanel.setResizeWeight(0.5);
		topPanel.setDividerLocation(0.5);
		topPanel.setEnabled(draggingEnabled);
		ViewUtils.outerise(topPanel);
		JSplitPane component=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topPanel, bottom);
		component.setOneTouchExpandable(draggingEnabled);
		component.setResizeWeight(0.7);
		component.setDividerLocation(0.5);
		component.setEnabled(draggingEnabled);
		ViewUtils.outerise(component);
		return component;
	}
	
	/**
	 * Combines the 3 JComponents into JSplitPanes, in a useful layout, with a top left, a top right and a bottom component.
	 * The location of the split panes is specified by horizLoc and vertLoc, which get passed to JSplitPane.setResizeWeight
	 * draggingEnabled should typically be a global setting for enabling or disabling split panes.
	 * @param topLeft
	 * @param topRight
	 * @param bottom
	 * @param draggingEnabled
	 * @return the resulting JSplitPane
	 */
	public static final JSplitPane make3Panes(JComponent topLeft, JComponent topRight, JComponent bottom, boolean draggingEnabled, double horizLoc, double vertLoc)
	{
		JSplitPane topPanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				topLeft, topRight);
		topPanel.setOneTouchExpandable(draggingEnabled);
		topPanel.setResizeWeight(horizLoc);
		topPanel.setDividerLocation(0.5);
		topPanel.setEnabled(draggingEnabled);
		ViewUtils.outerise(topPanel);
		JSplitPane component=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topPanel, bottom);
		component.setOneTouchExpandable(draggingEnabled);
		component.setResizeWeight(vertLoc);
		component.setDividerLocation(0.5);
		component.setEnabled(draggingEnabled);
		ViewUtils.outerise(component);
		return component;
	}
	
	/**
	 * Combines the 2 JComponents into JSplitPanes, in a useful layout, with a top and a bottom component.
	 * draggingEnabled should typically be a global setting for enabling or disabling split panes.
	 * The location of the split pane divider is specified by vertLoc, which get passed to JSplitPane.setResizeWeight
	 * @return the resulting JSplitPane
	 */
	public static final JSplitPane make2Panes(JComponent top, JComponent bottom, boolean draggingEnabled, double vertLoc)
	{
		JSplitPane panel=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				top, bottom);
		panel.setOneTouchExpandable(draggingEnabled);
		panel.setResizeWeight(vertLoc);
		panel.setDividerLocation(0.5);
		panel.setEnabled(draggingEnabled);
		ViewUtils.outerise(panel);
		return panel;
	}

}
