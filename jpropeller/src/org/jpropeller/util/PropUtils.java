package org.jpropeller.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;

/**
 * Utility methods for working with JPropeller
 */
public class PropUtils {

	/**
	 * Find the user-readable, localised name for a {@link GeneralProp}
	 * @param prop
	 * 		The prop
	 * @return
	 * 		The user readable name. This will be localised as far as possible
	 * for the current {@link Locale}, using resource bundles for the class
	 * name. If no resource is found, the string value of the {@link PropName}
	 * will be returned.
	 */
	public static String localisedName(GeneralProp<?> prop) {
		GenericPropName<?, ?> name = prop.getName();
		Bean bean = prop.getBean();
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
		List<Prop<?>> props = new ArrayList<Prop<?>>();
		for (GeneralProp<?> g : b.props().getList()) {
			if (g instanceof Prop<?>) {
				props.add((Prop<?>)g);
			}
		}
		
		return props;
	}
	
}
