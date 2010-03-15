package demo.pathref;

import java.util.Map;

import org.jpropelleralt.box.Box;
import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.change.Change;
import org.jpropelleralt.path.Path;
import org.jpropelleralt.path.impl.PathBuilder;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.PathRef;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.View;

import demo.examples.Person;

/**
 * Demonstrate {@link PathRef}
 */
public class PathRefDemo {

	/**
	 * Demonstrate {@link RefDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");

		LoggerUtils.enableConsoleLogging(RefDefault.class, UniverseDefault.class);

		Person bob = new Person();
		bob.name().set("bob_name_string");
		bob.age().set(27);
		
		Person alice = new Person();
		alice.name().set("alice_name_string");
		alice.age().set(25);
		alice.bff().set(bob);
		
		Ref<Person> model = Boxes.create(alice);
		
		//Make a path to the model Person's best friend's name
		Path<String> path = PathBuilder.create(model).via("bff").to(String.class, "name");

		//Make a Ref that will always contain the model's best friend's name
		final Ref<String> pathRef = PathRef.create(path);
		
		System.out.println("pathRef = " + pathRef.get());

		//Print changes to pathRef
		pathRef.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println(changes.get(pathRef) + ", pathRef = " + pathRef.get());
			}
		});
		
		//We should see a change when we change bob's name
		System.out.println("Setting bob's new name");
		bob.name().set("bob's new name");
		System.out.println("pathRef = " + pathRef.get());

		//Delay to make sure we see individual changes
		sleep();
		
		//We should NOT see a change when we change bob's age
		System.out.println("Setting bob's new age");
		bob.age().set(54);
		System.out.println("pathRef = " + pathRef.get());

		//Delay to make sure we see individual changes
		sleep();
		
		//FIXME we should see a change here, and end up pointing to null
		
		//We should NOT see a change when we change bob's age
		System.out.println("Setting model to bob");
		model.set(bob);
		System.out.println("pathRef = " + pathRef.get());

	}

	private static void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
