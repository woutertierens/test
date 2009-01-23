package demo.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jpropeller.ui.NumberCellEditor;
import org.jpropeller.ui.SelectingTextCellEditor;

/**
 * Simple demo of custom cell editors in a table
 * @author shingoki
 *
 */
public class TableCellEditorsDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp();
			}
		});
	}

	private static void runApp() {
		DefaultTableModel tm = new DemoTableModel(5,5);
		tm.setValueAt(new Double(10.12), 0, 0);
		tm.setValueAt(new Long(100), 0, 1);
		tm.setValueAt(new Float(10.1), 0, 2);
		tm.setValueAt(new Integer(10), 0, 3);
		tm.setValueAt("Test", 0, 4);
		tm.setColumnIdentifiers(new String[]{"Double", "Long", "Float", "Integer", "String"});
		
		JTable table = new JTable(tm);
		
		table.setDefaultEditor(Double.class, 	NumberCellEditor.doubleEditor());
		table.setDefaultEditor(Float.class, 	NumberCellEditor.floatEditor());

		table.setDefaultEditor(Integer.class,	NumberCellEditor.integerEditor());
		table.setDefaultEditor(Long.class, 		NumberCellEditor.longEditor());
		
		table.setDefaultEditor(String.class, 	SelectingTextCellEditor.editor());

		JScrollPane scroll = new JScrollPane(table);
		
		JFrame frame = new JFrame("Table Cell Editors Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scroll);
		frame.pack();
		frame.setVisible(true);		
	}

	private static class DemoTableModel extends DefaultTableModel {

		public DemoTableModel() {
			super();
		}

		public DemoTableModel(int rowCount, int columnCount) {
			super(rowCount, columnCount);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return Double.class;
			} else if (columnIndex == 1) {
				return Long.class;
			} else if (columnIndex == 2) {
				return Float.class;
			} else if (columnIndex == 3) {
				return Integer.class;
			} else if (columnIndex == 4) {
				return String.class;
			} else {
				return Object.class;
			}
		}
		
	}
	
	
}
