package org.jpropeller.util;

import java.util.List;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListCalculated;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.list.impl.ListPropDefault;

/**
 * Single point of access for making props and related objects
 */
public class P {

	/**
	 * Static methods only
	 */
	private P(){
	}
	
	/**
	 * Make a {@link Prop} containing a {@link CListCalculated}
	 * @param <T>				The type of list contents
	 * @param contentsClass		The class of list contents
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public static <T> Prop<CList<T>> calculatedList(Class<T> contentsClass, String name, Calculation<List<T>> calculation) {
		return ListPropDefault.calculated(contentsClass, name, calculation);
	}

	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param <T>				The type of {@link Prop} contents
	 * @param contentsClass		The class of {@link Prop} contents
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public static <T> Prop<T> calculated(Class<T> contentsClass, String name, Calculation<T> calculation) {
		return new CalculatedProp<T>(PropName.create(contentsClass, name), calculation);
	}
	
	

	
}
