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
import org.jpropeller.properties.Prop;
import org.jpropeller.system.Props;

/**
 * Simple bean for testing
 */
public class TestStringBean implements Bean {

	//Standard code block for a bean
	ExtendedBeanFeatures services = Props.getPropSystem().createExtendedBeanFeatures(this);
	
	@Override
	public BeanFeatures features() {
		return services;
	}

	//Set up Props
	Prop<String> name = services.editable("name", "default name value");
	Prop<String> street = services.editable("street", "default street value");
	Prop<Double> progress = services.editable("progress", 0.5d);
	Prop<Integer> percent = services.editable("progress", 50);
	
	/**
	 * Access to name
	 * @return
	 * 		name property
	 */
	public Prop<String> name() {return name;};
	/**
	 * Access to street
	 * @return
	 * 		street property
	 */
	public Prop<String> street() {return street;};

	/**
	 * Access to progress
	 * @return
	 * 		progress property
	 */
	public Prop<Double> progress() {return progress;};
	/**
	 * Access to percent
	 * @return
	 * 		percent property
	 */
	public Prop<Integer> percent() {return percent;};

	@Override
	public String toString() {
		String s = "'" + name().get() + "', street '" + street().get() + "'";
		return s;
	}
	
}
