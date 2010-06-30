package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.Views;

/**
 * Displays a contents {@link JPanel} so that it can be
 * collapsed or expanded using a toggle button displayed
 * next to a title.
 */
public class CollapsiblePanel {

	private static final Color BRANCH_COLOR = new Color(185, 185, 225);

	private final JPanel contents;
	private final JPanel main;
	
	private static final Icon expand = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "list-add"); 
	private static final Icon hide = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "list-remove");
	
	/**
	 * Create a {@link CollapsiblePanel}
	 * @param title		The title to display at the top of the panel,
	 * 					next to the expand/collapse button
	 */
	public CollapsiblePanel(String title) {
		main = new JPanel(new BorderLayout());
		contents = new JPanel(new BorderLayout());
		
		final JToggleButton toggle = new JToggleButton();
		final JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		final JPanel titleBar = new JPanel(new BorderLayout());
		
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 32));
		
		titleBar.add(toggle, BorderLayout.EAST);
		titleBar.add(titleLabel, BorderLayout.WEST);
		titleBar.add(new JSeparator(), BorderLayout.NORTH);
		main.add(new JSeparator(), BorderLayout.SOUTH);
		
		main.add(titleBar, BorderLayout.NORTH);
		main.add(contents, BorderLayout.CENTER);
		
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contents.setVisible(toggle.isSelected());
				toggle.setIcon(contents.isVisible() ? hide : expand);
			}
		});
		
		toggle.setSelected(false);
		toggle.setIcon(expand);
		
		contents.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 8, 0, 0, BRANCH_COLOR),
				contents.getBorder()));

		
		contents.setVisible(false);
	}
	
	/**
	 * The contents {@link JPanel}, to which the contents to
	 * be displayed should be added
	 * @return		The contents panel
	 */
	public JPanel contents() {
		return contents;
	}
	
	/**
	 * The collapsible panel itself - to be added to your UI
	 * @return		The collapsible panel
	 */
	public JPanel main() {
		return main;
	}
	
	/**
	 * Demonstrate use of the collapsible panel
	 * @param args		ignored
	 */
	public static void main(String[] args) {
		
		GeneralUtils.enableNimbus();
		
		JFrame frame = new JFrame("Test");

		JPanel mainPanel = new JPanel();
		//mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		for (int i = 0; i < 10; i++) {
			CollapsiblePanel collapsible = new CollapsiblePanel("Test panel " + i);
			collapsible.contents().add(new JLabel("Test Panel " + i + " Contents"));
			mainPanel.add(collapsible.main());
		}		
		
		frame.getContentPane().add(mainPanel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	
	
}
