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
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;

/**
 * Demonstrates mirroring of a property, using a {@link PathProp} with a path
 * having only a last name
 */
public class Mirror extends BeanDefault {

	/**
	 * The {@link PropName} for {@link #s()} property
	 */
	public final static PropName<String> S_NAME = PropName.create("s", String.class);

	//Set up Props
	private Prop<String> s = editable("s", "default s value");
	private Prop<String> sMirror = editableFrom("sMirror", String.class, this).to(S_NAME);
	
	/**
	 * Access to s
	 * @return
	 * 		s
	 */
	public Prop<String> s() {return s;};
	
	/**
	 * Access to sMmirror
	 * @return
	 * 		sMirror property
	 */
	public Prop<String> sMirror() {return sMirror;};

	@Override
	public String toString() {
		String s = "Mirror";
		return s;
	}
	
}
