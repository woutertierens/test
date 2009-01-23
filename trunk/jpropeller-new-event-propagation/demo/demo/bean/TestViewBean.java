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
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.changeable.impl.EditableChangeablePropDefault;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.PrintingChangeListener;

/**
 * Bean to test props
 * @author shingoki
 */
public class TestViewBean implements Bean {

	//Standard code block for a bean
	ExtendedBeanFeatures services = Props.getPropSystem().createExtendedBeanFeatures(this);
	
	@Override
	public BeanFeatures features() {
		return services;
	}

	//Set up Props
	EditableProp<String> name = services.editable("name", "default name value");
	EditableProp<TestViewBean> child = services.editable(TestViewBean.class, "child", null);
	
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
		
		GeneralUtils.enableConsoleLogging(EditableChangeablePropDefault.class);
		
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

		ChangeListener listener = new PrintingChangeListener();
		a.features().addListener(listener);
		b.features().addListener(listener);
		c.features().addListener(listener);

		
		System.out.println("Changing d");
		d.name.set("d changed");
		System.out.println("Changed d");
		
	}
	
}
