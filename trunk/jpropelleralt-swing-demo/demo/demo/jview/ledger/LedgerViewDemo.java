package demo.jview.ledger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.jpropelleralt.jview.table.impl.LedgerTableModel;
import org.jpropelleralt.ledger.Ledger;
import org.jpropelleralt.ledger.list.impl.ListLedger;
import org.jpropelleralt.list.ListBox;
import org.jpropelleralt.list.impl.ListBoxDefault;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.Views;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import demo.examples.Person;

/**
 * Demonstrate view of {@link Ledger}
 */
public class LedgerViewDemo {

	/**
	 * Demonstrate
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp2();
			}
		});
	}
	
	private static void runApp2() {
		LoggerUtils.enableConsoleLogging();
		Views.initialiseLookAndFeel();
		
		JFrame frame = new JFrame("Node View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final ListBox<Person> listBox = ListBoxDefault.create();
		
		final Person alice = new Person("Alice", "First Person", 20, null);
		final Person bob = new Person("Bob", "Second Person", 21, null);
		alice.bff().set(bob);
		bob.bff().set(alice);

		listBox.add(alice);
		listBox.add(bob);
		
		RefDefault<ListBox<Person>> listBoxRef = RefDefault.create(listBox);
		
		ListLedger<Person> ledger = ListLedger.create(Person.class, listBoxRef);
		
		RefDefault<ListLedger<Person>> ledgerRef = RefDefault.create(ledger);
		
		LedgerTableModel tableModel = LedgerTableModel.create(ledgerRef);
		
		JTable table = new JTable(tableModel);
		
		JScrollPane scroll = new JScrollPane(table);
		table.setTableHeader(null);
		
		JButton addButton = new JButton(new AbstractAction("Add a person") {
			@Override
			public void actionPerformed(ActionEvent e) {
				listBox.add(new Person("New person", "desc.", (int)(Math.random() * 10) + 10, null));
			}
		});

		JButton changeButton = new JButton(new AbstractAction("Change a person") {
			@Override
			public void actionPerformed(ActionEvent e) {
				listBox.get(0).age().set((int)(Math.random() * 10) + 10);
			}
		});

		FormLayout layout = new FormLayout(
				"fill:pref:grow",
			//   table                 add         change
				"fill:pref:grow, 3dlu, pref, 3dlu, pref"
		);
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int y = 1;
		builder.add(scroll,			cc.xy(1, y));
		y+=2;
		builder.add(addButton,		cc.xy(1, y));
		y+=2;
		builder.add(changeButton,	cc.xy(1, y));
		y+=2;

		frame.getContentPane().add(builder.getPanel());
		frame.pack();
		frame.setVisible(true);
	}
	
}
