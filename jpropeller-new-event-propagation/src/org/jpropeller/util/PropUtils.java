package org.jpropeller.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;

/**
 * Utility methods for working with JPropeller
 */
public class PropUtils {

	/**
	 * Find the user-readable, localised name for a {@link GeneralProp} of a {@link Bean}
	 * @param bean
	 * 		The bean containing the prop
	 * @param prop
	 * 		The prop
	 * @return
	 * 		The user readable name. This will be localised as far as possible
	 * for the current {@link Locale}, using resource bundles for the class
	 * name. If no resource is found, the string value of the {@link PropName}
	 * will be returned.
	 */
	public static String localisedName(Bean bean, GeneralProp<?> prop) {
		GenericPropName<?, ?> name = prop.getName();
		try {
			ResourceBundle res = ResourceBundle.getBundle(bean.getClass().getCanonicalName());
			return res.getString(name.getString());
		} catch (MissingResourceException mre) {
			return name.getString(); 
		}
	}
	
	/**
	 * Build a list of the {@link Prop}s in the bean - this
	 * filters out {@link GeneralProp}s that are not {@link Prop}s - 
	 * i.e. it gives you a list of properties with getters.
	 * @param b
	 * 		The bean from which to get names of {@link Prop}s
	 * @return
	 * 		A list of the names
	 */
	public static List<Prop<?>> buildPropsList(Bean b) {
		List<Prop<?>> props = new LinkedList<Prop<?>>();
		for (GeneralProp<?> g : b.features().getList()) {
			if (g instanceof Prop<?>) {
				props.add((Prop<?>)g);
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
