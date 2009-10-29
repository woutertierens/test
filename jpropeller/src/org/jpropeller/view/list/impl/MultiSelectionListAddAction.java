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
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.Views;
import org.jpropeller.view.table.impl.Messages;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for an element to be inserted into a {@link CList}.
 * New elements are got from a {@link Source} 
 * @param <T>		The type of element in the list 
 */
public class MultiSelectionListAddAction<T> extends AbstractAction implements ChangeListener, UpdatableView {

	private final static Icon icon = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "list-add");
	
	private final Prop<? extends CList<T>> list;
	private final Prop<? extends CCollection<Integer>> selection;
	private final Source<T> source;

	private final UpdateManager updateManager;

	/**
	 * Create a {@link MultiSelectionListAddAction}
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices, OR 
	 * 					null if there is no trackable selection.
	 * @param source	The source of new elements to add to the list
	 * @param text		Description text for action
	 * @param icon		Icon for action
	 * @param desc		Short description for action (e.g. for tooltip)
	 * @param mnemonic	Mnemonic for action, should be a {@link KeyEvent} value, or
	 * 					negative to have no mnemonic
	 * 					e.g. {@link KeyEvent#VK_A}
	 */
	public MultiSelectionListAddAction(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			Source<T> source,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.list = list;
		this.selection = selection;
		this.source = source;
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to the list and selection, we can use this to
		//enable and disable the action
		list.features().addListener(this);
		if (selection != null) {
			selection.features().addListener(this);
		}

		//Start out updated
		updateManager.updateRequiredBy(this);

	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action with no icon. Text, mnemonic and tooltip set by resources.
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices, OR 
	 * 					null if there is no trackable selection.
	 * @param source	The source of new elements to add to the list
	 * @return			A new {@link MultiSelectionListAddAction}
	 * @param <T>		The type of element in the list 
	 */
	public static <T> MultiSelectionListAddAction<T> create(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection, 
					Source<T> source) {
		return new MultiSelectionListAddAction<T>(
				list,
				selection,
				source,
				Messages.getString("ListAddAction.addText"), 
				icon, 
				Messages.getString("ListAddAction.addDescription"), 
				Messages.getInt("ListAddAction.addMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Perform actions atomically
		Props.acquire();
		try {
			//Can't add if there is no list
			if (list.get() == null) {
				return;
			}
			
			//Use last index in multiselection, or
			//last element of list if it is not present and valid
			int index = -1;
			for (int i : selection.get()) {
				if (i > index) index = i;
			}
			
			//Check current index is valid - if not
			//use last element of list
			if (index < 0 || index >= list.get().size()) {
				index = list.get().size()-1;
			}

			try {
				T newElement = source.get();
				
				//Add the new element
				list.get().add(index + 1, newElement);
				
				//If we had no selection, OR a single element
				//was selected, then select just the new element,
				//otherwise add the new element to the selection
				if (selection.get().size() < 2) {
					selection.get().clear();
				}
				selection.get().add(index+1);

			} catch (NoInstanceAvailableException niae) {
				//Nothing to do - most likely user cancelled selection
			}
			
		} finally {
			Props.release();			
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
		//Enabled unless list is null
		setEnabled(list.get() != null);
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
