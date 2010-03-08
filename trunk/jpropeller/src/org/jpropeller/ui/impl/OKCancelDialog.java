package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * A modal {@link JDialog} that is constructed using a 
 * component to be displayed.
 * The dialog will display this component in the center, 
 * with buttons below it to confirm and cancel the
 * dialog. These will hide and dispose the dialog, returning control
 * to the code that displayed the dialog, which can then
 * check {@link #lastClickWasOK()} to see which button
 * was clicked.
 * 
 * Note that the dialog should be used once only, and
 * a new dialog created for every prompt.
 */
public class OKCancelDialog extends JDialog {

	private Component component;
	private Frame owner;
	private boolean lastClickWasOK = false;
	private final String okString;
	private final String cancelString;
	
	/**
	 * Create an {@link OKCancelDialog}
	 * 
	 * @param component 	The {@link Component} to display in the
	 * 						dialog
	 * @param owner 		The frame that owns this dialog
	 * @param title 		The title of the dialog
	 * @param ok			The text to display on ok button 
	 * @param cancel 		The text to display on cancel button
	 */
	public OKCancelDialog(
			Component component, Frame owner, 
			String title, 
			String ok, String cancel) {
		super(owner, title, true);
		
		this.component = component;
		this.owner = owner;
		this.okString = ok;
		this.cancelString = cancel;
		
		//Create UI
		getContentPane().add(buildUI());
		
		pack();
		
		setLocationRelativeTo(owner);
	}

	/**
	 * True if the most recently clicked button was ok,
	 * false if it was cancel
	 * @return		last click type
	 */
	public boolean lastClickWasOK() {
		return lastClickWasOK;
	}

	/**
	 * @return Entire dialog UI
	 */
	public Component buildUI() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component);
		panel.add(buildButtonPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	/**
	 * @return Panel with select/cancel buttons
	 */
	public Component buildButtonPanel() {
		JPanel panel = new JPanel();
		
		//Create button, when clicked this will set
		//the selected class as appropriate, and
		//hide the dialog if a valid selection has been made,
		JButton select = new JButton(okString);
		select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		panel.add(select);
		
		getRootPane().setDefaultButton(select);

		//Add cancel button, which simply records that user didn't click ok,
		//and hides the dialog
		if (cancelString != null) {
			JButton cancel = new JButton(cancelString);
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
			panel.add(cancel);
		}
		
		return panel;
	}

	/**
	 * Does the same as clicking cancel - dialog will be hidden and disposed, and
	 * {@link #lastClickWasOK()} will return false 
	 */
	public void cancel() {
		lastClickWasOK = false;
		closeDialog();
	}
	
	/**
	 * Does the same as clicking ok - dialog will be hidden and disposed, and
	 * {@link #lastClickWasOK()} will return true
	 */
	public void ok() {
		lastClickWasOK = true;
		closeDialog();
	}

	private void closeDialog() {
		setVisible(false);
		getContentPane().removeAll();
		dispose();
	}
	
	/**
	 * Display the dialog.
	 * Note that the dialog will remain open and modal until
	 * ok or cancel is clicked, at which point it will be hidden
	 * and disposed. You can then use {@link #lastClickWasOK()} to
	 * see which button was used to close the dialog.
	 */
	public void displayDialog() {
		//This displays window in sensible position, and eliminates the 
		//irritating "flick" where the window appears for a tiny interval 
		//(or just causes screen to be greyed in that area) then moves to
		//another position.
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}