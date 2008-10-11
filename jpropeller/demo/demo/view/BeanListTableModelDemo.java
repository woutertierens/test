package demo.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.table.impl.ListRowsTableModel;
import org.jpropeller.view.table.impl.BeanRowView;

import test.example.LotsOfProps;

/**
 * Simple demonstration of {@link ListRowsTableModel}
 * @author bwebster
 */
public class BeanListTableModelDemo {

	/**
	 * Run demonstration
	 * @param args
	 */
	public static void main(String[] args) {
		final ObservableList<LotsOfProps> l = new ObservableListDefault<LotsOfProps>();
		for (int i = 0; i < 15; i++) {
			LotsOfProps p = new LotsOfProps();
			p.stringProp().set("Row " + i);
			l.add(p);
		}
		
		final LotsOfProps example = new LotsOfProps();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				GeneralUtils.enableNimbus();
				
				JFrame frame = new JFrame("BeanListTableModel Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				TableModel model = new ListRowsTableModel<LotsOfProps>(l, new BeanRowView(example));
				JTable table = new JTable(model);
				JScrollPane scroll = new JScrollPane(table);
				
				JPanel buttons = new JPanel();
				
				JButton add = new JButton("Add");
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps p = new LotsOfProps();
						p.stringProp().set("New random p " + Math.random());
						l.add(p);
					}
				});
				buttons.add(add);

				JButton change = new JButton("Change");
				change.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						l.get(0).doubleProp().set(Math.random());
					}
				});
				buttons.add(change);

				frame.add(scroll, BorderLayout.CENTER);
				frame.add(buttons, BorderLayout.SOUTH);
				
				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}
	
}
