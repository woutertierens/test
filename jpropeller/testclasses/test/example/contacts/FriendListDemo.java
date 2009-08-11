package test.example.contacts;

import org.jpropeller.util.PrintingChangeListener;

/**
 * Demonstrate the {@link FriendList}
 */
public class FriendListDemo {
	
	/**
	 * Run demo
	 * @param args
	 * 	ignored
	 */
	public static void main(String[] args) {
		Person a = new Person();
		a.name().set("Alice");
		
		Person b = new Person();
		b.name().set("Bob");
		
		
		FriendList fl = new FriendList();
		
		fl.features().addListener(new PrintingChangeListener());
		
		System.out.println(">>> Change title");
		fl.title().set("Example Friend List");
		System.out.println(">>> Add Alice");
		fl.friends().add(a);
		System.out.println(">>> Add Bob");
		fl.friends().get().add(b);
		
		System.out.println("Friends List: " + fl.toString());

		System.out.println(">>> Rename Alice");
		a.name().set("Alicia");

		System.out.println(">>> Rename Alicia via list");
		fl.friends().get().get(0).name().set("Alexandra");

	}
	
}
