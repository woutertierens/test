package demo.altpath;

import javax.swing.SwingUtilities;

import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.util.GeneralUtils;


/**
 * Simple demonstration of an alternative path technique
 * @author shingoki
 *
 */
public class AltPathDemo {

	/**
	 * Demonstrate A to D
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
		
			@Override
			public void run() {
				
				GeneralUtils.enableConsoleLogging(PathProp.class);
				
				A a = new A();
				B b = new B();
				C c = new C();
				D d = new D();
				
				a.b().set(b);

				System.out.println("Get expected to return null:" + a.dByTransforms().get());

				b.c().set(c);
				c.d().set(d);
				d.i().set(42);

				System.out.println(a);
				System.out.println(a.b());
				System.out.println(b.c());
				System.out.println(c.d());
				
				//System.out.println(b.c());
				
				System.out.println(a.dByTransforms().get());
				System.out.println(a.dByTransforms().get().i().get());
		
			}
		
		});
		
	}
	
}
