/*
 *  $Id: org.eclipse.jdt.ui.prefs,v 1.1 2008/03/24 11:20:15 shingoki Exp $
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

package test.properties.list;


import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.util.PrintingChangeListener;
import org.junit.Before;
import org.junit.Test;

import test.example.contacts.FriendList;
import test.example.contacts.Person;

/**
 * Test {@link ListPropDefault}
 */
public class ListPropDefaultTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Unit test method
	 * @throws Exception
	 * 		On any error in test
	 */
	@Test
	public void testStuff() throws Exception {
		
		Person a = new Person();
		a.name().set("Alice");
		
		Person b = new Person();
		b.name().set("Bob");
		
		
		FriendList fl = new FriendList();
		
		fl.features().addListener(new PrintingChangeListener());
		
		System.out.println(">>> Change title");
		fl.title().set("Example Friend List");
		System.out.println(">>> Add Alice");
		fl.friends().add(a);
		System.out.println(">>> Add Bob");
		fl.friends().get().add(b);
		
		System.out.println("Friends List: " + fl.toString());

		System.out.println(">>> Rename Alice");
		a.name().set("Alicia");

		System.out.println(">>> Rename Alicia via list");
		fl.friends().get().get(0).name().set("Alexandra");
		
	}
	
}
