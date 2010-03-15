package test;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.path.Path;
import org.jpropelleralt.path.PathIterator;
import org.jpropelleralt.path.impl.PathBuilder;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.transformer.Transformer;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link PathBuilder}
 */
public class PathBuilderTest {

	private List<Person> chain;
	private Person p9;
	private Ref<Person> model;

	/**
	 * JUnit setup
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		chain = new LinkedList<Person>();
		for (int i = 0; i < 10; i++) {
			Person p = new Person();
			p.name().set("Person " + i);
			p.age().set(20 + i);
			chain.add(p);
			if (i > 0) {
				p.bff().set(chain.get(i-1));
			}
		}
		
		p9 = chain.get(9);
		
		model = Boxes.create(p9);
	}

	/**
	 * Test paths using mix of names and {@link Transformer}s
	 * @throws Exception
	 */
	public void testMixedPath() throws Exception {
		//Partial path, just 3 steps, then to name
		Path<String> path = PathBuilder.create(model).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).to(String.class, "name");

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				
				chain.get(6).name());
		
		//Follow a path through the whole chain - hence 9 steps for the 9 Persons, then to the name of the last person, "Person 0"
		//Use transformers then names.
		path = PathBuilder.create(model).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via("bff", "bff", "bff", "bff", "bff", "bff").to(String.class, "name");

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				chain.get(6).bff(), 
				chain.get(5).bff(), 
				chain.get(4).bff(), 
				chain.get(3).bff(), 
				chain.get(2).bff(), 
				chain.get(1).bff(), 
				
				chain.get(0).name());
	}
	
	/**
	 * Test paths via {@link Transformer}s
	 * @throws Exception
	 */
	@Test
	public void testTransformerPath() throws Exception {
		
		//Partial path, just 3 steps, then to name
		Path<String> path = PathBuilder.create(model).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).to(Person.toName);

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				
				chain.get(6).name());

		//Follow a path through the whole chain - hence 9 steps for the 9 Persons, then to the name of the last person, "Person 0"
		path = PathBuilder.create(model).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).to(Person.toName);

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				chain.get(6).bff(), 
				chain.get(5).bff(), 
				chain.get(4).bff(), 
				chain.get(3).bff(), 
				chain.get(2).bff(), 
				chain.get(1).bff(), 
				
				chain.get(0).name());

		//Now we add another "bff" step - this can't work since the last person doesn't HAVE a bff
		path = PathBuilder.create(model).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).via(Person.toBFF).to(Person.toName);

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				chain.get(6).bff(), 
				chain.get(5).bff(), 
				chain.get(4).bff(), 
				chain.get(3).bff(), 
				chain.get(2).bff(), 
				chain.get(1).bff(),
				chain.get(0).bff(),					//At this point we are trying to get person 0's BFF, but he doesn't have one (last in the chain), so we hit a null
				null);					//The destination is hence also null, since we try to get a null person's name

	}
	
	/**
	 * Test paths via string names
	 * @throws Exception
	 */
	@Test
	public void testNamePath() throws Exception {
		
		//Partial path, just 3 steps, then to name
		Path<String> path = PathBuilder.create(model).via("bff", "bff", "bff").to(String.class, "name");

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				
				chain.get(6).name());

		//Follow a path through the whole chain - hence 9 steps for the 9 Persons, then to the name of the last person, "Person 0"
		path = PathBuilder.create(model).via("bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff").to(String.class, "name");

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				chain.get(6).bff(), 
				chain.get(5).bff(), 
				chain.get(4).bff(), 
				chain.get(3).bff(), 
				chain.get(2).bff(), 
				chain.get(1).bff(), 
				
				chain.get(0).name());

		//Now we add another "bff" step - this can't work since the last person doesn't HAVE a bff
		path = PathBuilder.create(model).via("bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff", "bff").to(String.class, "name");

		assertPath(path, 
				model, 
				chain.get(9).bff(), 
				chain.get(8).bff(), 
				chain.get(7).bff(), 
				chain.get(6).bff(), 
				chain.get(5).bff(), 
				chain.get(4).bff(), 
				chain.get(3).bff(), 
				chain.get(2).bff(), 
				chain.get(1).bff(),
				null,					//At this point we are trying to get person 0's BFF, but he doesn't have one (last in the chain), so we hit a null
										//This is interesting since the result is different to the corresponding section of the testTransformerPath() method.
										//When transformers are used, this step of the path is a Ref<Person> of value null, but when we use names we just
										//get null. This is because when using a name, we can't tell whether a Ref<Person> with value null is a Ref<Node>,
										//so we just get a null instead. This is the correct and valid behaviour, and in practical terms it doesn't 
										//actually make any difference - it just needs to be allowed for in the expected results of this test.
				null);					//The destination is hence also null, since we try to get a null person's name

		path = PathBuilder.create(model).via("bff", "bff", "nonexistent", "bff", "bff").to(String.class, "name");

		assertPath(path, 
				model, 					//root
				chain.get(9).bff(), 	//first step via bff
				chain.get(8).bff(), 	//second step via bff
				null,					//At this point we are trying to get a Ref called "nonexistent" - this fails and so we get a null
				null);					//The destination is hence also null, since we try to get a null person's name

		Path<Integer> integerPath = PathBuilder.create(model).via("bff", "bff", "bff", "bff").to(Integer.class, "name");

		assertPath(integerPath, 
				model,					//root 
				chain.get(9).bff(), 	//bff step
				chain.get(8).bff(), 	//bff step
				chain.get(7).bff(), 	//bff step
				chain.get(6).bff(), 	//bff step (our last one - there are 4)
				null); 					//Now we try to make the final step using to(), but we are expecting an Integer and get a String, so result is null to preserve type safety.

	}

	private static void assertPath(Path<?> path, Object... steps) {
		PathIterator<?> iterator = path.iterator();
		
		//Check iterated steps are the same
		int step = 0;
		while (iterator.hasNext()) {
			Assert.assertSame(steps[step], iterator.next());
			step++;
		}
		
		//Check destination is the same
		Assert.assertSame(steps[step], iterator.destination());
		
		//Check we reached the end of expected steps
		Assert.assertEquals(steps.length-1, step);
	}
	
}
