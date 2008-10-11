package org.jpropeller.properties;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.map.HasPropMap;

/**
 * The most general type of property.
 * The value of such a prop cannot actually be read or set, but a {@link GeneralProp}
 * can be handled by {@link PropMap}s, and has a name, etc. Essentially this interface
 * may be used by code that simply handles props without any interest in the contents - 
 * as is the case (for example) for {@link PropMap}s etc.
 * 
 * In order to read/write the value(s) in a {@link GeneralProp}, it must be referenced
 * as a subinterface of the {@link GeneralProp} interface, e.g. {@link EditableProp} etc.
 * The info ({@link #getInfo()}) of the Prop can however be used as a quicker/easier
 * way of checking than instanceof - where {@link #getInfo()} returns EDITABLE the Prop may
 * be cast to EditableProp, and similarly for the list and map
 * subclasses of {@link GeneralProp}
 * 
 * NOTE: implementations must enforce the rules for {@link Bean}s, {@link PropMap} and {@link Prop}s:
 * 
 * A Prop has a {@link PropMap} it belongs to - once it belongs to a {@link PropMap}, it may never
 * be moved to a different {@link PropMap}. Hence {@link #props()} may return null, but when
 * it first returns a non-null value, it must always return the same value from
 * then onwards.
 * 
 * The {@link PropName} returned by {@link #getName()} must have editability matching the {@link Prop} - 
 * that is, a {@link Prop} should not return an {@link PropName} with parametric type for an
 * {@link EditableProp} unless it is an {@link EditableProp}. 
 * If this is done wrongly, it should be caught by compliant {@link PropMap}s, which 
 * will not allow non-compliant {@link Prop}s to be added.   
 * 
 * @param <T>
 * 		The type of value in the property
 */
public interface GeneralProp<T> extends HasPropMap {

	/**
	 * Get info on the type of the prop.
	 *
	 * @return
	 * 		The prop info
	 */
	public abstract PropInfo getInfo();

	/**
	 * The name of the prop
	 * This is used in the {@link PropMap} to look up {@link Prop}s via {@link PropMap#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public abstract GenericPropName<? extends GeneralProp<T>, T> getName();

	/**
	 * The {@link PropMap} to which this {@link Prop} belongs - it can only belong to one
	 * {@link PropMap}, and to one {@link Bean}
	 * @return
	 * 		The {@link PropMap} to which this Prop belongs
	 */
	public abstract PropMap props();

	/**
	 * The Bean to which this Prop belongs - this is a convenience method
	 * and MUST return the same value as {@link PropMap#getBean()} called on
	 * the result of {@link #props()}, or null
	 * if {@link #props()} returns null
	 * @return
	 * 		The Bean to which this Prop belongs
	 */
	public abstract Bean getBean();

	/**
	 * Set the {@link PropMap} to which this {@link Prop} belongs. Note that
	 * this method must only work once - if it is called after
	 * a {@link PropMap} is already set, it will throw an {@link IllegalArgumentException}
	 * @param map
	 * 		The set the prop will now belong to
	 */
	public abstract void setPropMap(PropMap map);

}