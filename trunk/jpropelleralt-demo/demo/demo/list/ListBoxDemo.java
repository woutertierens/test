package demo.list;

import java.util.Map;

import org.jpropelleralt.box.Box;
import org.jpropelleralt.change.Change;
import org.jpropelleralt.list.ListBox;
import org.jpropelleralt.list.impl.ListBoxDefault;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.View;

import demo.examples.Person;

/**
 * Demonstrate {@link ListBoxDefault}
 */
public class ListBoxDemo {
	
	/**
	 * Demonstrate {@link ListBoxDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");

		LoggerUtils.enableConsoleLogging(RefDefault.class, UniverseDefault.class);
		
		ListBox<Person> list = ListBoxDefault.<Person>create();
		
		final Person p = new Person();

		p.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println("Change to p - " + p);
			}
		});

		list.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println("Change to list");
			}
		});

		System.out.println("About to add person to list");
		list.add(p);

		sleep();
		
		System.out.println("About to change person name");
		p.name().set("Cate");

		sleep();

		System.out.println("About to remove person from list");
		list.remove(p);

		sleep();

		System.out.println("About to change person name");
		p.name().set("Doug");

	}

	private static void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
	}

}
