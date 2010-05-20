package demo.table;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.table.columns.ColumnInfo;
import org.jpropeller.view.table.columns.ColumnLayout;
import org.jpropeller.view.table.columns.impl.ColumnInfoDefault;
import org.jpropeller.view.table.columns.impl.ColumnUpdater;

/**
 * Look at behaviour of {@link ColumnLayout}
 */
public class ColumnLayoutDemo {

	/**
	 * Run Demo
	 * @param args		Ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runDemo();
			}
		});
	}

	private static void runDemo() {
		
		final DefaultTableModel model = new DefaultTableModel(3, 3);
		
		final DefaultTableColumnModel cm = new DefaultTableColumnModel();
		final JTable table = new JTableImproved(model, cm);
		JScrollPane scroll = new JScrollPane(table);

		new ColumnUpdater(model, cm, new ColumnLayoutSizing());
		
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double a = table.getColumnModel().getColumn(0).getWidth();
				double b = ((double)table.getColumnModel().getColumn(1).getWidth())/a;
				double c = ((double)table.getColumnModel().getColumn(2).getWidth())/a;
				System.out.println(a);
				System.out.println(b);
				System.out.println(c);
			}
		});
		
		JFrame frame = new JFrame("Table demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scroll);
		frame.add(print, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
//		table.getColumnModel().getColumn(0).setPreferredWidth(1000);
//		table.getColumnModel().getColumn(1).setPreferredWidth(2000);
//		table.getColumnModel().getColumn(2).setPreferredWidth(3000);
		
		model.addColumn("BOB");

	}
	
	private static class ColumnLayoutSizing implements ColumnLayout {

		@Override
		public Iterable<ColumnInfo> layout(TableModel model) {
			int columnCount = model.getColumnCount();
			LinkedList<ColumnInfo> list = new LinkedList<ColumnInfo>();
			
			list.add(new ColumnInfoDefault(0, 10, 10, 10));
			list.add(new ColumnInfoDefault(1, 30, 30, 30));

			for (int i = 2; i < columnCount; i++) {
				list.add(new ColumnInfoDefault(i, (i+1) * 100, 0, 1000));
			}
			
			return list;
		}

	}
	
}
