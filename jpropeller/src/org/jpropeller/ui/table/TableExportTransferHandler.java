package org.jpropeller.ui.table;


import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;


/**
 * KeyAdapter to detect Windows standard cut, copy and paste keystrokes on a
 * JTable and put them to the clipboard in Excel friendly plain text format.
 * Assumes that null represents an empty column for cut operations. Replaces
 * line breaks and tabs in copied cells to spaces in the clipboard.
 * 
 * @see java.awt.event.KeyAdapter
 * @see javax.swing.JTable
 */
public class TableExportTransferHandler extends TransferHandler {

	private static final String LINE_BREAK = "\n";
	private static final String CELL_BREAK = "\t";

	private final JTable table;
	private final TableExporter exporter;

	@Override
	public int getSourceActions(JComponent c) {
	    return COPY;
	}

	@Override
	public Transferable createTransferable(JComponent c) {
	    if (c != table) return null;
	    return toSelection(false);
	}

	@Override
	public void exportDone(JComponent c, Transferable t, int action) {
	}
	
	/**
	 * Create a {@link TableExportTransferHandler}
	 * 
	 * @param table		The table handled by this adapter.
	 * @param exporter	The {@link TableExporter} used to export cells to strings
	 */
	private TableExportTransferHandler(JTable table, TableExporter exporter) {
		this.table = table;
		this.exporter = exporter;
	}

	/**
	 * Create a {@link TableExportTransferHandler} for a given table, and attach it to
	 * that table.
	 * 
	 * @param table		The table handled by this adapter
	 * @param exporter	The {@link TableExporter} used to export cells to strings
	 * @return A new {@link TableExportTransferHandler}, which is already set as the transfter handler on that table.
	 */
	public static TableExportTransferHandler createAndAttach(JTable table, TableExporter exporter) {
		TableExportTransferHandler a = new TableExportTransferHandler(table, exporter);
		table.setTransferHandler(a);
		return a;
	}
	
	private String escape(Object cell) {
		if (cell==null) {
			return "";
		} else {
			return cell.toString().replace(LINE_BREAK, " ").replace(CELL_BREAK, " ");
		}
	}

	private StringSelection toSelection(boolean isCut) {
		int[] rowsSelected = table.getSelectedRows();
		int[] colsSelected = table.getSelectedColumns();
		if (!table.getColumnSelectionAllowed()) {
			int numCols = table.getColumnCount();
			colsSelected = new int[numCols];
			for (int i = 0; i < numCols; i++) {
				colsSelected[i] = i;
			}
		}

		StringBuffer excelStr = new StringBuffer();
		for (int i = 0; i < rowsSelected.length; i++) {
			int r = rowsSelected[i];
			for (int j = 0; j < colsSelected.length; j++) {
				int c = colsSelected[j];
				Object v = table.getValueAt(r,c);
				ValueExporter vExporter = exporter.exporterFor(table.getColumnClass(c));
				for (int subColumn = 0; subColumn < vExporter.numColumns(); subColumn++) {
					String s = (v == null ? "" : escape(vExporter.transformer(subColumn).transform(v)));
					excelStr.append(s);
					if (subColumn < vExporter.numColumns() - 1) {
						excelStr.append(CELL_BREAK);
					}
				}
				if (isCut) {
					table.setValueAt(null, r, c);
				}
				if (j < colsSelected.length - 1) {
					excelStr.append(CELL_BREAK);
				}
			}
			excelStr.append(LINE_BREAK);
		}

		return new StringSelection(excelStr.toString());
	}

//	private void pasteFromClipboard() {
//		int startRow = table.getSelectedRows()[0];
//		int startCol = table.getSelectedColumns()[0];
//
//		String pasteString = "";
//		try {
//			pasteString = (String) (CLIPBOARD.getContents(this)
//					.getTransferData(DataFlavor.stringFlavor));
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(null, "Invalid Paste Type",
//					"Invalid Paste Type", JOptionPane.ERROR_MESSAGE);
//			return;
//		}
//
//		String[] lines = pasteString.split(LINE_BREAK);
//		for (int i = 0; i < lines.length; i++) {
//			String[] cells = lines[i].split(CELL_BREAK);
//			for (int j = 0; j < cells.length; j++) {
//				if (table.getRowCount() > startRow + i
//						&& table.getColumnCount() > startCol + j) {
//					table.setValueAt(cells[j], startRow + i, startCol + j);
//				}
//			}
//		}
//	}

}