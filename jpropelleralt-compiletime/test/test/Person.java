package test;

import java.awt.Color;

import org.jpropelleralt.node.Described;
import org.jpropelleralt.node.Named;
import org.jpropelleralt.node.impl.NodeDefault;
import org.jpropelleralt.path.PathStep;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;

/**
 * A {@link Person}
 */
public class Person extends NodeDefault implements Named, Described {
	
	private final Ref<String> name;
	private final Ref<String> description;
	
	private final Ref<Integer> age;
	
	private final Ref<Person> bff;
	
	private final Ref<Boolean> married;
	
	private final Ref<Color> favouriteColour;

	/**
	 * Make a default person
	 */
	public Person() {
		this("Default Name", "Default Description", 20, null);
	}

	/**
	 * Make a person
	 * 
	 * @param name			The name
	 * @param description	The description
	 * @param age			The age in years
	 * @param bff			The {@link Person}'s best friend
	 */
	public Person(String name, String description, int age, Person bff) {
		super();
		this.name = addRef("name", RefDefault.create(name));
		this.description = addRef("description", RefDefault.create(description));
		this.age = addRef("age", RefDefault.create(age));
		this.bff = addRef("bff", RefDefault.create(Person.class, bff));
		this.married = addRef("married", RefDefault.create(false));
		this.favouriteColour = addRef("favouriteColour", RefDefault.create(Color.blue));
	}

	
	
	public Ref<String> name() {
		return name;
	}

	public Ref<String> description() {
		return description;
	}

	/**
	 * {@link Person}'s age in whole years
	 * @return age
	 */
	public Ref<Integer> age() {
		return age;
	}

	/**
	 * {@link Person}'s best friend forever
	 * @return bff
	 */
	public Ref<Person> bff() {
		return bff;
	}

	/**
	 * Whether {@link Person} is married
	 * @return married
	 */
	public Ref<Boolean> married() {
		return married;
	}

	/**
	 * The {@link Person}s favourite {@link Color}
	 * @return Favourite Colour
	 */
	public Ref<Color> favouriteColour() {
		return favouriteColour;
	}

	@Override
	public String toString() {
		return "Person, name = " + name().get() + ", age = " + age().get();
	}

	/**
	 * {@link PathStep} to {@link #name()}
	 */
	public final static PathStep<Person, String> toName = new PathStep<Person, String>() {
		public Ref<String> transform(Person s) {
			return s.name();
		};
	};

	/**
	 * {@link PathStep} to {@link #age()}
	 */
	public final static PathStep<Person, Integer> toAge = new PathStep<Person, Integer>() {
		public Ref<Integer> transform(Person s) {
			return s.age();
		};
	};

	/**
	 * {@link PathStep} to {@link #bff()}
	 */
	public final static PathStep<Person, Person> toBFF = new PathStep<Person, Person>() {
		public Ref<Person> transform(Person s) {
			return s.bff();
		};
	};

}
