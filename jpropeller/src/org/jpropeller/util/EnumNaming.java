package org.jpropeller.util;

import java.util.EnumMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides friendly naming for {@link Enum}s, via resource bundle
 */
public class EnumNaming {

	/**
	 * Get a map from {@link Enum} elements to their localized names,
	 * or their {@link Enum#name()} if full localization is not available.
	 * @param <E>			The type of {@link Enum}
	 * @param clazz			The {@link Class} of {@link Enum}
	 * @param enumValues	The values of {@link Enum} for which entries should be made - 
	 * 						generally just the result of the static {@link Enum} values() method.
	 * @return				A map from elements of enumValues to their localized names
	 */
	public static <E extends Enum<E>> EnumMap<E, String> loadNames(Class<E> clazz, E[] enumValues) {
		EnumMap<E,String> names = new EnumMap<E, String>(clazz);
		
		//Default to name()
		for (E e : enumValues) {
			names.put(e, e.name());
		}
		
		//Try to replace with localized strings
		if (enumValues.length > 0) {
			try {
				ResourceBundle res = ResourceBundle.getBundle(enumValues[0].getClass().getCanonicalName());
				for (E e : enumValues) {
					names.put(e, res.getString(e.name()));
				}
			} catch (MissingResourceException mre) {
				//Give up if bundle or any individual resources are missing
			}
		}
		
		return names;
	}
	
}
