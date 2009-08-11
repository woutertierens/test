package org.jpropeller.view.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.jpropeller.bean.Bean;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;
import org.jpropeller.util.Target;
import org.jpropeller.view.Views;

/**
 * An action allowing for a {@link Target} to be given a 
 * value from a {@link Source} - implementing a "File->New" action
 * When the {@link Source} returns no instance, nothing will be done.
 * @param <T> The type of value in the {@link Source} and {@link Target}
 */
public class NewToTargetAction<T> extends AbstractAction {

	private final static Icon icon = Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "document-new");
	private final Source<? extends T> source;
	private final Target<? super T> target;
	
	/**
	 * Create a {@link NewToTargetAction}
	 * @param source		Source of new instances
	 * @param target		Target for new instances
	 * @param text			Description text for action
	 * @param icon			Icon for action
	 * @param desc			Short description for action (e.g. for tooltip)
	 * @param mnemonic		Mnemonic for action, should be a {@link KeyEvent} 
	 * 						value, or negative to have no mnemonic
	 * 						e.g. {@link KeyEvent#VK_A}
	 */
	public NewToTargetAction(
			Source<? extends T> source,
			Target<? super T> target,
			String text, Icon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}

		this.source = source;
		this.target = target;
	}
	
	/**
	 * Create an action with default icon. Text, mnemonic and tooltip set by resources.
	 * @param source		Source of new instances
	 * @param target		Target for new instances
	 * @return				A new {@link NewToTargetAction}
	 * @param <T>			The type of instance 
	 */
	public static <T extends Bean> NewToTargetAction<T> create(
			Source<? extends T> source,
			Target<? super T> target) {
		return new NewToTargetAction<T>(
				source,
				target,
				Messages.getString("NewAction.text"), 
				icon,
				Messages.getString("NewAction.description"), 
				Messages.getInt("NewAction.mnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	//We check that object is assignable from Class<T>, before casting to T
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			target.put(source.get());
		} catch (NoInstanceAvailableException niae) {
			//No need to do anything - no new instance was available
		}
	}
}
