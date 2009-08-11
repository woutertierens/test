package org.jpropeller.view.table.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 *	Provides spreadsheet functionality onto a {@link JTable}.
 */
public class SpreadSheetAdapter {

	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	/**
	 * Registers several spreadsheet-esque actions on a table.
	 * @param table		The table to install the adapter onto.
	 */
	public static void install(final JTable table) {
		
		installDelete(table);
		
		installCopy(table);
		
		installPaste(table);
	}

	/**
	 * Registers a delete action on a table.
	 * @param table		The table to register the delete action on.
	 */
	public static void installDelete(final JTable table) {
		KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
		table.getInputMap().put(delete, "Delete");
		table.getActionMap().put("Delete", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = table.getSelectedRows().clone();
				int[] cols = table.getSelectedColumns().clone();
				for(int row : rows) {
					for(int col : cols) {
						table.setValueAt("", row, col);
					}
				}
			}
		});
	}

	/**
	 * Registers a paste action on a table, will read the data from the system clipboard.
	 * @param table		The table to register the paste action on.
	 */
	public static void installPaste(final JTable table) {
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		table.getInputMap().put(paste, "Paste");
		table.getActionMap().put("Paste", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String xfer = (String)(clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
					Scanner scanner = new Scanner(xfer);
					int row = table.getSelectedRows()[0];
					int colMin = table.getSelectedColumns()[0];
					while(scanner.hasNextLine()) {
						String line = scanner.nextLine();
						Scanner lineScan = new Scanner(line).useDelimiter("\t");
						int col = colMin;						
						while(lineScan.hasNext()) {
							String val = lineScan.next();
							table.setValueAt(val, row, col);
							++col;
							if(col >= table.getColumnCount()) {
								break;
							}
						}
						++row;
						if(row >= table.getRowCount()) {
							break;
						}
					}					
				} catch (UnsupportedFlavorException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	/**
	 * Registers a cioy action on a table, will write the selected cells to the system clipboard.
	 * @param table		The table to register the copy action on.
	 */
	public static void installCopy(final JTable table) {
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		table.getInputMap().put(copy, "Copy");
		table.getActionMap().put("Copy", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuffer buffer = new StringBuffer();
				for(int row : table.getSelectedRows()) {
					for(int col : table.getSelectedColumns()) {
						buffer.append(table.getValueAt(row, col));
						if(col < table.getColumnModel().getSelectionModel().getMaxSelectionIndex()) {
							buffer.append("\t");
						}
					}
					buffer.append("\n");
				}
				StringSelection selection = new StringSelection(buffer.toString());
				clipboard.setContents(selection, selection);
			}
		});
	}
	
}
