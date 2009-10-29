package org.jpropeller.view.list.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.Views;
import org.jpropeller.view.table.impl.Messages;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for a single selected element of a {@link CList} 
 * to be moved by a set number of indices.
 * 
 * @param <T>		The type of element in the {@link CList} 
 */
public class MultiSelectionListMoveAction<T> extends AbstractAction implements ChangeListener, UpdatableView {

	private final static Icon iconUp = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-up");
	private final static Icon iconDown = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-down");

	private final Prop<? extends CList<T>> list;
	private final Prop<? extends CCollection<Integer>> selection;

	private final int movement;
	
	private UpdateManager updateManager;

	/**
	 * Create a {@link MultiSelectionListMoveAction}
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
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
	public MultiSelectionListMoveAction(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			int movement,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		if (movement == 0) throw new IllegalArgumentException("Cannot have movement of 0 - would do nothing");
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.list = list;
		this.selection = selection;
		this.movement = movement;

		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		//Start out updated
		updateManager.updateRequiredBy(this);
		
		//Listen to the list and selection, we can use this to
		//enable and disable the action
		list.features().addListener(this);
		if (selection != null) {
			selection.features().addListener(this);
		}

	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action to move one index up (decrement index), with
	 * no icon. Text, mnemonic and tooltip set by resources.
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @return
	 * 		A new {@link MultiSelectionListMoveAction} to move items up
	 * @param <T>
	 * 		The type of element in the {@link CList} 
	 */
	public static <T> MultiSelectionListMoveAction<T> createUpAction(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection
			) {
		return new MultiSelectionListMoveAction<T>(
				list,
				selection,
				-1, 
				Messages.getString("ListMoveAction.moveUpText"), 
				iconUp, 
				Messages.getString("ListMoveAction.moveUpDescription"), 
				Messages.getInt("ListMoveAction.moveUpMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create an action to move one index down (increment index), with
	 * no icon. Text, mnemonic and tooltip set by resources.
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @return
	 * 		A new {@link MultiSelectionListMoveAction} to move items down
	 * @param <T>
	 * 		The type of element in the {@link CList} 
	 */
	public static <T> MultiSelectionListMoveAction<T> createDownAction(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection
			) {
		return new MultiSelectionListMoveAction<T>(
				list,
				selection,
				1, 
				Messages.getString("ListMoveAction.moveDownText"), 
				iconDown, 
				Messages.getString("ListMoveAction.moveDownDescription"), 
				Messages.getInt("ListMoveAction.moveDownMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Perform actions atomically
		Props.acquire();
		try {
			
			//Can't move if no list
			if (list.get() == null) {
				return;
			}

			//If there are multiple or no indices selected, cannot move
			int index = -1;
			if (selection.get().size() == 1) {
				index = selection.get().iterator().next();
			}

			int size = list.get().size();
			
			//Check current index is valid
			if (index < 0 || index >= size) return;
			
			int target = index + movement;
			
			//Can't move outside list
			if (target < 0 || target >= size) return;
			
			//Remove the element
			T element = list.get().remove(index);
			
			//If the movement is positive, we need to allow for the fact that
			//we just removed an element
			//if (movement > 0) target--;
			
			//Reinsert the element
			list.get().add(target, element);
			
			//TODO we can remove the need to manually adjust the selection
			//for increments and decrements (movement size 1). For example, for a movement
			//of 1, we can remove the element AFTER the selected element, then insert it
			//BEFORE the selected element, this will move the selected element without
			//deselecting it
			
			//Reselect the element (recalculate the target index since we may have adjusted it)
			selection.get().clear();
			selection.get().add(index + movement);
			
		} finally {
			Props.release();			
		}

	}

	@Override
	public void update() {
		if (list.get() == null) {
			setEnabled(false);
			return;
		}

		//If there are multiple or no indices selected, cannot move
		int index = -1;
		if (selection.get().size() == 1) {
			index = selection.get().iterator().next();
		}
		
		int target = index + movement;
		
		//Enabled when movement is possible
		setEnabled(index >= 0 && index < list.get().size() && target >= 0 && target < list.get().size());
	}
	
	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);

		list.features().removeListener(this);
		if (selection != null) {
			selection.features().removeListener(this);
		}
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
