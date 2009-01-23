package demo.list;

import java.util.List;
import java.util.Map;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.util.PropUtils;

import demo.bean.TestStringBean;


/**
 * Test operation of {@link ObservableListDefault}
 * @author shingoki
 *
 */
public class ObservableListDefaultDemo {

	/**
	 * Demonstrate {@link ObservableListDefault}
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		final ObservableList<Object> list = new ObservableListDefault<Object>();
		
		list.features().addListener(new ChangeListener() {
		
			@Override
			public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
				System.out.println(PropUtils.describeChanges(initial, changes));
			}
		});
		
		System.out.println("list.add(\"a\")");
		list.add("a");

		System.out.println("list.add(0, \"b\")");
		list.add(0, "b");
		
		TestStringBean bean = new TestStringBean();
		System.out.println("list.add(bean)");
		list.add(bean);
	
		System.out.println("bean.name().set(\"bean name\")");
		bean.name().set("bean name");
		
		System.out.println("list.add(bean)");
		list.add(bean);
		
		System.out.println("bean.name().set(\"bean name2\")");
		bean.name().set("bean name2");
		
		System.out.println("list.remove(bean)");
		list.remove(bean);
		
		System.out.println("bean.name().set(\"bean name3\")");
		bean.name().set("bean name3");
		
		System.out.println("list.remove(bean)");
		list.remove(bean);
		
		System.out.println("bean.name().set(\"bean name4\")");
		bean.name().set("bean name4");

	}

}
