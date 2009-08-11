package org.jpropeller.ui.impl;

import java.awt.Component;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Implements a {@link CellEditor} that uses a {@link JTextField}
 * to edit {@link String}s, but selects all contents of the 
 * cell when editing starts (like any sane default table editor 
 * should already)
 * This behaviour only occurs on {@link String} values - other values
 * will get the same editor as provided by {@link DefaultCellEditor}
 */
public class SelectingTextCellEditor extends DefaultCellEditor {

	private final static SelectingTextCellEditor SHARED_INSTANCE = new SelectingTextCellEditor();
	
	/**
	 * Get the single shared instance of the {@link SelectingTextCellEditor}
	 * @return
	 * 		Shared editor instance
	 */
	public final static SelectingTextCellEditor editor() {
		return SHARED_INSTANCE;
	}
	
	/**
	 * Create a {@link SelectingTextCellEditor}
	 */
	private SelectingTextCellEditor() {
		super(new JTextField());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		
		Component superEditor = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		
		if (superEditor instanceof JTextField) {
			//Get editor from parent, but select all contents before returning
			JTextField tf = (JTextField) superEditor;
			tf.selectAll();
			return tf;
		} else {
			return superEditor;
		}
	}
}