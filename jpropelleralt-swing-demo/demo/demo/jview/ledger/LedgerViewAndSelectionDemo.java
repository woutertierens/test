package demo.jview.ledger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jpropelleralt.jview.list.impl.ListButtonsView;
import org.jpropelleralt.jview.node.impl.NodeView;
import org.jpropelleralt.jview.ref.impl.NumberView;
import org.jpropelleralt.jview.table.impl.TableView;
import org.jpropelleralt.jview.tango.TangoIconFactory;
import org.jpropelleralt.ledger.Ledger;
import org.jpropelleralt.list.ListBox;
import org.jpropelleralt.list.impl.ListBoxDefault;
import org.jpropelleralt.list.impl.ListSelection;
import org.jpropelleralt.list.impl.ListSelectionIndex;
import org.jpropelleralt.plumbing.NoInstanceAvailableException;
import org.jpropelleralt.plumbing.Source;
import org.jpropelleralt.plumbing.Target;
import org.jpropelleralt.ref.Ref;
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
public class LedgerViewAndSelectionDemo {

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
		TangoIconFactory.useAsDefault();
		
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
		
		Ref<Integer> selectionIndex = ListSelectionIndex.create(listBoxRef);
		
		NumberView<Integer> selectionIndexView = NumberView.createInteger(selectionIndex);
		
		TableView tableView = TableView.create(Person.class, listBoxRef, selectionIndex, false);
		
		JScrollPane scroll = new JScrollPane(tableView.getComponent());

		Ref<Person> selection = ListSelection.create(listBoxRef, selectionIndex);
		
		NodeView selectionView = NodeView.create(selection);

		Source<Person> source = new Source<Person>() {
			@Override
			public Person get() throws NoInstanceAvailableException {
				return new Person();
			}
		};
		
		Target<Person> target = new Target<Person>() {
			@Override
			public void put(Person instance) {
				System.out.println("Deleted " + instance);
			}
		};
		
		ListButtonsView<Person> buttons = ListButtonsView.create(listBoxRef, selectionIndex, null, source, target);
		
		FormLayout layout = new FormLayout(
			//   list                  editor
				"fill:pref:grow, 5dlu, pref",
				
			//   table                 buttons
				"fill:pref:grow, 3dlu, pref"
		);
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.add(scroll,								cc.xy(1, 1));
		builder.add(selectionView.getComponent(),		cc.xy(3, 1));
		builder.add(buttons.getComponent(),				cc.xy(1, 3));
		builder.add(selectionIndexView.getComponent(),	cc.xy(3, 3));

		frame.getContentPane().add(builder.getPanel());
		frame.pack();
		frame.setVisible(true);
	}
	
}
