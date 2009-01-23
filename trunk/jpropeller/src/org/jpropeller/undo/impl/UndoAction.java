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
 *triggered to undo changes. Updates to reflect whether
 *the {@link UndoSystem} can currently undo 
 */
public class UndoAction extends AbstractAction implements UndoSystemListener {

	UndoSystem system;
	
	/**
	 * Create a {@link UndoAction}
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
	public UndoAction(
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
	 * Make a default {@link UndoAction}
	 * @param system
	 * 		The {@link UndoSystem}
	 * @return
	 * 		A new {@link UndoAction}
	 */
	public static UndoAction create(UndoSystem system) {
		return new UndoAction(system, Messages.getString("UndoAction.text"), null, Messages.getString("UndoAction.description"), Messages.getInt("UndoAction.mnemonic")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		system.undo();
	}

	private void update() {
		setEnabled(system.canUndo());
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