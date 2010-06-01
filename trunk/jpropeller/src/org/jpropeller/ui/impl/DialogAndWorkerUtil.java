package org.jpropeller.ui.impl;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.SwingWorker;


/**
 * Utilities to display a dialog while work is done in a background thread
 */
public class DialogAndWorkerUtil {

	/**
	 * MUST BE CALLED FROM EDT, Swing Thread
	 * <br />
	 * Sets up code to hide and dispose a dialog when a swing worker 
	 * completes, then starts the swing worker and shows the dialog.
	 * Note that this is designed to allow for locking the interface 
	 * using a modal dialog while a swing worker performs a task in the
	 * background, so the dialog will often be modal. If the dialog is 
	 * NOT modal, be aware that the user may still use the GUI while the
	 * SwingWorker is operating.
	 * 
	 * Note that the SwingWorker need only perform the tasks required
	 * that are NOT associated with the dialog - a property change 
	 * listener is added to the worker to handle hiding, clearing and disposing the
	 * dialog. Hence the background and swing thread tasks performed by the
	 * SwingWorker do not need to interact with the dialog.
	 * 
	 * NOTE: If you also use the dialog to display progress of the background
	 * task, remember to interact with the dialog in the EDT (Swing thread),
	 * e.g. by using SwingUtilities.invokeLater(...) to make changes to the
	 * dialog.
	 * 
	 * @param dialog
	 * 		The dialog to show while the swingWorker proceeds
	 * @param swingWorker
	 * 		The SwingWorker to execute while the dialog is shown.
	 */
	public static void dialogWhileWorking(JDialog dialog, SwingWorker<?, ?> swingWorker) {
		
		/**
		 * This will close a dialog when it sees a property change
		 * indicating a swing worker has completed
		 */
		class SwingWorkerCompletionWaiter implements PropertyChangeListener {
			private JDialog dialog;

			SwingWorkerCompletionWaiter(JDialog dialog) {
				this.dialog = dialog;
			}

			public void propertyChange(PropertyChangeEvent event) {
				if ("state".equals(event.getPropertyName())
						&& SwingWorker.StateValue.DONE == event.getNewValue()) {
					
					//Note this runs from the EDT, after the worker has
					//finished - hence it MUST run after the setVisible(true) line below
					dialog.setVisible(false);
					dialog.getContentPane().removeAll();
					dialog.dispose();
				}
			}
		}
		
		//Hide, clear and dispose the dialog when the worker completes
		swingWorker.addPropertyChangeListener(new SwingWorkerCompletionWaiter(
				dialog));
		swingWorker.execute();
		
		// the dialog will be visible until the SwingWorker is done
		//Note that the swing worker will always have to run its done method
		//and lead to the dialog being hidden AFTER the line below, since the
		//line below runs directly in the EDT, whereas the swing worker done method
		//has to be added to the event queue
		dialog.setVisible(true);
	}
	
	/**
	 * MUST BE CALLED FROM EDT, Swing Thread
	 * <br />
	 * Perform a task in a background thread, while a modal busy dialog is displayed,
	 * then hide the dialog when the task is complete
	 * @param owner		The owner for the dialog
	 * @param title		The title for the dialog
	 * @param message	The message to display in the dialog
	 * @param task		The task to perform in a background thread, while the dialog
	 * 					is displayed.
	 */
	public static void busyDialogWhileRunning(Frame owner, String title, String message, final Runnable task) {
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				task.run();
				return null;
			}
		};
		busyDialogWhileWorking(owner, title, message, worker);
	}
	
	/**
	 * MUST BE CALLED FROM EDT, Swing Thread
	 * <br />
	 * Execute a {@link SwingWorker}, while a modal busy dialog is displayed,
	 * then hide the dialog when the worker is complete
	 * @param owner		The owner for the dialog
	 * @param title		The title for the dialog
	 * @param message	The message to display in the dialog
	 * @param worker	The worker to execute, while the dialog
	 * 					is displayed.
	 */
	public static void busyDialogWhileWorking(Frame owner, String title, String message, final SwingWorker<?, ?> worker) {
		final ComponentDialog busyDialog = ComponentDialog.createBusyDialog(owner, title, message);
		busyDialog.setLocationRelativeTo(owner);
		dialogWhileWorking(busyDialog, worker);
	}
	
	/**
	 * MUST BE CALLED FROM EDT, Swing Thread
	 * <br />
	 * Execute a {@link SwingWorker}, while a modal dialog displays a given component,
	 * then hide the dialog when the worker is complete
	 * @param owner		The owner for the dialog
	 * @param title		The title for the dialog
	 * @param component	The {@link Component} to display in the dialog
	 * @param worker	The worker to execute, while the dialog
	 * 					is displayed.
	 */
	public static void componentDialogWhileWorking(Frame owner, String title, Component component, final SwingWorker<?, ?> worker) {
		final ComponentDialog dialog = ComponentDialog.createComponentDialog(owner, title, component);
		dialog.setLocationRelativeTo(owner);
		dialogWhileWorking(dialog, worker);
	}
}
