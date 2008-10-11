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
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.bean.impl.EditableBeanPropDefault;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;

/**
 * Bean to test props
 * @author shingoki
 */
public class TestViewBean implements Bean {

	//Standard code block for a bean
	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	
	@Override
	public PropMap props() {
		return propMap;
	}

	//Set up Props
	EditableProp<String> name = propMap.editable("name", "default name value");
	EditableProp<TestViewBean> child = propMap.editable(TestViewBean.class, "child", null);
	
	/**
	 * Access to name
	 * @return
	 * 		name property
	 */
	public EditableProp<String> name() {return name;};
	/**
	 * Access to child
	 * @return
	 * 		child property
	 */
	public EditableProp<TestViewBean> child() {return child;};
	
	@Override
	public String toString() {
		String s = name().get() + ", child";
		if (child.get() == null) {
			s += " null";
		} else {
			s += ": (" + child.get().name().get() + ")";
		}
		return s;
	}
	
	
	
	
	
	/**
	 * Demonstrate use of the bean
	 * 
	 * @param args
	 */
	public final static void main(String[] args) {
		
		GeneralUtils.enableConsoleLogging(EditableBeanPropDefault.class);
		
		TestViewBean a = new TestViewBean();
		a.name().set("a");
		TestViewBean b = new TestViewBean();
		b.name().set("b");
		TestViewBean c = new TestViewBean();
		c.name().set("c");
		TestViewBean d = new TestViewBean();
		d.name().set("d");

		//Construct a tree to fan out events
		a.child().set(d);
		b.child().set(d);
		c.child().set(d);

		//Listen to internal and external events on a, b and c
		PropInternalListener internalListener = new PropInternalListener() {
			@Override
			public <T> void propInternalChanged(PropEvent<T> event) {
				System.out.println("INTERNAL: " + event);
			}
		};
		a.props().addInternalListener(internalListener);
		b.props().addInternalListener(internalListener);
		c.props().addInternalListener(internalListener);

		
		PropListener listener = new PropListener() {
			@Override
			public <T> void propChanged(PropEvent<T> event) {
				System.out.println("view: " + event);
			}
		};
		a.props().addListener(listener);
		b.props().addListener(listener);
		c.props().addListener(listener);

		
		System.out.println("Changing d");
		d.name.set("d changed");
		System.out.println("Changed d");
		
	}
	
}
