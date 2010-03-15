package demo.list;

import org.jpropelleralt.list.ListBox;
import org.jpropelleralt.list.impl.ListBoxDefault;
import org.jpropelleralt.list.impl.ListSelectionIndex;
import org.jpropelleralt.multiverse.Multiverse;
import org.jpropelleralt.plumbing.NoInstanceAvailableException;
import org.jpropelleralt.plumbing.Source;
import org.jpropelleralt.reaction.impl.ReactionDefault;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;

import demo.examples.Person;

/**
 * Demonstrate {@link ListBoxDefault}
 */
public class ListSelectionIndexDemo {
	
	/**
	 * Demonstrate {@link ListBoxDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {

		ListBox<Person> list = ListBoxDefault.<Person>create();

		for (int i = 0; i < 10; i++) {
			list.add(new Person("Person " + i, "Description " + i, i, null));
		}

		final RefDefault<ListBox<Person>> listRef = RefDefault.create(list);
		
		final Ref<Integer> index = ListSelectionIndex.create(listRef);
		
		System.out.println("Setting index 4");
		index.set(4);
		
		Ref<Person> selectedPerson = RefDefault.create((Person)null);
		
		new ReactionDefault<Person>(new Source<Person>() {
			@Override
			public Person get() throws NoInstanceAvailableException {
				int currentIndex = index.get();
				ListBox<Person> currentList = listRef.get();
				if (currentList == null || currentIndex < 0 || currentIndex >= currentList.size()) {
					return null;
				}
				Person person = currentList.get(currentIndex);
				return person;
			}
		}, selectedPerson, listRef, index);
		
		Multiverse.getCurrentUniverse().internal().acquire();
		
//		System.out.println("Should be 4: " + index.get() + ", " + selectedPerson.get());
		
		list.remove(0);
//		System.out.println("Should be 3: " + index.get() + ", " + selectedPerson.get());

		list.remove(7);
//		System.out.println("Should be 3: " + index.get() + ", " + selectedPerson.get());

		list.remove(0);
//		System.out.println("Should be 2: " + index.get() + ", " + selectedPerson.get());

		list.add(0, new Person("New person at 0", "", 0, null));
//		System.out.println("Should be 3: " + index.get() + ", " + selectedPerson.get());

		list.add(5, new Person("New person at 5", "", 4, null));
//		System.out.println("Should be 3: " + index.get() + ", " + selectedPerson.get());

		list.set(3, new Person("New person at 3, replacing selection", "", 3, null));
//		System.out.println("Should be 3: " + index.get() + ", " + selectedPerson.get());

		ListBox<Person> secondList = ListBoxDefault.<Person>create();

		for (int i = 0; i < 10; i++) {
			secondList.add(new Person("Second List Person " + i, "Description " + i, i, null));
		}

//		System.out.println("Setting listRef to secondList");
		listRef.set(secondList);
		
//		System.out.println("Should be 0: " + index.get() + ", " + selectedPerson.get());

		System.out.println("Setting index to 6");
		index.set(6);
		
//		System.out.println("Should be 6: " + index.get() + ", " + selectedPerson.get());
		
		secondList.add(0, new Person("New person at 0", "", 0, null));
//		System.out.println("Should be 7: " + index.get() + ", " + selectedPerson.get());

		secondList.add(8, new Person("New person at 8", "", 8, null));
//		System.out.println("Should be 7: " + index.get() + ", " + selectedPerson.get());
		secondList.add(0, new Person("New person at 0", "", 0, null));
		secondList.add(0, new Person("New person at 0", "", 0, null));
		secondList.add(0, new Person("New person at 0", "", 0, null));

		secondList.set(10, new Person("New person at 10, replacing selection", "", 10, null));
		System.out.println("Should be 10: " + index.get() + ", " + selectedPerson.get());

		Multiverse.getCurrentUniverse().internal().release();
		
	}

}
