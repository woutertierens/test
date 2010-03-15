package org.jpropelleralt.box;

import org.jpropelleralt.autocode.AutoCode;

/**
 * Automatic code generation for {@link RefDefault}
 */
public class BoxesAutoCode{
	
	/**
	 * Automatically generate repeated code
	 * @return	Code
	 */
	public static String autoCode() {
		return AutoCode.makeCrudeTypes(BoxesAutoCode.class);
	}
	
	/**
	 * Print {@link #autoCode()} output to {@link System#out}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println(autoCode());
	}
	
}
