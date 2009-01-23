package demo.list;

import java.util.logging.Logger;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.properties.list.impl.ListAndSelectionReferenceDefault;
import org.jpropeller.properties.list.impl.ListSelection;
import org.jpropeller.properties.list.impl.ListSelectionProp;
import org.jpropeller.properties.list.impl.ListSelectionReferenceProp;
import org.jpropeller.util.GeneralUtils;

/**
 * Simple demo of {@link ListSelection}
 * @author shingoki
 */
public class ListAndSelectionReferenceDemo {

	private final static Logger logger = GeneralUtils.logger(ListAndSelectionReferenceDemo.class);
	private ListAndSelectionReferenceDefault<String> ref;
	/**
	 * Create a {@link ListAndSelectionReferenceDemo}
	 */
	public ListAndSelectionReferenceDemo() {

		GeneralUtils.enableConsoleLogging(
				ListSelectionProp.class,
				ListSelectionReferenceProp.class,
				ListAndSelectionReferenceDemo.class 
				);
		
		ObservableList<String> list = new ObservableListDefault<String>();

		for (int i = 0; i < 10; i++) {
			list.add(Integer.toString(i));
		}
		
		ref = new ListAndSelectionReferenceDefault<String>(list, String.class);
		
		ref.selection().set(5);
		
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

		ObservableListDefault<String> newList = new ObservableListDefault<String>();
		newList.add("Start");
		ref.value().set(newList);
		showSelection();

		for (int i = 0; i < 10; i++) {
			newList.add("n" + Integer.toString(i));
		}
		showSelection();

		ref.selection().set(3);
		showSelection();
		
		newList.add(0, "bobAgain");
		showSelection();
		
		list.add(0, "bobAgainAgain");
		showSelection();
		
		ref.value().set(list);
		showSelection();
	}
	
	/**
	 * Run demo
	 * @param args
	 * 		Ignored
	 */
	public static void main(String[] args) {
		new ListAndSelectionReferenceDemo();
	}

	private void showSelection() {
		StringBuilder s = new StringBuilder();
		int sel = ref.selection().get();
		ObservableList<String> list = ref.value().get();
		s.append(sel + "\t ");
		for (int i = 0; i < list.size(); i++) {
			if (i == sel) s.append("[");
			s.append(list.get(i));
			if (i == sel) s.append("]");
			s.append(" ");
		}
		s.append("\n");
		
		logger.finest(s.toString());
		
	}
	
}
