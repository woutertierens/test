package org.jpropeller.view.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.util.Source;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.Views;
import org.jpropeller.view.update.UpdateManager;

/**
 * An action allowing for the value of the model to be set to
 * new instances from a {@link Source}
 * @param <T> The type of value in the model 
 */
public class NewAction<T extends Bean> extends AbstractAction implements ChangeListener, UpdatableSingleValueView<T> {

	private final static Icon icon = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "document-new");
	private final Reference<T> reference;
	private final UpdateManager updateManager;
	private final Source<T> source;
	
	/**
	 * Create a {@link NewAction}
	 * @param source		{@link Source} of new instances
	 * @param reference		Reference to the value to set with opened file
	 * @param text			Description text for action
	 * @param icon			Icon for action
	 * @param desc			Short description for action (e.g. for tooltip)
	 * @param mnemonic		Mnemonic for action, should be a {@link KeyEvent} 
	 * 						value, or negative to have no mnemonic
	 * 						e.g. {@link KeyEvent#VK_A}
	 */
	public NewAction(
			Source<T> source,
			Reference<T> reference,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		this.source = source;
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		this.reference = reference;
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to the model
		reference.value().features().addListener(this);

		//Start out updated
		updateManager.updateRequiredBy(this);
		
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	/**
	 * Create an action with default icon. Text, mnemonic and tooltip set by resources.
	 * @param source		The source of new elements to set
	 * @param reference		Reference to the {@link CList} to act on, and 
	 * 						the selection index of the item to delete.
	 * @return				A new {@link NewAction}
	 * @param <T>			The type of element in the list 
	 */
	public static <T extends Bean> NewAction<T> create(Source<T> source, Reference<T> reference) {
		return new NewAction<T>(
				source,
				reference,
				Messages.getString("NewAction.text"), 
				icon,
				Messages.getString("NewAction.description"), 
				Messages.getInt("NewAction.mnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		reference.value().set(source.get());
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);

		reference.value().features().removeListener(this);
	}

	@Override
	public void update() {
		//Can't set new if value is not editable
		setEnabled(reference.value().getEditability() == PropEditability.EDITABLE);
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
	public Reference<T> getModel() {
		return reference;
	}

	@Override
	public boolean isEditing() {
		//Instant editing
		return false;
	}

}
