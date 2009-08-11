package demo.ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.ListSelectionSource;
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;
import org.jpropeller.view.list.impl.ListEditView;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.impl.BeanRowView;
import org.jpropeller.view.table.impl.TableRowViewDirect;

import test.example.contacts.FriendList;
import test.example.contacts.Person;

/**
 * Simple demonstration of {@link ListSelectionSource}
 * A source list is displayed at the top of a frame, which can have new sequentially named
 * {@link Person}s added to it. A target list is displayed at the bottom of the frame,
 * which can have {@link Person}s from the source list added to it.
 */
public class ListSelectionSourceDemo {
	
	/**
	 * Run the demo application
	 * @param args ignored
	 */
	public static void main(String[] args) {
		GeneralUtils.enableNimbus();
		GeneralUtils.enableConsoleLogging();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp();
			}
		});
	}

	/**
	 * Run the demo application
	 */
	private static void runApp() {
		
		JFrame frame = new JFrame("ListSelectionSourceDemo");
		
		FriendList sourceList = new FriendList();
		Person a = new Person();
		a.name().set("a");
		Person b = new Person();
		b.name().set("b");
		sourceList.friends().add(a);
		sourceList.friends().add(b);
	
		FriendList targetList = new FriendList();
		
		ListAndSelectionAndValueReferenceDefault<Person> sourceRef = 
			new ListAndSelectionAndValueReferenceDefault<Person>(sourceList.friends(), Person.class);

		ListAndSelectionAndValueReferenceDefault<Person> targetRef = 
			new ListAndSelectionAndValueReferenceDefault<Person>(targetList.friends(), Person.class);

		TableRowView<Bean> rowView = new BeanRowView(a);
		
		Source<Person> sourceSource = new Source<Person>() {
			int i = 0;
			@Override
			public Person get() throws NoInstanceAvailableException {
				Person n = new Person();
				n.name().set("New person number " + (i++));
				return n;
			}
		};
		
		TableRowView<Person> directRowView = new TableRowViewDirect<Person>(Person.class, "Person");
		
		Source<Person> targetSource = new ListSelectionSource<Person>(
				frame, "Select a person", 
				sourceList.friends(), 
				Person.class, 
				directRowView,
				false);
		
		ListEditView<Person> sourceView = ListEditView.create(sourceRef, Person.class, rowView, sourceSource, true);
		ListEditView<Person> targetView = ListEditView.create(targetRef, Person.class, rowView, targetSource, true);
		
		//Add the two views to the frame
		frame.setLayout(new GridLayout(2, 1));
		frame.add(sourceView.getComponent());
		frame.add(targetView.getComponent());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
}
