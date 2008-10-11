/*
 *  $Id: TestStringBean.java,v 1.1 2008/03/24 11:19:58 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package test.example;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;

/**
 * A bean with lots of props
 * @author shingoki
 */
public class LotsOfProps extends BeanDefault {

	private EditableProp<String> stringProp = editable("stringProp", "A String");
	private EditableProp<Integer> intProp = editable("intProp", 1);
	private EditableProp<Long> longProp = editable("longProp", 10l);
	private EditableProp<Float> floatProp = editable("floatProp", 1.1f);
	private EditableProp<Double> doubleProp = editable("doubleProp", 1.11d);
	private EditableProp<Boolean> booleanProp = editable("booleanProp", false);
	
	/**
	 * Accessor for booleanProp
	 * @return Editable property booleanProp
	 */
	public EditableProp<Boolean> booleanProp() {
		return booleanProp;
	}
	
	/**
	 * Accessor for floatProp
	 * @return Editable property floatProp
	 */
	public EditableProp<Float> floatProp() {
		return floatProp;
	}


	/**
	 * Accessor for longProp
	 * @return Editable property longProp
	 */
	public EditableProp<Long> longProp() {
		return longProp;
	}
	

	/**
	 * Accessor for doubleProp
	 * @return Editable property doubleProp
	 */
	public EditableProp<Double> doubleProp() {
		return doubleProp;
	}
	

	/**
	 * Accessor for intProp
	 * @return Editable property intProp
	 */
	public EditableProp<Integer> intProp() {
		return intProp;
	}
	

	/**
	 * Accessor for stringProp
	 * @return Editable property stringProp
	 */
	public EditableProp<String> stringProp() {
		return stringProp;
	}

	@Override
	public String toString() {
		String s = 
			stringProp().get() + 
			", int " + intProp.get() + 
			", long " + longProp.get() + 
			", float " + floatProp.get() + 
			", double " + doubleProp.get() + 
			", boolean " + booleanProp.get();
		return s;
	}
	
}
