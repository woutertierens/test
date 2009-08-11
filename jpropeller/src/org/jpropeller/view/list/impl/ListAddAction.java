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
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.Views;
import org.jpropeller.view.table.impl.Messages;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for an element to be inserted into an {@link CList}.
 * The {@link CList} is referenced by a {@link ListAndSelectionReference}, 
 * and the selection of that reference is used as the insertion point. New elements
 * are got from a {@link Source} 
 * @param <T>		The type of element in the list 
 */
public class ListAddAction<T> extends AbstractAction implements ChangeListener, UpdatableView {

	private final static Icon icon = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "list-add");
	
	ListAndSelectionReference<T> reference;
	Source<T> source;

	private UpdateManager updateManager;

	/**
	 * Create a {@link ListAddAction}
	 * @param reference
	 * 		Reference to the {@link CList} to act on, and the selection index of
	 * the item to insert after.
	 * @param source
	 * 		The source of new elements to add to the list
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
	public ListAddAction(
			ListAndSelectionReference<T> reference,
			Source<T> source,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.reference = reference;
		this.source = source;
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to the list and selection of the reference, we can use this to
		//enable and disable the action
		reference.value().features().addListener(this);
		reference.selection().features().addListener(this);

		//Start out updated
		updateManager.updateRequiredBy(this);

	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action with
	 * no icon. Text, mnemonic and tooltip set by resources.
	 * @param reference
	 * 		Reference to the {@link CList} to act on, and the selection index of
	 * the item to delete.
	 * @param source
	 * 		The source of new elements to add to the list
	 * @return
	 * 		A new {@link ListAddAction}
	 * @param <T>
	 * 		The type of element in the list 
	 */
	public static <T> ListAddAction<T> create(ListAndSelectionReference<T> reference, Source<T> source) {
		return new ListAddAction<T>(
				reference,
				source,
				Messages.getString("ListAddAction.addText"), 
				icon, 
				Messages.getString("ListAddAction.addDescription"), 
				Messages.getInt("ListAddAction.addMnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
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
			
			//Check current index is valid - if not
			//use last element of list
			if (index < 0 || index >= list.size()) {
				index = list.size()-1;
			}

			try {
				T newElement = source.get();
				
				//Add the new element
				list.add(index + 1, newElement);
				
				//Select the newly added element
				reference.selection().set(index+1);
				
			} catch (NoInstanceAvailableException niae) {
				//Nothing to do - most likely user cancelled selection
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
		//Enabled unless list is null
		CList<?> list = reference.value().get();
		setEnabled(list != null);
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
