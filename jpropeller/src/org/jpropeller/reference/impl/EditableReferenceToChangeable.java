package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.ObservableMap;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.impl.EditableChangeablePropDefault;
import org.jpropeller.properties.changeable.impl.GenericEditableChangeablePropDefault;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * an {@link EditableProp} containing some type of {@link Bean} value
 *
 * @param <M>
 * 		The type of bean value
 */
public class EditableReferenceToChangeable<M extends Changeable> implements Reference<M> {

	MutableBeanFeatures features = Props.getPropSystem().createBeanFeatures(this); 
	GenericEditableProp<M> model;
	
	private EditableReferenceToChangeable(GenericEditableProp<M> model) {
		this.model = features.add(model);
	}

	@Override
	public BeanFeatures features() {
		return features;
	}

	@Override
	public GenericEditableProp<M> value() {
		return model;
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
	public static <S extends Changeable> EditableReferenceToChangeable<S> create(Class<S> clazz, S value) {
		return new EditableReferenceToChangeable<S>(EditableChangeablePropDefault.create("model", clazz, value));
	}

	/**
	 * Create a new reference for an {@link ObservableList} value
	 * @param <S>
	 * 		The type of value in the {@link ObservableList}
	 * @param clazz
	 * 		The class of value in the {@link ObservableList}
	 * @param value 
	 * 		The initially referenced {@link ObservableList}
	 * @return
	 * 		A new reference
	 */
	public static <S extends Changeable> EditableReferenceToChangeable<ObservableList<S>> createObservableListReference(Class<S> clazz, ObservableList<S> value) {
		GenericEditableChangeablePropDefault<ObservableList<S>> prop = GenericEditableChangeablePropDefault.createObservableList("model", clazz, value);
		return new EditableReferenceToChangeable<ObservableList<S>>(prop);
	}
	
	/**
	 * Create a new reference for an {@link ObservableMap} value
	 * @param <K> 
	 * 		The type of key in the {@link ObservableMap}
	 * @param <S>
	 * 		The type of value in the {@link ObservableMap}
	 * @param clazz
	 * 		The class of value in the {@link ObservableMap}
	 * @param value 
	 * 		The initially referenced {@link ObservableMap}
	 * @return
	 * 		A new reference
	 */
	public static <K, S extends Changeable> EditableReferenceToChangeable<ObservableMap<K, S>> createObservableMapReference(Class<S> clazz, ObservableMap<K, S> value) {
		GenericEditableChangeablePropDefault<ObservableMap<K, S>> prop = GenericEditableChangeablePropDefault.createObservableMap("model", clazz, value);
		return new EditableReferenceToChangeable<ObservableMap<K, S>>(prop);
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
	public static <S extends Bean> EditableReferenceToChangeable<S> create(Class<S> clazz) {
		return create(clazz, null);
	}

}
