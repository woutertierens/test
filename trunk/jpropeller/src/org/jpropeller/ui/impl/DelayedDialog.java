package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jpropeller.util.GeneralUtils;

import com.jgoodies.forms.factories.Borders;

/**
 * A modal {@link JDialog} that is constructed using a 
 * component to be displayed, and strings for two buttons.
 * The default button will always be enabled, the other button
 * will only be enabled after a delay. The dialog can only be closed
 * by pressing a button or closing the window, which will trigger the immediate option, 
 * and the choice of button is then available.
 * Can be used to present a "dangerous" or irreversible action
 * in a way that avoids the user clicking the delayed button unless
 * they are sure.
 * 
 * The dialog will display the provided component in the center,
 * and convenience methods provide for using a simple label. 
 * 
 * Note that the dialog should be used once only, and
 * a new dialog created for every prompt.
 */
public class DelayedDialog extends JDialog {

	private Component component;
	private Frame owner;
	private boolean lastClickWasDelayedOption = false;
	private final String delayedString;
	private final String immediateString;
	//private Timer enabler;
	private SwingWorker<String, String> enabler;
	
	/**
	 * Create an {@link DelayedDialog}
	 * 
	 * @param component 		The {@link Component} to display in the
	 * 							dialog
	 * @param owner 			The frame that owns this dialog
	 * @param title 			The title of the dialog
	 * @param delayedString		The text to display on the delayed, non-default button 
	 * @param immediateString 	The text to display on the immediately displayed, default button
	 * @param delayMillis Number of milliseconds delay before delayed button is enabled.
	 */
	public DelayedDialog(
			Component component, Frame owner, 
			String title, 
			String delayedString, String immediateString,
			int delayMillis) {
		super(owner, title, true);
		
		this.component = component;
		this.owner = owner;
		this.delayedString = delayedString;
		this.immediateString = immediateString;

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				immediate();
			}
		});
		
		
		//Create UI
		getContentPane().add(buildUI(delayMillis));
		
		pack();
		
		setLocationRelativeTo(owner);
	}

	/**
	 * True if the most recently clicked button was the delayed, non-default
	 * option, false if it was the immediately displayed, default option
	 * @return		last click type
	 */
	public boolean lastClickWasDelayedOption() {
		return lastClickWasDelayedOption;
	}

	/**
	 * @param delayMillis Number of milliseconds delay before delayed button is enabled.
	 * @return Panel with select/cancel buttons
	 */
	public Component buildUI(final int delayMillis) {
		JPanel buttonPanel = new JPanel();
		
		//Create button, when clicked this will set
		//the selected class as appropriate, and
		//hide the dialog if a valid selection has been made,
		final JButton delayed = new JButton(delayedString);
		delayed.setEnabled(false);
		delayed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delayed();
			}
		});
		buttonPanel.add(delayed);
		
		//This somewhat complicated way of doing things is modeled on
		//DialogAndWorkerUtil. A Timer would be simpler, but the timer
		//action is not performed while a modal dialog is displayed.
		
		//Swingworker just carries out a delay in a background thread,
		//the only important thing is that it will switch to state "DONE"
		//after the correct time
		enabler = new SwingWorker<String, String>() {
			@Override
			protected String doInBackground() throws Exception {
				GeneralUtils.sleepUpTo(delayMillis);
				return null;
			}
		};
		
		//This listener will respond to the worker being done by enabling the
		//delayed button.
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if ("state".equals(event.getPropertyName())
						&& SwingWorker.StateValue.DONE == event.getNewValue()) {
					delayed.setEnabled(true);
				}
			}
		};
		enabler.addPropertyChangeListener(listener);
		
		//Add cancel button, which simply records that user didn't click ok,
		//and hides the dialog
		JButton immediate = new JButton(immediateString);
		immediate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				immediate();
			}
		});
		buttonPanel.add(immediate);
		getRootPane().setDefaultButton(immediate);
		
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.setBorder(Borders.DIALOG_BORDER);
		return panel;
	}

	/**
	 * Does the same as clicking immediate button - dialog will be hidden and disposed, and
	 * {@link #lastClickWasDelayedOption()} will return false 
	 */
	public void immediate() {
		lastClickWasDelayedOption = false;
		closeDialog();
	}
	
	/**
	 * Does the same as clicking delayed button - dialog will be hidden and disposed, and
	 * {@link #lastClickWasDelayedOption()} will return true
	 */
	public void delayed() {
		lastClickWasDelayedOption = true;
		closeDialog();
	}

	private void closeDialog() {
		setVisible(false);
		getContentPane().removeAll();
		dispose();
	}
	
	/**
	 * Display the dialog, and start timer that will enable the delayed option.
	 * Note that the dialog will remain open and modal until
	 * ok or cancel is clicked, at which point it will be hidden
	 * and disposed. You can then use {@link #lastClickWasDelayedOption()} to
	 * see which button was used to close the dialog.
	 */
	public void displayDialog() {
		//This displays window in sensible position, and eliminates the 
		//irritating "flick" where the window appears for a tiny interval 
		//(or just causes screen to be greyed in that area) then moves to
		//another position.
		setLocationRelativeTo(owner);
		enabler.execute();
		setVisible(true);
	}
}