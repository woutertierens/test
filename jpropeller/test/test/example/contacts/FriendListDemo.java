package test.example.contacts;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;

/**
 * Demonstrate the {@link FriendList}
 * @author bwebster
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
		
		fl.props().addListener(new PropListener() {
			@Override
			public <T> void propChanged(PropEvent<T> event) {
				System.out.println("EVENT:");
				System.out.println(event);
				
				PropEvent<?> cause = event;
				while ((cause = cause.getCauseEvent())!=null) {
					System.out.println(" caused by:");
					System.out.println(cause);
				}
				
			}
		});
		
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
