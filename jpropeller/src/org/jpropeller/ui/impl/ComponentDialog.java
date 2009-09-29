package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A {@link JDialog} that is constructed using a 
 * component to be displayed.
 * The dialog will display this component, but has
 * no other buttons and cannot be closed - be sure
 * to use {@link #setVisible(boolean)} to hide
 * it from another thread.
 */
public class ComponentDialog extends JDialog {

	private Component component;
	private Frame owner;
	
	/**
	 * Create a {@link ComponentDialog}
	 * 
	 * @param component 	The {@link Component} to display in the
	 * 						dialog
	 * @param owner 		The frame that owns this dialog
	 * @param title 		The title of the dialog
	 * @param modal 		Whether the dialog is modal
	 * @param closeable		Whether frame closes when requested
	 */
	private ComponentDialog(
			Component component, Frame owner, 
			String title, boolean modal, boolean closeable) {
		super(owner, title, modal);
		
		this.component = component;
		this.owner = owner;
		this.setDefaultCloseOperation(closeable? JDialog.HIDE_ON_CLOSE : JDialog.DO_NOTHING_ON_CLOSE);
		
		//Create UI
		getContentPane().add(buildUI());
		
		pack();
	}
	
	/**
	 * Make a modal {@link ComponentDialog} showing a message,
	 * and a busy indicator (indeterminate progress bar),
	 * with title "Busy..."
	 * @param owner		The owner of this dialog
	 * @param title		The title of this dialog
	 * @param message	The message to display
	 * @return			A new dialog
	 */
	public static ComponentDialog createBusyDialog(Frame owner, String title, String message) {
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		JLabel label = new JLabel(message);
		
		//Build the start/abort panel
		FormLayout layout = new FormLayout(
				"300px",
				"pref, 3dlu, pref"
				);
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		builder.append(bar);
		builder.nextRow();
		builder.append(label);
		JPanel panel = builder.getPanel();
		
		return new ComponentDialog(panel, owner, title, true, false);
	}
	
	/**
	 * Make a modal {@link ComponentDialog} showing a component
	 * and a button to close the dialog
	 * @param owner		The owner of this dialog
	 * @param title		The title of this dialog
	 * @param component	The component to display
	 * @param closeText	The text to display on close button
	 * @return			A new dialog
	 */
	public static ComponentDialog createCloseableDialog(Frame owner, String title, JComponent component, String closeText) {

		JButton closeButton = new JButton(closeText);
		
		//Build the panel
		FormLayout layout = new FormLayout(
				"fill:0dlu:grow, pref, fill:0dlu:grow",
				"fill:pref:grow, 7dlu, pref"
				);
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.add(component, cc.xyw(1, 1, 3));
		builder.add(closeButton, cc.xyw(1, 3, 3));
		JPanel panel = builder.getPanel();
		
		final ComponentDialog dialog = new ComponentDialog(panel, owner, title, true, true);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		
		dialog.setLocationRelativeTo(owner);
		
		return dialog;

	}
	
	/**
	 * @return Entire dialog UI
	 */
	public Component buildUI() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component);
		return panel;
	}
	
	/**
	 * Display the dialog.
	 * Note that if the dialog is modal, you will need to 
	 * call {@link #closeDialog()} in response to select or cancel
	 * events in order to continue.
	 */
	public void displayDialog() {
		//This displays window in sensible position, and eliminates the 
		//irritating "flick" where the window appears for a tiny interval 
		//(or just causes screen to be greyed in that area) then moves to
		//another position.
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	/**
	 * Close the dialog
	 */
	public void closeDialog() {
		setVisible(false);
	}
}