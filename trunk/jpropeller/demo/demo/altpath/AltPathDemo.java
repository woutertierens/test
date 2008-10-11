package demo.altpath;

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
		A a = new A();
		B b = new B();
		C c = new C();
		D d = new D();
		
		a.b().set(b);
		b.c().set(c);
		c.d().set(d);
		d.i().set(42);
		
		/*
		Transformer<A, B> aToB = new Transformer<A, B>() {
			public B transform(A s) {
				return s.b().get(); 
			}
		};
		*/
	}
	
}
