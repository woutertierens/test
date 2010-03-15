package demo.path;

import java.util.LinkedList;
import java.util.List;

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
 * Demonstrate {@link PathBuilder} using String names
 * for the path steps.
 */
public class PathNameBuilderDemo {

	/**
	 * Demonstrate {@link RefDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");

		LoggerUtils.enableConsoleLogging(RefDefault.class, UniverseDefault.class);

		List<Person> chain = new LinkedList<Person>();
		for (int i = 0; i < 10; i++) {
			Person p = new Person();
			p.name().set("Person " + i);
			p.age().set(20 + i);
			chain.add(p);
			if (i > 0) {
				p.bff().set(chain.get(i-1));
			}
		}
		
		Person p9 = chain.get(9);
		
		Ref<Person> model = Boxes.create(p9);
		
		Path<String> path = PathBuilder.create(model).via("bff", "bff", "bff").to(String.class, "name");

		printPath(path);

		path = PathBuilder.create(model).via("bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff").to(String.class, "name");

		printPath(path);

		path = PathBuilder.create(model).via("bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff").to(String.class, "name");

		printPath(path);

		path = PathBuilder.create(model).via("bff", "bff", "nonexistent", "bff", "bff").to(String.class, "name");

		printPath(path);

		Path<Integer> integerPath = PathBuilder.create(model).via("bff", "bff", "bff", "bff").to(Integer.class, "name");

		printPath(integerPath);

	}

	private static void printPath(Path<?> path) {
		PathIterator<?> iterator = path.iterator();
		while (iterator.hasNext()) {
			System.out.println("via " + iterator.next());
		}
		System.out.println("to " + iterator.destination());
		System.out.println();
	}

}
