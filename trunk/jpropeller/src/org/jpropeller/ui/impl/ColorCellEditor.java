package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

/**
 * Simple {@link TableCellEditor} for {@link Color}, showing a 
 * {@link JColorChooser} to perform edit.
 */
public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {

	//FIXME can we extend DefaultCellEditor here to get better default behaviour?
	
	Color currentColor;
	JButton button;
	ColorPaletteChooser chooser;
	protected static final String EDIT = "edit";
	
	private final ColorSwatchIcon icon = new ColorSwatchIcon();
	JLabel label = new JLabel(icon, SwingConstants.CENTER);

	/**
	 * Create a default {@link ColorCellEditor}
	 */
	public ColorCellEditor() {
		
		chooser = ColorPaletteChooser.create();

	}

	private void edit() {
		//New dialog each time
		OKCancelDialog dialog = new OKCancelDialog(chooser.getPanel(), null, "Select color", "OK", "Cancel");
		
		//The user has clicked the cell, so
		//bring up the dialog.
		chooser.setPreviousColor(currentColor);
		dialog.setVisible(true);

		//This runs when the dialog is closed
		if (dialog.lastClickWasOK()) {
			Color color = chooser.getSelectedColor();
			if (color != null) {
				currentColor = color;
			}
		}
		
		//Make the renderer reappear, after all editing completes
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fireEditingStopped();			
			}
		});
		 
	}
	
	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		return currentColor;
	}

	//Implement the one method defined by TableCellEditor.
	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		currentColor = (Color)value;
		icon.setColor(currentColor);
		edit();
		return label;
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		//Only double clicks start editing using mouse
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= 2;
		}
		return true;
	}
}
