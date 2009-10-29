package org.jpropeller.view.list.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.jpropeller.util.Target;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.Views;
import org.jpropeller.view.table.impl.Messages;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for selected elements of a {@link CList} to be deleted.
 * Deleted items are passed to a {@link Target} if one is specified
 * @param <T>		The type of element in the list 
 */
public class MultiSelectionListDeleteAction<T> extends AbstractAction implements ChangeListener, UpdatableView {

	private final static Icon ICON = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "list-remove");

	private final Prop<? extends CList<T>> list;
	private final Prop<? extends CCollection<Integer>> selection;

	private Target<T> target;
	
	private UpdateManager updateManager;

	/**
	 * Create a {@link MultiSelectionListDeleteAction} with a target,
	 * to which items removed from the list are passed.
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
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
	public MultiSelectionListDeleteAction(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			Target<T> target,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		this.target = target;
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.list = list;
		this.selection = selection;
		
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
	
	/**
	 * Create a {@link MultiSelectionListDeleteAction} which does not have a target,
	 * and so simply removes items from the list
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @param text		Description text for action
	 * @param icon		Icon for action
	 * @param desc		Short description for action (e.g. for tooltip)
	 * @param mnemonic	Mnemonic for action, should be a {@link KeyEvent} value, or
	 * 					negative to have no mnemonic
	 * 					e.g. {@link KeyEvent#VK_A}
	 */
	public MultiSelectionListDeleteAction(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		this(list, selection, null, text, icon, desc, mnemonic);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action with default icon. Text, mnemonic and 
	 * tooltip set by resources.
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @return			A new {@link MultiSelectionListDeleteAction}
	 * @param <T>		The type of element in the lists
	 */
	public static <T> MultiSelectionListDeleteAction<T> create(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection) {
		return create(list, selection, null);
	}

	/**
	 * Create an action with default icon. Text, mnemonic and 
	 * tooltip set by resources.
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @param target	Items removed from the list are passed
	 * 					to this target immediately after they are
	 * 					removed
	 * @return			A new {@link MultiSelectionListDeleteAction}
	 * @param <T>		The type of element in the lists
	 */
	public static <T> MultiSelectionListDeleteAction<T> create(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection, 
					Target<T> target) {
		return new MultiSelectionListDeleteAction<T>(
				list,
				selection,
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
			//Can't delete from null list
			if (list.get() == null) {
				return;
			}
			
			//If there is no selection, do nothing
			if (selection.get().isEmpty()) return;
			
			//Make reverse sorted set of selected indices, then
			//iterate it and remove in that order, otherwise
			//the deletion affects the indices yet to be removed.
			SortedSet<Integer> reverseIndices = new TreeSet<Integer>(new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return -o1.compareTo(o2);
				}
			});
			reverseIndices.addAll(selection.get());
			
			//Find the index after the highest deleted index
			int highest = reverseIndices.first();
			//Work out where the index AFTER this will be,
			//after we perform the deletions
			int newSelection = highest - reverseIndices.size() + 1;

			//Clear the selection
			selection.get().clear();

			//Remove the elements
			for (int i : reverseIndices) {
				T removed = list.get().remove(i);
				
				if (target != null) {
					target.put(removed);
				}
			}
			//If the new selection is after the end of the list, this
			//indicates we deleted the last element, so select the last
			//element
			if (newSelection == list.get().size()) {
				newSelection = list.get().size() - 1;
			}
			//If the new selection is valid, make it
			if (newSelection >= 0 && newSelection < list.get().size()) {
				selection.get().add(newSelection);
			}
			
		} finally {
			Props.getPropSystem().getChangeSystem().release();			
		}

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
	public void update() {
		//Enabled when we have a non-empty selection, and a list
		setEnabled(list.get()!=null && selection.get()!= null && !selection.get().isEmpty());
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
