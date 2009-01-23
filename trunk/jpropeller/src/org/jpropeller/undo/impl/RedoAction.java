package org.jpropeller.undo.impl;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.jpropeller.undo.UndoSystem;
import org.jpropeller.undo.UndoSystemListener;

/**
 *	An {@link Action} allowing an {@link UndoSystem} to be
 *triggered to redo changes. Updates to reflect whether
 *the {@link UndoSystem} can currently redo 
 */
public class RedoAction extends AbstractAction implements UndoSystemListener {

	UndoSystem system;
	
	/**
	 * Create a {@link RedoAction}
	 * @param system
	 * 		The system this action will control 
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
	public RedoAction(
			UndoSystem system,
			String text, ImageIcon icon,
            String desc, Integer mnemonic) {
		
		super(text, icon);
		
		this.system = system;
		system.addListener(this);
		
		update();
		
		putValue(SHORT_DESCRIPTION, desc);
		
		if (mnemonic != null && mnemonic >= 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}
	}
	
	/**
	 * Make a default {@link RedoAction}
	 * @param system
	 * 		The {@link UndoSystem}
	 * @return
	 * 		A new {@link RedoAction}
	 */
	public static RedoAction create(UndoSystem system) {
		return new RedoAction(system, Messages.getString("RedoAction.text"), null, Messages.getString("RedoAction.description"), Messages.getInt("RedoAction.mnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		system.redo();
	}

	private void update() {
		setEnabled(system.canRedo());
	}
	
	@Override
	public void changed(UndoSystem system) {
		update();
	}

	@Override
	public void redone(UndoSystem system) {
		update();
	}

	@Override
	public void undone(UndoSystem system) {
		update();
	}

}
