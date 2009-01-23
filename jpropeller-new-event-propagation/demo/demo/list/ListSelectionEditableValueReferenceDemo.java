package demo.list;

import java.util.logging.Logger;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.properties.list.impl.ListSelectionEditableValueReferenceDefault;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.PrintingChangeListener;

/**
 * Simple demo of {@link ListSelectionEditableValueReferenceDefault}
 * @author shingoki
 */
public class ListSelectionEditableValueReferenceDemo {

	private final static Logger logger = GeneralUtils.logger(ListSelectionEditableValueReferenceDemo.class);
	private ListSelectionEditableValueReferenceDefault<String> ref;
	/**
	 * Create a {@link ListSelectionEditableValueReferenceDemo}
	 */
	public ListSelectionEditableValueReferenceDemo() {

		GeneralUtils.enableConsoleLogging(
				//ListSelectionProp.class,
				//ListSelectionReferenceProp.class,
				ListSelectionEditableValueReferenceDemo.class 
			);
		
		ObservableList<String> list = new ObservableListDefault<String>();

		for (int i = 0; i < 10; i++) {
			list.add(Integer.toString(i));
		}
		
		ref = new ListSelectionEditableValueReferenceDefault<String>(list, String.class);
		
		ref.selectedValue().features().addListener(new PrintingChangeListener());
		
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
		
		ref.selectedValue().set("replacementForBobAgainAgain");
		showSelection();
	}
	
	/**
	 * Run demo
	 * @param args
	 * 		Ignored
	 */
	public static void main(String[] args) {
		new ListSelectionEditableValueReferenceDemo();
	}

	private void showSelection() {
		StringBuilder s = new StringBuilder();
		int sel = ref.selection().get();
		String selVal = ref.selectedValue().get();
		ObservableList<String> list = ref.value().get();
		s.append("index " + sel  + " = " + selVal + "\t ");
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
