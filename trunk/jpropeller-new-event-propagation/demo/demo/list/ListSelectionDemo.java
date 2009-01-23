package demo.list;

import java.util.logging.Logger;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.properties.list.impl.ListSelection;
import org.jpropeller.properties.list.impl.ListSelectionProp;
import org.jpropeller.util.GeneralUtils;

/**
 * Simple demo of {@link ListSelection}
 * @author shingoki
 */
public class ListSelectionDemo {

	private final static Logger logger = GeneralUtils.logger(ListSelectionDemo.class);
	private ObservableList<String> list;
	private ListSelection selection;

	/**
	 * Create a {@link ListSelectionDemo}
	 */
	public ListSelectionDemo() {

		GeneralUtils.enableConsoleLogging(ListSelectionProp.class, ListSelectionDemo.class);
		
		list = new ObservableListDefault<String>();

		for (int i = 0; i < 10; i++) {
			list.add(Integer.toString(i));
		}
		
		selection = new ListSelection(list);
		
		selection.selection().set(5);
		
		showSelection();

		list.set(0, "bob");
		showSelection();
		
		list.add(0, "alice");
		showSelection();
		
		list.remove(5);
		showSelection();

		list.remove(1);
		showSelection();

		list.remove(5);
		showSelection();
		
		list.remove(6);
		showSelection();

		list.remove(4);
		showSelection();

		list.remove(4);
		showSelection();

		list.remove(4);
		showSelection();

		
	}
	
	/**
	 * Run demo
	 * @param args
	 * 		Ignored
	 */
	public static void main(String[] args) {
		new ListSelectionDemo();
	}

	private void showSelection() {
		StringBuilder s = new StringBuilder();
		s.append(selection.selection().get() + "\t ");
		for (int i = 0; i < list.size(); i++) {
			if (i == selection.selection().get()) s.append("[");
			s.append(list.get(i));
			if (i == selection.selection().get()) s.append("]");
			s.append(" ");
		}
		s.append("\n");
		
		logger.finest(s.toString());
		
	}
	
}
