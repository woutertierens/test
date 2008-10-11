/*
 *  $Id: TestBean.java,v 1.1 2008/03/24 11:20:00 shingoki Exp $
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
package demo.bean;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.bean.impl.EditableBeanPropDefault;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;

/**
 * Bean to test props
 * @author shingoki
 *
 */
public class TestBean implements Bean {

	//Standard code block for a bean
	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	
	@Override
	public PropMap props() {
		return propMap;
	}

	//Set up Props
	EditableProp<String> name = propMap.editable("name", "default name value");
	EditableProp<Integer> age = propMap.editable("age", 42);
	EditableProp<TestBean> child = propMap.editable(TestBean.class, "child", null);
	
	/**
	 * Access to name
	 * @return
	 * 		name property
	 */
	public EditableProp<String> name() {return name;};
	/**
	 * Access to age
	 * @return
	 * 		age property
	 */
	public EditableProp<Integer> age() {return age;};
	/**
	 * Access to child
	 * @return
	 * 		child property
	 */
	public EditableProp<TestBean> child() {return child;};
	
	@Override
	public String toString() {
		String s = name().get() + ", age " + age.get() + ", child";
		if (child.get() == null) {
			s += " null";
		} else {
			s += ": (" + child.get().name().get() + ")";
		}
		return s;
	}

	
	
	
	
	
	
	/**
	 * Demonstrate use of the bean
	 * @param args
	 */
	public final static void main(String[] args) {
		
		GeneralUtils.enableConsoleLogging(EditableBeanPropDefault.class);
		
		TestBean b = new TestBean();
		System.out.println(b);
		
		b.name().set("new name value");
		System.out.println(b);

		b.age().set(43);
		System.out.println(b);
		
		System.out.println(b.age().getName());
		System.out.println(b.props());
		System.out.println(b.age().props());
		System.out.println(b.age().getBean());
		System.out.println(b.props().getBean());
		
		System.out.println(b.props().get(PropName.editable("name", String.class)).get());
				
		b.props().addListener(new PropListener() {
			@Override
			public <T> void propChanged(PropEvent<T> event) {
				System.out.println(event);
			}
		});

		System.out.println("Setting name to alice");
		b.name().set("alice");
		
		System.out.println("Setting Child to new TestBean");
		TestBean bob = new TestBean();
		b.child().set(bob);
		
		System.out.println(b);
		
		System.out.println("Setting child's name to bob");
		bob.name().set("bob");
		
		System.out.println("Setting child's child to own parent (weird!)");
		bob.child().set(b);
		
	}
	
}
