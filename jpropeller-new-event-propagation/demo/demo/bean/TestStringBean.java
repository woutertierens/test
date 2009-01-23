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
package demo.bean;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.system.Props;

/**
 * Simple bean for testing
 * @author shingoki
 *
 */
public class TestStringBean implements Bean {

	//Standard code block for a bean
	ExtendedBeanFeatures services = Props.getPropSystem().createExtendedBeanFeatures(this);
	
	@Override
	public BeanFeatures features() {
		return services;
	}

	//Set up Props
	EditableProp<String> name = services.editable("name", "default name value");
	EditableProp<String> street = services.editable("street", "default street value");
	
	/**
	 * Access to name
	 * @return
	 * 		name property
	 */
	public EditableProp<String> name() {return name;};
	/**
	 * Access to street
	 * @return
	 * 		street property
	 */
	public EditableProp<String> street() {return street;};
	
	@Override
	public String toString() {
		String s = "'" + name().get() + "', street '" + street().get() + "'";
		return s;
	}
	
}
