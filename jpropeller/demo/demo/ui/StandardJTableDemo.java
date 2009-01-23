package demo.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Demonstrate operation of standard {@link JTable}
 * @author shingoki
 */
public class StandardJTableDemo {

	/**
	 * Run the demo
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp();
			}
		});
	}

	/**
	 * Run the application
	 */
	public static void runApp() {
		final DefaultTableModel model = new DefaultTableModel(10, 3);
		final JTable table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);

		JPanel buttons = new JPanel();

		JButton addStart = new JButton("Add Start");
		addStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.insertRow(0, new String[]{"a", "b", "c"});
			}
		});
		buttons.add(addStart);

		JButton removeStart = new JButton("Remove Start");
		removeStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.removeRow(0);
			}
		});
		buttons.add(removeStart);

		JFrame frame = new JFrame();

		frame.add(scroll, BorderLayout.CENTER);
		frame.add(buttons, BorderLayout.SOUTH);
		frame.pack();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
}
