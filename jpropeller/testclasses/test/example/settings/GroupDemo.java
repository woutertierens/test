package test.example.settings;

import org.jpropeller.util.GeneralUtils;

/**
 * Demonstration of {@link Group}
 */
public class GroupDemo {

	/**
	 * Demonstrate {@link Group}
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		GeneralUtils.enableConsoleLogging();
		
		Group f = new Group();
		f.name().set("Group f");
		User a = new User();
		a.group().set(f);
		
		System.out.println(">>>Group f permissions");
		System.out.println(a.groupPermissions().get());

		System.out.println(">>>Change Initial group permissions");
		f.permissions().set("New permissions for group f");
		System.out.println(">>>Get Initial group permissions from a");
		System.out.println(a.groupPermissions().get());

		Group g = new Group();
		g.name().set("Group g");
		g.permissions().set("Group g initial permissions");

		System.out.println(">>>Change to group g");
		a.group().set(g);
		System.out.println(">>>Get group permissions from a");
		System.out.println(a.groupPermissions().get());

		System.out.println(">>>Change group g permissions");
		g.permissions().set("New permissions for group g");
		System.out.println(">>>Get group permissions from a");
		System.out.println(a.groupPermissions().get());

		
	}
	
}
