package demo.path;

import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.path.Path;
import org.jpropelleralt.path.PathIterator;
import org.jpropelleralt.path.impl.PathBuilder;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;

import demo.examples.Person;

/**
 * Demonstrate {@link PathBuilder}
 */
public class PathBuilderDemo {

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
		Path<String> path = PathBuilder.create(model).via(Person.toBFF).to(Person.toName);

		//Iterate the path - shows that the path goes via first the current model ref (Ref containing alice),
		//then to alice's BFF ref (Ref containing bob) then to bob's name ref.
		printPath(path);
		
		//Now change the model - lets make it point to bob instead
		model.set(bob);
		
		//The path will change too - now since bob has no BFF, the path goes via
		//the current model ref (Ref now containing bob), to bob's BFF ref (Ref containing null), then
		//to null, since the path terminates at this point. If we had any additional stages, they would
		//all return null - essentially when using a Path we can give up when a null is returned.
		printPath(path);
		
	}

	private static void printPath(Path<String> path) {
		PathIterator<String> iterator = path.iterator();
		while (iterator.hasNext()) {
			System.out.println("via " + iterator.next());
		}
		System.out.println("to " + iterator.destination());
	}

}
