package demo.ref;

import java.util.Map;

import org.jpropelleralt.box.Box;
import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.change.Change;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.View;

/**
 * Demonstrate {@link RefDefault}
 */
public class RefDefaultDemo {

	/**
	 * Demonstrate {@link RefDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");

		LoggerUtils.enableConsoleLogging(RefDefault.class, UniverseDefault.class);
		
		final Ref<String> s = Boxes.create("alice");
		final Ref<Ref<String>> container = Boxes.create(s);
		
		s.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println("S changed to " + s.get());
			}
		});

		container.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println("Container changed to " + container.get());
			}
		});

		System.out.println("About to change value of s - note that this will cause both s AND container Views to see changes");
		s.set("bob");

	}

}
