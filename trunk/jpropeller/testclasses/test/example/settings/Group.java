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
package test.example.settings;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.list.EditableListProp;

/**
 * Stores an address
 * @author shingoki
 */
public class Group extends BeanDefault {

	/**
	 * The {@link PropName} for {@link #permissions()} property
	 */
	public final static PropName<EditableProp<String>, String> PERMISSIONS_NAME = PropName.editable("permissions", String.class);

	//Set up Props
	private EditableProp<String> name = editable("name", "default group name");
	private EditableListProp<User> users = editableList(User.class, "users");
	private EditableProp<String> permissions = editable("permissions", "default group permissions");
	
	/**
	 * Access to users
	 * @return
	 * 		users property
	 */
	public EditableListProp<User> users() {return users;};
	
	/**
	 * Access to name
	 * @return
	 * 		name property
	 */
	public EditableProp<String> name() {return name;};

	/**
	 * Access to permissions
	 * @return
	 * 		permissions property
	 */
	public EditableProp<String> permissions() {return permissions;};

	@Override
	public String toString() {
		String s = name().get() + ", " + 
		permissions().get() + ": [";
		boolean first = true;
		for (User p : users().get()) {
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
