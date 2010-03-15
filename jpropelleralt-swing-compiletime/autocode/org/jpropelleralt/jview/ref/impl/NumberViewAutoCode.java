package org.jpropelleralt.jview.ref.impl;

import org.jpropelleralt.autocode.AutoCode;

/**
 * Automatic code generation for {@link NumberView}
 */
public class NumberViewAutoCode{
	
	/**
	 * Automatically generate repeated code
	 * @return	Code
	 */
	public static String autoCode() {
		return AutoCode.makeNumberTypes(NumberViewAutoCode.class);
	}
	
	/**
	 * Print {@link #autoCode()} output to {@link System#out}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println(autoCode());
	}
	
}
