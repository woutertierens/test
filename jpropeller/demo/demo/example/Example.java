package demo.example;

import org.jpropeller.util.GraphPrinter;

import test.example.contacts.Address;
import test.example.contacts.FriendList;
import test.example.contacts.Person;


/**
 * Example of using test beans
 */
public class Example {

	/**
	 * Example of using test beans
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		Address address = new Address();
		address.houseNumber().set(42);
		address.street().set("bobstreet");
		
		Person person = new Person();
		person.name().set("bob");
		person.address().set(address);

		Address address2 = new Address();
		address2.houseNumber().set(43);
		address2.street().set("alicestreet");
		
		Person person2 = new Person();
		person2.name().set("alice");
		person2.address().set(address2);

		FriendList friendList = new FriendList();
		friendList.title().set("some friends");
		friendList.friends().add(person);
		friendList.friends().add(person2);
		friendList.friends().add(person);

		new GraphPrinter().printTree(friendList);
		
		/*
		friendList.features().addListener(new PrintingChangeListener());
		
		System.out.println(friendList.friends().get());
		for (Person p : friendList.friends()) {
			System.out.println(p);
		}
		
		person.name().set("bob's new name");
		*/
				
	}

}
