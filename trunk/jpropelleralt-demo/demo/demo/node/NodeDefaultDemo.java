package demo.node;

import java.util.Map;

import org.jpropelleralt.box.Box;
import org.jpropelleralt.change.Change;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.View;

import demo.examples.Person;

/**
 * Demonstrate {@link RefDefault}
 */
public class NodeDefaultDemo {
	
	/**
	 * Demonstrate {@link RefDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");

		LoggerUtils.enableConsoleLogging(RefDefault.class, UniverseDefault.class);
		
		final Person p = new Person();
		System.out.println(p);

		p.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println("Change to p - " + p);
			}
		});
		
		p.name().set("Cate");
		System.out.println(p);
		
		for (String name : p) {
			System.out.println(name + " is a " + p.get(name).valueClass() + " = " + p.get(name).get());
		}
	}

}
