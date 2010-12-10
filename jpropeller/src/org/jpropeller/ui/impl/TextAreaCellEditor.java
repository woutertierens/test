package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import org.jpropeller.view.info.Notable;

/**
 * Simple {@link TableCellEditor} for {@link String}, showing a 
 * dialog with a multiline text area for editing long strings (e.g.
 * notes in a {@link Notable}
 */
public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {

	private String currentText;
	
	//TODO nice "editing" icon
	private final Icon icon = null;
	private final JLabel label = new JLabel(icon, SwingConstants.CENTER);

	private final JTextArea textArea;
	private final JScrollPane textScroll;
	private final String dialogTitle;
	private final JPanel dialogPanel;
	
	/**
	 * Create a default {@link TextAreaCellEditor}
	 * @param dialogTitle 	Title for editing dialog
	 */
	public TextAreaCellEditor(String dialogTitle) {
		textArea = new JTextArea(5, 40);
		textScroll = new JScrollPane(textArea);
		dialogPanel = new JPanel(new BorderLayout());
		dialogPanel.add(textScroll);
		dialogPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		this.dialogTitle = dialogTitle;
	}

	private void edit() {
		//New dialog each time
		OKCancelDialog dialog = new OKCancelDialog(dialogPanel, null, dialogTitle, "OK", "Cancel");
		
		//The user has clicked the cell, so
		//bring up the dialog.
		textArea.setText(currentText);
		dialog.setVisible(true);

		//This runs when the dialog is closed
		if (dialog.lastClickWasOK()) {
			currentText = textArea.getText();
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
		return currentText;
	}

	//Implement the one method defined by TableCellEditor.
	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		currentText = (String)value;
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
