package test.example.contacts;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;

/**
 * Demonstrate {@link Nicknames}
 * @author bwebster
 */
public class NicknamesDemo {
	
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
		
		Nicknames n = new Nicknames();
		
		n.props().addListener(new PropListener() {
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
		n.title().set("Example nicknames");
		System.out.println(">>> Add Alice as Al");
		n.nicknames().put("Al", a);
		System.out.println(">>> Add Bob as Bobby");
		n.nicknames().put("Bobby", b);
		
		System.out.println("Nicknames: " + n.toString());

		System.out.println(">>> Rename Alice");
		a.name().set("Alicia");

		System.out.println(">>> Rename Alicia via map");
		n.nicknames().get("Al").name().set("Alexandra");

		System.out.println(">>> Remove Bob from map");
		n.nicknames().get().remove("Bobby");

		System.out.println(">>> Change Al mapping to point to Bob");
		n.nicknames().put("Al", b);

	}
	
}
