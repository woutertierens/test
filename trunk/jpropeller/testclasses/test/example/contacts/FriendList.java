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
import org.jpropeller.properties.list.EditableListProp;

/**
 * Stores an address
 * @author shingoki
 */
public class FriendList extends BeanDefault {

	//Set up Props
	private EditableProp<String> title = editable("title", "default friend list title");
	private EditableListProp<Person> friends = editableList(Person.class, "friends");
	
	/**
	 * Access to friends 
	 * @return
	 * 		friends property
	 */
	public EditableListProp<Person> friends() {return friends;};
	
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
		for (Person p : friends().get()) {
			if (!first) {
				s += ", ";
			}
			s += p.name().get();
			first = false;
		}
		s += "]";
		return s;
	}
}
