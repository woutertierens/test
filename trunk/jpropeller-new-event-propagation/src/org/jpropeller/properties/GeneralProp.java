package org.jpropeller.properties;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.change.Changeable;

/**
 * The most general type of property.
 * The value of such a prop cannot actually be read or set, but a {@link GeneralProp}
 * can be handled by {@link BeanFeatures}s, and has a name, etc. Essentially this interface
 * may be used by code that simply handles props without any interest in the contents.
 * 
 * In order to read/write the value(s) in a {@link GeneralProp}, it must be referenced
 * as a subinterface of the {@link GeneralProp} interface, e.g. {@link EditableProp} etc.
 * The info ({@link #getInfo()}) of the Prop can however be used as a quicker/easier
 * way of checking than instanceof - where {@link #getInfo()} returns EDITABLE the Prop may
 * be cast to EditableProp, and similarly for the list and map
 * subclasses of {@link GeneralProp}
 * 
 * NOTE: implementations must enforce the rules for {@link Bean}s, {@link BeanFeatures} and {@link Prop}s:
 * 
 * The {@link PropName} returned by {@link #getName()} must have editability matching the {@link Prop} - 
 * that is, a {@link Prop} should not return an {@link PropName} with parametric type for an
 * {@link EditableProp} unless it is an {@link EditableProp}. 
 * If this is done wrongly, it should be caught by compliant implementations where possible.
 * 
 * @param <T>
 * 		The type of value in the property
 */
public interface GeneralProp<T> extends Changeable {

	/**
	 * Get info on the type of the prop.
	 *
	 * @return
	 * 		The prop info
	 */
	public PropInfo getInfo();

	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public GenericPropName<? extends GeneralProp<T>, T> getName();

}