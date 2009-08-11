package org.jpropeller.view;

import org.jpropeller.ui.IconFactory;
import org.jpropeller.ui.impl.MinimalIconFactory;
import org.jpropeller.view.impl.ViewSystemDefault;

/**
 * Central source for System-wide aspects of View system 
 */
public class Views {

	//Static class
	private Views(){}
	
	private static ViewSystem viewSystem = new ViewSystemDefault();
	private static IconFactory iconFactory = new MinimalIconFactory();
	
	/**
	 * Get the system-wide {@link ViewSystem}
	 * @return
	 * 		The {@link ViewSystem}
	 */
	public static ViewSystem getViewSystem() {
		return viewSystem;
	}
	
	/**
	 * Get the system-wide {@link IconFactory}
	 * @return
	 * 		The {@link IconFactory}
	 */
	public static IconFactory getIconFactory() {
		return iconFactory;
	}

	/**
	 * Set the system-wide {@link IconFactory}
	 * @param factory		The new {@link IconFactory}
	 */
	public static void setIconFactory(IconFactory factory) {
		iconFactory = factory;
	}

}
