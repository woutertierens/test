package demo.altpath;

import javax.swing.SwingUtilities;

import org.jpropeller.properties.path.impl.EditablePathProp;
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
				b.c().set(c);
				//c.d().set(d);
				//d.i().set(42);

				System.out.println(a);
				System.out.println(a.b());
				System.out.println(b.c());
				System.out.println(c.d());
				
				//System.out.println(b.c());
				
				System.out.println(a.d().get());
				//System.out.println(a.d().get().i().get());
		
				FIXME fix problem with paths that lead to null - we need to have a consistent response to any stage of the path
				being broken - run time exception or just return null?
				
			}
		
		});
		
	}
	
}
