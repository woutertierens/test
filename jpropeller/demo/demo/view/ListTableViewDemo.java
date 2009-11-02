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

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.reference.impl.ReferenceToChangeable;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.table.impl.BeanRowView;
import org.jpropeller.view.table.impl.ListTableModel;
import org.jpropeller.view.table.impl.ListTableView;

import test.example.LotsOfProps;

/**
 * Simple demonstration of {@link ListTableModel}
 */
public class ListTableViewDemo {

	/**
	 * Run demonstration
	 * @param args
	 */
	public static void main(String[] args) {
		final CList<LotsOfProps> l = makeBeanList();
		
		final LotsOfProps example = new LotsOfProps();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				GeneralUtils.enableNimbus();
				
				JFrame frame = new JFrame("BeanListTableModel Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				//Make reference to the list
				final ReferenceToChangeable<CList<LotsOfProps>> reference = ReferenceToChangeable.createObservableListReference(LotsOfProps.class, l);
				
				//Make table model based on reference and row view displaying properties as columns
				ListTableView<LotsOfProps> view = new ListTableView<LotsOfProps>(reference, new BeanRowView<Bean>(example));
				
				//Default table using model
				JTable table = view.getComponent();
				JScrollPane scroll = new JScrollPane(table);
				
				JPanel buttons = new JPanel();
				
				JButton add = new JButton("Add");
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps p = new LotsOfProps();
						p.stringProp().set("New random p " + Math.random());
						reference.value().get().add(p);
					}
				});
				buttons.add(add);

				JButton changeOriginalList = new JButton("Change original list");
				changeOriginalList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						l.get(0).doubleProp().set(Math.random());
					}
				});
				buttons.add(changeOriginalList);
				
				JButton changeEntireList = new JButton("Change entire list");
				changeEntireList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						reference.value().set(makeBeanList());
					}
				});
				buttons.add(changeEntireList);

				JButton changeCurrentList = new JButton("Change current list");
				changeCurrentList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						reference.value().get().get(0).doubleProp().set(Math.random());
					}
				});
				buttons.add(changeCurrentList);


				frame.add(scroll, BorderLayout.CENTER);
				frame.add(buttons, BorderLayout.SOUTH);
				
				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}

	private static CList<LotsOfProps> makeBeanList() {
		final CList<LotsOfProps> l = new CListDefault<LotsOfProps>();
		for (int i = 0; i < 20; i++) {
			LotsOfProps p = new LotsOfProps();
			p.stringProp().set("Row " + i);
			l.add(p);
		}
		return l;
	}
	
}
