package test.example;

import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.util.GeneralUtils;

/**
 * Quick demonstration of {@link Mirror}
 * @author bwebster
 */
public class MirrorDemo {
	
	/**
	 * Demonstrate {@link Mirror}
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		GeneralUtils.enableConsoleLogging(PathProp.class);
	
		Mirror a = new Mirror();
		System.out.println(a.sMirror().get());
		a.s().set("new name");
		System.out.println(a.sMirror().get());

	}

}
