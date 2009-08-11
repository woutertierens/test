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
import org.jpropeller.util.Target;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.Views;
import org.jpropeller.view.table.impl.Messages;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for an element of an {@link CList} to be deleted.
 * The {@link CList} is referenced by a {@link ListAndSelectionReference}, and the item deleted
 * is the one selected by that reference. The item is passed to a {@link Target} if one is
 * specified
 * @param <T>		The type of element in the list 
 */
public class ListDeleteAction<T> extends AbstractAction implements ChangeListener, UpdatableView {

	private final static Icon ICON = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "list-remove");

	private ListAndSelectionReference<T> reference;
	
	private Target<T> target;
	
	private UpdateManager updateManager;

	/**
	 * Create a {@link ListDeleteAction} with a target,
	 * to which items removed from the list are passed.
	 * 
	 * @param reference	Reference to the {@link CList} to act on, 
	 * 					and the selection index of the item to delete.
	 * @param target	Items removed from the list are passed
	 * 					to this target immediately after they are
	 * 					removed
	 * @param text		Description text for action
	 * @param icon		Icon for action
	 * @param desc		Short description for action (e.g. for tooltip)
	 * @param mnemonic	Mnemonic for action, should be a {@link KeyEvent} value, or
	 * 					negative to have no mnemonic
	 * 					e.g. {@link KeyEvent#VK_A}
	 */
	public ListDeleteAction(
			ListAndSelectionReference<T> reference,
			Target<T> target,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		this.target = target;
		
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
	
	/**
	 * Create a {@link ListDeleteAction} which does not have a target,
	 * and so simply removes items from the list
	 * 
	 * @param reference	Reference to the {@link CList} to act on, 
	 * 					and the selection index of the item to delete.
	 * @param text		Description text for action
	 * @param icon		Icon for action
	 * @param desc		Short description for action (e.g. for tooltip)
	 * @param mnemonic	Mnemonic for action, should be a {@link KeyEvent} value, or
	 * 					negative to have no mnemonic
	 * 					e.g. {@link KeyEvent#VK_A}
	 */
	public ListDeleteAction(
			ListAndSelectionReference<T> reference,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		this(reference, null, text, icon, desc, mnemonic);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action with default icon. Text, mnemonic and 
	 * tooltip set by resources.
	 * 
	 * @param reference	Reference to the {@link CList} to act on, 
	 * 					and the selection index of the item to delete.
	 * @return			A new {@link ListDeleteAction}
	 * @param <T>	The type of element in the lists
	 */
	public static <T> ListDeleteAction<T> create(ListAndSelectionReference<T> reference) {
		return create(reference, null);
	}

	/**
	 * Create an action with default icon. Text, mnemonic and 
	 * tooltip set by resources.
	 * 
	 * @param reference	Reference to the {@link CList} to act on, 
	 * 					and the selection index of the item to delete.
	 * @param target	Items removed from the list are passed
	 * 					to this target immediately after they are
	 * 					removed
	 * @return			A new {@link ListDeleteAction}
	 * @param <T>	The type of element in the lists
	 */
	public static <T> ListDeleteAction<T> create(ListAndSelectionReference<T> reference, Target<T> target) {
		return new ListDeleteAction<T>(
				reference,
				target,
				Messages.getString("ListDeleteAction.deleteText"), 
				ICON, 
				Messages.getString("ListDeleteAction.deleteDescription"), 
				Messages.getInt("ListDeleteAction.deleteMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
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
			
			//Remove the element
			T removed = list.remove(index);
			
			if (target != null) {
				target.put(removed);
			}
			
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
		CList<?> list = reference.value().get();
		
		if (list == null) {
			setEnabled(false);
			return;
		}
		
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
	public boolean isEditing() {
		//Instant editing
		return false;
	}

}
