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

import java.awt.Color;
import java.awt.Image;
import java.util.Comparator;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.comparison.ComparisonType;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.Values;
import org.jpropeller.properties.values.impl.NonEmptyStringProcessor;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.view.Views;

/**
 * A bean with lots of {@link Prop}s
 */
public class LotsOfProps extends BeanDefault {

	private final static Image DEFAULT_IMAGE = Views.getIconFactory().getImage(IconSize.SMALL, "actions", "address-book-new");
	
	private Prop<String> stringProp = editable("stringProp", "A String");
	private Prop<Integer> intProp = editable("intProp", 1);
	private Prop<Long> longProp = editable("longProp", 10l);
	private Prop<Float> floatProp = editable("floatProp", 1.1f);
	private Prop<Double> doubleProp = editable("doubleProp", 1.11d);
	private Prop<Color> colorProp = editable("colorProp", new Color(0.2f, 0.5f, 0.75f));
	private Prop<Boolean> booleanProp = editable("booleanProp", false);
	private Prop<ImmutableIcon> imageProp = editable("imageProp", ImmutableIcon.fromImage(DEFAULT_IMAGE));
	private Prop<String> validatedStringProp = create("validatedStringProp", "Mustn't be empty", NonEmptyStringProcessor.get(true));
	
	private ValueProcessor<Double> moreThan5 = Values.comparison(ComparisonType.MORE_THAN, 5d);
	private Prop<Double> moreThan5Prop = create("moreThan5Prop", 6d, moreThan5);

	private Comparator<Color> brightnessComparator = new Comparator<Color>() {
		float[] hsbvals = new float[3];
		@Override
		public int compare(Color o1, Color o2) {
			return Float.compare(brightness(o1), brightness(o2));
		}
		private final float brightness(Color c) {
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
			return hsbvals[2];
		}
	};
	
	private ValueProcessor<Color> bright = Values.comparison(brightnessComparator, ComparisonType.MORE_THAN, new Color(0.5f, 0.5f, 0.5f));
	private Prop<Color> brightProp = create("brightProp", Color.WHITE, bright);
	
	/**
	 * Accessor for booleanProp
	 * @return Editable property booleanProp
	 */
	public Prop<Boolean> booleanProp() {
		return booleanProp;
	}
	
	/**
	 * Accessor for floatProp
	 * @return Editable property floatProp
	 */
	public Prop<Float> floatProp() {
		return floatProp;
	}


	/**
	 * Accessor for longProp
	 * @return Editable property longProp
	 */
	public Prop<Long> longProp() {
		return longProp;
	}
	

	/**
	 * Accessor for doubleProp
	 * @return Editable property doubleProp
	 */
	public Prop<Double> doubleProp() {
		return doubleProp;
	}
	

	/**
	 * Accessor for intProp
	 * @return Editable property intProp
	 */
	public Prop<Integer> intProp() {
		return intProp;
	}
	

	/**
	 * Accessor for stringProp
	 * @return Editable property stringProp
	 */
	public Prop<String> stringProp() {
		return stringProp;
	}

	/**
	 * Accessor for validatedStringProp
	 * @return Editable property validatedStringProp
	 */
	public Prop<String> validatedStringProp() {
		return validatedStringProp;
	}

	/**
	 * Accessor for moreThan5Prop
	 * @return Editable property moreThan5Prop
	 */
	public Prop<Double> moreThan5Prop() {
		return moreThan5Prop;
	}

	/**
	 * Accessor for imageProp
	 * @return Property imageProp
	 */
	public Prop<ImmutableIcon> imageProp() {
		return imageProp;
	}

	/**
	 * Accessor for colorProp
	 * @return Property colorProp
	 */
	public Prop<Color> colorProp() {
		return colorProp;
	}

	/**
	 * Accessor for brightProp
	 * @return Property brightProp
	 */
	public Prop<Color> brightProp() {
		return brightProp;
	}

	@Override
	public String toString() {
		String s = 
			stringProp().get() + 
			", int " + intProp.get() + 
			", long " + longProp.get() + 
			", float " + floatProp.get() + 
			", double " + doubleProp.get() + 
			", boolean " + booleanProp.get() + 
			", image " + imageProp.get() +
			", color " + colorProp.get();
		return s;
	}
	
}
