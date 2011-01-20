package org.jpropeller.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;

/**
 * Utility methods for working with JPropeller
 */
public class PropUtils {

	/**
	 * Find the user-readable, localised name for a {@link Prop} of a {@link Bean}
	 * @param beanClass
	 * 		The class of bean containing the prop
	 * @param prop
	 * 		The prop
	 * @return
	 * 		The user readable name. This will be localised as far as possible
	 * for the current {@link Locale}, using resource bundles for the class
	 * name. If no resource is found, the string value of the {@link PropName}
	 * will be returned.
	 */
	public static String localisedName(Class<?> beanClass, Prop<?> prop) {
		PropName<?> name = prop.getName();
		try {
			ResourceBundle res = ResourceBundle.getBundle(beanClass.getCanonicalName());
			return res.getString(name.getString());
		} catch (MissingResourceException mre) {
			return name.getString(); 
		}
	}
	
	/**
	 * Find the user-readable, localised name for a {@link Prop} of a {@link Bean}
	 * @param beanClass
	 * 		The class of bean containing the prop
	 * @param propName
	 * 		The string prop name
	 * @return
	 * 		The user readable name. This will be localised as far as possible
	 * for the current {@link Locale}, using resource bundles for the class
	 * name. If no resource is found, the string value of the {@link PropName}
	 * will be returned.
	 */
	public static String localisedName(Class<?> beanClass, String propName) {
		try {
			ResourceBundle res = ResourceBundle.getBundle(beanClass.getCanonicalName());
			return res.getString(propName);
		} catch (MissingResourceException mre) {
			return propName; 
		}
	}
	
	/**
	 * Find the user-readable, localised setting for a given property of a class
	 * Note that the property must be "true" (when trimmed and converted to lower case)
	 * to give a true value, all other strings will be treated as false.
	 * @param clazz The class
	 * @param name The name of the property
	 * @return The setting, or false if not found
	 */
	public static boolean localisedBoolean(Class<?> clazz, String name) {
		try {
			ResourceBundle res = ResourceBundle.getBundle(clazz.getCanonicalName());
			return "true".equals(res.getString(name).trim().toLowerCase());
		} catch (MissingResourceException mre) {
			return false; 
		}
	}
	
	/**
	 * Find the user-readable, localised name for a class of {@link Bean}
	 * @param beanClass		The class of bean containing the prop
	 * @return				The user readable class name. This will be 
	 * 						localised as far as possible for the current 
	 * 						{@link Locale}, using resource bundles for the class
	 * 						name. If no resource is found, the value of 
	 * 						{@link Class#getName()} will be returned.
	 */
	public static String localisedClassName(Class<?> beanClass) {
		try {
			ResourceBundle res = ResourceBundle.getBundle(beanClass.getCanonicalName());
			return res.getString("Bean.Class");
		} catch (MissingResourceException mre) {
			return beanClass.getName(); 
		}
	}
	
	/**
	 * Build a list of the {@link Prop}s in the bean - this
	 * filters out {@link Prop}s that have a generic {@link PropName} 
	 * @param b		The bean from which to get names of {@link Prop}s
	 * @return		A list of the names
	 */
	public static List<Prop<?>> buildNonGenericPropsList(Bean b) {
		List<Prop<?>> list = b.features().getList();
		List<Prop<?>> props = new ArrayList<Prop<?>>(list.size());
		for (Prop<?> g : list) {
			if (!g.getName().isTGeneric()) {
				props.add(g);
			}
		}
		
		return props;
	}
	
	/**
	 * Return a string describing changes (as reported to a {@link ChangeListener})
	 * @param initial
	 * 		The initial changes
	 * @param changes
	 * 		The map of all changes
	 * @return
	 * 		A string (multiple lines) describing the changes
	 */
	public static String describeChanges(List<Changeable> initial, Map<Changeable, Change> changes) {
		StringBuilder s = new StringBuilder();
		s.append("Initial change was to " + initial + "\n");
		s.append("All changes:\n");
		for (Changeable changed : changes.keySet()) {
			s.append("\t" + changed + ": " + changes.get(changed) + "\n");
		}
		return s.toString();
	}
	
}
