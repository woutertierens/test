package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.map.impl.MapPropDefault;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * an {@link Prop} containing some type of {@link Changeable} value
 *
 * @param <M>
 * 		The type of bean value
 */
public class ReferenceToChangeable<M extends Changeable> implements Reference<M> {

	MutableBeanFeatures features = Props.getPropSystem().createBeanFeatures(this); 
	Prop<M> value;
	
	private ReferenceToChangeable(Prop<M> value) {
		this.value = features.add(value);
	}

	@Override
	public BeanFeatures features() {
		return features;
	}

	@Override
	public Prop<M> value() {
		return value;
	}
	
	/**
	 * Create a new reference for a {@link Changeable} value
	 * @param <S>
	 * 		The type of {@link Changeable}
	 * @param clazz
	 * 		The class of {@link Changeable}
	 * @param value
	 * 		The initial value in the model property
	 * @return
	 * 		A new reference
	 */
	public static <S extends Changeable> ReferenceToChangeable<S> create(Class<S> clazz, S value) {
		return new ReferenceToChangeable<S>(ChangeablePropDefault.editable(clazz, "value", value));
	}

	/**
	 * Create a new reference for an {@link CList} value
	 * @param <S>
	 * 		The type of value in the {@link CList}
	 * @param clazz
	 * 		The class of value in the {@link CList}
	 * @param value 
	 * 		The initially referenced {@link CList}
	 * @return
	 * 		A new reference
	 */
	public static <S extends Changeable> ReferenceToChangeable<CList<S>> createObservableListReference(Class<S> clazz, CList<S> value) {
		Prop<CList<S>> prop = ListPropDefault.editable(clazz, "value", value);
		return new ReferenceToChangeable<CList<S>>(prop);
	}
	
	/**
	 * Create a new reference for an {@link CMap} value
	 * @param <K> 
	 * 		The type of key in the {@link CMap}
	 * @param <S>
	 * 		The type of value in the {@link CMap}
	 * @param keyClass
	 * 		The class of key in the {@link CMap}
	 * @param valueClass
	 * 		The class of value in the {@link CMap}
	 * @param value 
	 * 		The initially referenced {@link CMap}
	 * @return
	 * 		A new reference
	 */
	public static <K, S extends Changeable> ReferenceToChangeable<CMap<K, S>> createObservableMapReference(Class<K> keyClass, Class<S> valueClass, CMap<K, S> value) {
		Prop<CMap<K, S>> prop = MapPropDefault.editable(keyClass, valueClass, "value", value);
		return new ReferenceToChangeable<CMap<K, S>>(prop);
	}
	
	/**
	 * Create a new reference for a {@link Changeable} model with an initial
	 * null value
	 * @param <S>
	 * 		The type of {@link Changeable}
	 * @param clazz
	 * 		The class of {@link Changeable}
	 * @return
	 * 		A new reference
	 */
	public static <S extends Bean> ReferenceToChangeable<S> create(Class<S> clazz) {
		return create(clazz, null);
	}

}
