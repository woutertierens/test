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
import org.jpropeller.properties.map.EditableMapProp;

/**
 * Stores mappings from nicknames to {@link Person}s
 * @author shingoki
 */
public class Nicknames extends BeanDefault {

	//Set up Props
	private EditableProp<String> title = editable("title", "default alias map title");
	private EditableMapProp<String, Person> nicknames = editableMap(Person.class, "nicknames");
	
	/**
	 * Access to nicknames 
	 * @return
	 * 		nicknames property
	 */
	public EditableMapProp<String, Person> nicknames() {return nicknames;};
	
	/**
	 * Access to title
	 * @return
	 * 		street property
	 */
	public EditableProp<String> title() {return title;};
		
	@Override
	public String toString() {
		String s = title().get() + ": [";
		boolean first = true;
		for (String n : nicknames().get().keySet()) {
			if (!first) {
				s += ", ";
			}
			s += n + " -> " + nicknames().get(n);
			first = false;
		}
		s += "]";
		return s;
	}
}
