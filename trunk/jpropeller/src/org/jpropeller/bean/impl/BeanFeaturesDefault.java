package org.jpropeller.bean.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;

/**
 * Default implementation of {@link BeanFeatures}
 */
public class BeanFeaturesDefault extends ChangeableFeaturesDefault implements MutableBeanFeatures {

	Map<PropName<?>, Prop<?>> map;
	List<Prop<?>> list;
	List<Prop<?>> umList;
	Bean bean;
	
	/**
	 * Create a new {@link BeanFeaturesDefault}
	 * @param bean
	 * 		The {@link Bean} this will provide features for
	 * @param implementation
	 * 		The {@link InternalChangeImplementation} to handle calls
	 * to internalChange
	 */
	public BeanFeaturesDefault(Bean bean, InternalChangeImplementation implementation) {
		super(implementation, bean);
		map = new HashMap<PropName<?>, Prop<?>>(10);
		list = new LinkedList<Prop<?>>();
		umList = Collections.unmodifiableList(list);
		this.bean = bean;
	}

	/**
	 * Create a new {@link BeanFeaturesDefault} with a default response to
	 * internal changes: a non-initial change will be fired, with the same
	 * {@link Change#sameInstances()} state as the incoming change. This handles
	 * the case for a normal bean which only listens to exactly its own props.
	 * @param bean
	 * 		The {@link Bean} this will provide features for
	 */
	protected BeanFeaturesDefault(Bean bean) {
		this(bean, new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change, List<Changeable> initial,
					Map<Changeable, Change> changes) {
				//We will only see incoming changes from our own props, since this is all
				//we listen to, so we just respond with the same basic change as the incoming one
				//(We always use basic change, even if incoming is for a list, etc. since the
				//bean can only change in a basic way)
				return ChangeDefault.instance(
						false,	//Never an initial change - always a consequence of a change to one of our props 
						change.sameInstances()	//Instances are the same (for just this change) if they are for the prop that has changed.
						);
			}
		});
	}
	
	@Override
	public Iterator<Prop<?>> iterator() {
		return list.iterator();
	}
	
	/**
	 * Add a prop to the {@link Bean}
	 * 
	 * @param <P> 		The type of prop
	 * @param <S>		Type of data in the prop
	 * @param prop		The prop
	 * @return			The same prop (for chaining)
	 */
	public <P extends Prop<S>, S> P add(P prop){

		//Sync on the prop and ourselves
		synchronized (prop) {
			synchronized (this) {
				
				//FIXME - odd warning, since getUnsafe returns "P extends GeneralProp<S>"
				Prop<S> existingProp = getUnsafe(prop.getName());
				if (existingProp != null) {
					throw new IllegalArgumentException("Cannot add property '" + prop + "' since there is already a prop '" + existingProp + "' with the same name, '" + prop.getName() + "'");
				}
				
				//Note that we use the prop's own name as its key - hence the
				//parametric type of the Prop value always matches the
				//parametric type of the PropName key - this makes the
				//cast we perform in getProp(name) safe
				map.put(prop.getName(), prop);
				list.add(prop);
				
				//make bean listen internally to the prop
				prop.features().addChangeableListener(bean);
				
				return prop;
			}
		}
	}
		
	//Note that the suppressed warning is safe, since we know that we
	//always have the type P and parametric type <S> of values in the map matching
	//the type of keys, due to the code and types in addProp(prop)
	@SuppressWarnings("unchecked")
	@Override
	public <S> Prop<S> get(PropName<S> name) {
		if (name.isTGeneric()) {
			throw new IllegalArgumentException(
				"Cannot use BeanFeatures.get(PropName) with a " +
				"PropName for a generic value type - type cannot " +
				"be guaranteed. Use getUnsafe(PropName) instead. " +
				"PropName used is " + name);
		}
		return (Prop<S>) map.get(name);
	}
	
	//Note that suppressed warning is NOT entirely safe. We know that we have 
	//retrieved a value that shares its BASIC class with P, but it may not
	//match in terms of its parametric types. We do try to get a match
	//using the string representation of the parametric types, but this is
	//not checked at compiletime. This is in contrast to the safe
	//cast by get(PropName)
	@SuppressWarnings("unchecked")
	@Override
	public <S> Prop<S> getUnsafe(PropName<S> name) {
		return (Prop<S>) map.get(name);
	}

	
	@Override
	public List<Prop<?>> getList() {
		return umList;
	}
	
	@Override
	public Bean getBean() {
		return bean;
	}
	
}
