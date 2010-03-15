package org.jpropelleralt.sequence.impl;

import org.jpropelleralt.autocode.AutoCode;

/**
 * Automatic code generation for {@link Sequences}
 */
public class SequencesAutoCode{
	
	/**
	 * Automatically generate repeated code
	 * @return	Code
	 */
	public static String autoCode() {
		return AutoCode.makeNumberTypes(SequencesAutoCode.class);
	}
	
	/**
	 * Print {@link #autoCode()} output to {@link System#out}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println(autoCode());
	}
	
}
