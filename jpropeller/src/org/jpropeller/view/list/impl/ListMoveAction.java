package org.jpropeller.view.list.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.list.selection.ListAndSelectionReference;
import org.jpropeller.system.Props;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.Views;
import org.jpropeller.view.table.impl.Messages;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for an element of an {@link CList} to be moved by a set number of indices.
 * The {@link CList} is referenced by a {@link ListAndSelectionReference}, and the item moved
 * is the one selected by that reference.
 * @param <T>
 * 		The type of element in the {@link CList} 
 */
public class ListMoveAction<T> extends AbstractAction implements ChangeListener, UpdatableView {

	private final static Icon iconUp = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-up");
	private final static Icon iconDown = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-down");

	int movement;
	ListAndSelectionReference<T> reference;
	
	private UpdateManager updateManager;

	/**
	 * Create a {@link ListMoveAction}
	 * @param reference
	 * 		Reference to the {@link CList} to act on, and the selection index of
	 * the item to move.
	 * @param movement
	 * 		The number of indices the current selection will be moved when the action is used
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
	public ListMoveAction(
			ListAndSelectionReference<T> reference,
			int movement,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		if (movement == 0) throw new IllegalArgumentException("Cannot have movement of 0 - would do nothing");
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.reference = reference;
		this.movement = movement;

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
	 * Create an action to move one index up (decrement index), with
	 * no icon. Text, mnemonic and tooltip set by resources.
	 * @param reference
	 * 		Reference to the {@link CList} to act on, and the selection index of
	 * the item to move.
	 * @return
	 * 		A new {@link ListMoveAction} to move items up
	 * @param <T>
	 * 		The type of element in the {@link CList} 
	 */
	public static <T> ListMoveAction<T> createUpAction(ListAndSelectionReference<T> reference) {
		return new ListMoveAction<T>(
				reference,
				-1, 
				Messages.getString("ListMoveAction.moveUpText"), 
				iconUp, 
				Messages.getString("ListMoveAction.moveUpDescription"), 
				Messages.getInt("ListMoveAction.moveUpMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create an action to move one index down (increment index), with
	 * no icon. Text, mnemonic and tooltip set by resources.
	 * @param reference
	 * 		Reference to the {@link CList} to act on, and the selection index of
	 * the item to move.
	 * @return
	 * 		A new {@link ListMoveAction} to move items down
	 * @param <T>
	 * 		The type of element in the {@link CList} 
	 */
	public static <T> ListMoveAction<T> createDownAction(ListAndSelectionReference<T> reference) {
		return new ListMoveAction<T>(
				reference,
				1, 
				Messages.getString("ListMoveAction.moveDownText"), 
				iconDown, 
				Messages.getString("ListMoveAction.moveDownDescription"), 
				Messages.getInt("ListMoveAction.moveDownMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Perform actions atomically
		Props.getPropSystem().getChangeSystem().acquire();
		try {
			
			int index = reference.selection().get();
			CList<T> list = reference.value().get();

			if (list == null) {
				return;
			}

			//Check current index is valid
			if (index < 0 || index >= list.size()) return;
			
			int target = index + movement;
			
			//Can't move outside list
			if (target < 0 || target >= list.size()) return;
			
			//Remove the element
			T element = list.remove(index);
			
			//If the movement is positive, we need to allow for the fact that
			//we just removed an element
			//if (movement > 0) target--;
			
			//Reinsert the element
			list.add(target, element);
			
			//TODO if possible, batch the changes by informing the change system before we start
			
			//TODO we can remove the need to manually adjust the selection
			//for increments and decrements (movement size 1). For example, for a movement
			//of 1, we can remove the element AFTER the selected element, then insert it
			//BEFORE the selected element, this will move the selected element without
			//deselecting it
			//Reselect the element (recalculate the target index since we may have adjusted it)
			reference.selection().set(index + movement);
			
		} finally {
			Props.getPropSystem().getChangeSystem().release();			
		}

	}

	@Override
	public void update() {
		int index = reference.selection().get();
		CList<T> list = reference.value().get();
		
		if (list == null) {
			setEnabled(false);
			return;
		}

		int target = index + movement;
		
		//Enabled when movement is possible
		setEnabled(index >= 0 && index < list.size() && target >= 0 && target < list.size());
	}
	
	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);

		reference.value().features().removeListener(this);
		reference.selection().features().removeListener(this);
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
	public boolean isEditing() {
		//Instant editing
		return false;
	}
}
