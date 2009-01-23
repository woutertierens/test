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
package test.example.contacts;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;

/**
 * Stores an address
 * @author shingoki
 */
public class Address extends BeanDefault {

	//Set up Props
	private EditableProp<Integer> houseNumber = editable("houseNumber", 1);
	private EditableProp<String> street = editable("street", "default street value");
	
	/**
	 * Access to house number
	 * @return
	 * 		house number property
	 */
	public EditableProp<Integer> houseNumber() {return houseNumber;};
	
	/**
	 * Access to street
	 * @return
	 * 		street property
	 */
	public EditableProp<String> street() {return street;};
		
	@Override
	public String toString() {
		String s = houseNumber().get() + ", " + street().get();
		return s;
	}
	
}
