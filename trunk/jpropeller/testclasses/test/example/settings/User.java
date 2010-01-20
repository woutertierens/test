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
import org.jpropeller.properties.Prop;

/**
 * Stores a user
 */
public class User extends BeanDefault {

	/**
	 * The {@link PropName} for {@link #group()} property
	 */
	public final static PropName<Group> GROUP_NAME = PropName.create(Group.class, "group");

	//Set up Props
	private Prop<String> name = editable("name", "default name");
	private Prop<Group> group = editable(Group.class, "group", null);
	private Prop<String> groupPermissions = 
		editableFrom(String.class, "groupPermissions", this).
			via(GROUP_NAME).
			to(Group.PERMISSIONS_NAME);
	
	private Prop<String> permissionsOverride = editable("permissionsOverride", (String)null);
	
	/**
	 * Access to group
	 * @return
	 * 		group
	 */
	public Prop<Group> group() {return group;};
	
	/**
	 * Access to name
	 * @return
	 * 		name property
	 */
	public Prop<String> name() {return name;};

	/**
	 * Access to groupPermissions
	 * @return
	 * 		groupPermissions property
	 */
	public Prop<String> groupPermissions() {return groupPermissions;};

	/**
	 * Access to permissionsOverride
	 * @return
	 * 		permissionsOverride property
	 */
	public Prop<String> permissionsOverride() {return permissionsOverride;};
	
	@Override
	public String toString() {
		String s = name().get() + " in " + group().get();
		return s;
	}
	
}
