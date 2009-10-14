package org.jpropeller.reference.impl;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.impl.ClassFilterProp;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * a {@link ClassFilterProp}
 *
 * @param <M> The type of bean value
 */
public class ReferenceWithClassFilter<M> implements Reference<M> {

	MutableBeanFeatures features = Props.getPropSystem().createBeanFeatures(this);
	ClassFilterProp<M> value;
	
	private ReferenceWithClassFilter(ClassFilterProp<M> value) {
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
	 * Create a new reference
	 * 
	 * @param <S>			The type of value in the reference
	 * @param clazz			The class of value in the reference
	 * @param filteredProp	The {@link Prop} to be filtered by
	 * 						this reference.
	 * @return				A new reference
	 */
	public static <S> ReferenceWithClassFilter<S> create(Class<S> clazz, Prop<? super S> filteredProp) {
		PropName<S> name = PropName.create(clazz, "value");
		ClassFilterProp<S> prop = new ClassFilterProp<S>(clazz, name, filteredProp);
		return new ReferenceWithClassFilter<S>(prop);
	}

	/**
	 * Create a new reference, filtering the {@link Reference#value()}
	 * of another {@link Reference}
	 * 
	 * @param <S>			The type of value in the reference
	 * @param clazz			The class of value in the reference
	 * @param filteredRef	The {@link Reference} to be filtered by
	 * 						this reference.
	 * @return				A new reference
	 */
	public static <S> ReferenceWithClassFilter<S> create(Class<S> clazz, Reference<? super S> filteredRef) {
		PropName<S> name = PropName.create(clazz, "value");
		ClassFilterProp<S> prop = new ClassFilterProp<S>(clazz, name, filteredRef.value());
		return new ReferenceWithClassFilter<S>(prop);
	}

	/**
	 * Create a new reference. This method is NOT type safe,
	 * and is provided to allow {@link ReferenceWithClassFilter}
	 * to be used where the type to be filtered to is not 
	 * known at compile-time. For example, you may know that
	 * you have an instance of Number, but not which. In this
	 * case you cannot call the type safe {@link #create(Class, Prop)}
	 * method.
	 * 
	 * NOTE: For this to be sensible, you must know that filteredProp
	 * may sometimes contain an instance of Class clazz. You will
	 * have to cast the returned {@link ReferenceWithClassFilter}
	 * yourself - it is safe to cast it so that the parametric
	 * type is the same as the type of clazz, although obviously
	 * you will only know this by logical reasoning, not be
	 * compiler verification.
	 * 
	 * @param clazz			The class of value in the reference
	 * @param filteredProp	The {@link Prop} to be filtered by
	 * 						this reference.
	 * @return				A new reference
	 */
	//See docs for reasoning behind suppression
	@SuppressWarnings("unchecked")
	public static ReferenceWithClassFilter createUnsafe(Class clazz, Prop filteredProp) {
		PropName name = PropName.create(clazz, "value");
		ClassFilterProp prop = new ClassFilterProp(clazz, name, filteredProp);
		return new ReferenceWithClassFilter(prop);
	}

	/**
	 * Create a new reference, filtering the {@link Reference#value()}
	 * of another {@link Reference}
	 * This method is NOT type safe,
	 * and is provided to allow {@link ReferenceWithClassFilter}
	 * to be used where the type to be filtered to is not 
	 * known at compile-time. For example, you may know that
	 * you have an instance of Number, but not which. In this
	 * case you cannot call the type safe {@link #create(Class, Prop)}
	 * method.
	 * 
	 * NOTE: For this to be sensible, you must know that filteredProp
	 * may sometimes contain an instance of Class clazz. You will
	 * have to cast the returned {@link ReferenceWithClassFilter}
	 * yourself - it is safe to cast it so that the parametric
	 * type is the same as the type of clazz, although obviously
	 * you will only know this by logical reasoning, not be
	 * compiler verification.
	 * 
	 * @param clazz			The class of value in the reference
	 * @param filteredRef	The {@link Reference} to be filtered by
	 * 						this reference.
	 * @return				A new reference
	 */
	//See docs for reasoning behind suppression
	@SuppressWarnings("unchecked")
	public static ReferenceWithClassFilter createUnsafe(Class clazz, Reference filteredRef) {
		PropName name = PropName.create(clazz, "value");
		ClassFilterProp prop = new ClassFilterProp(clazz, name, filteredRef.value());
		return new ReferenceWithClassFilter(prop);
	}
}
