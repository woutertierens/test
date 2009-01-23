package org.jpropeller.view.table.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.list.ListAndSelectionReference;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.View;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for an element of an {@link ObservableList} to be deleted.
 * The {@link ObservableList} is referenced by a {@link ListAndSelectionReference}, and the item deleted
 * is the one selected by that reference.
 */
public class ListDeleteAction extends AbstractAction implements ChangeListener, Updatable, View<ObservableList<?>> {

	ListAndSelectionReference<?> reference;
	
	private UpdateManager updateManager;

	/**
	 * Create a {@link ListDeleteAction}
	 * @param reference
	 * 		Reference to the {@link ObservableList} to act on, and the selection index of
	 * the item to delete.
	 * @param text
	 * 		Description text for action
	 * @param icon
	 * 		Icon for action
	 * @param desc
	 * 		Short description for action (e.g. for tooltip)
	 * @param mnemonic
	 * 		Mnemonic for action, should be a {@link KeyEvent} value, or
	 * negative to have no mnemonic
	 * e.g. {@link KeyEvent#VK_A}
	 */
	public ListDeleteAction(
			ListAndSelectionReference<?> reference,
			String text, ImageIcon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.reference = reference;
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		//Start out updated
		updateManager.updateRequiredBy(this);

		//Listen to the list and selection of the reference, we can use this to
		//enable and disable the action
		reference.value().features().addListener(this);
		reference.selection().features().addListener(this);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action with
	 * no icon. Text, mnemonic and tooltip set by resources.
	 * @param reference
	 * 		Reference to the {@link ObservableList} to act on, and the selection index of
	 * the item to delete.
	 * @return
	 * 		A new {@link ListDeleteAction}
	 */
	public static ListDeleteAction create(ListAndSelectionReference<?> reference) {
		return new ListDeleteAction(
				reference,
				Messages.getString("ListDeleteAction.deleteText"), 
				null, 
				Messages.getString("ListDeleteAction.deleteDescription"), 
				Messages.getInt("ListDeleteAction.deleteMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Perform actions atomically
		Props.getPropSystem().getChangeSystem().acquire();
		try {
			int index = reference.selection().get();
			ObservableList<?> list = reference.value().get();

			//Check current index is valid
			if (index < 0 || index >= list.size()) return;
			
			//Remove the element
			list.remove(index);
		} finally {
			Props.getPropSystem().getChangeSystem().release();			
		}

	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);

		reference.value().features().removeListener(this);
		reference.selection().features().removeListener(this);
	}

	@Override
	public void update() {
		int index = reference.selection().get();
		ObservableList<?> list = reference.value().get();
		
		//Enabled when index is in list
		setEnabled(index >= 0 && index < list.size());
	}
	
	@Override
	public void cancel() {
		//Instant editing
	}

	@Override
	public void commit() throws CompletionException {
		//Instant editing
	}

	@Override
	public Reference<? extends ObservableList<?>> getModel() {
		return reference;
	}

	@Override
	public boolean isEditing() {
		//Instant editing
		return false;
	}

}
