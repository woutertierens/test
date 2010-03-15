package org.jpropelleralt.ref.impl;

import org.jpropelleralt.autocode.AutoCode;

/**
 * Automatic code generation for {@link RefDefault}
 */
public class RefDefaultAutoCode{
	
	/**
	 * Automatically generate repeated code
	 * @return	Code
	 */
	public static String autoCode() {
		return AutoCode.makeCrudeTypes(RefDefaultAutoCode.class);
	}
	
	/**
	 * Print {@link #autoCode()} output to {@link System#out}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println(autoCode());
	}
	
}
