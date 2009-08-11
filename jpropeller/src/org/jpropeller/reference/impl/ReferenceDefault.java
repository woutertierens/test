package org.jpropeller.reference.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;

/**
 * Default implementation of {@link Reference} just accepts an
 * external {@link Prop} for the {@link #value()}
 *
 * @param <T>		Type of referenced value
 */
public class ReferenceDefault<T> extends BeanDefault implements Reference<T> {

	private final Prop<T> value;
	
	/**
	 * Create a {@link ReferenceDefault}
	 * @param value		The referenced value prop
	 * @param <T> 		The type of referenced value
	 * @return 			A new {@link ReferenceDefault}
	 */
	public static <T> ReferenceDefault<T> create(Prop<T> value) {
		return new ReferenceDefault<T>(value);
	}
	
	/**
	 * Create a {@link ReferenceDefault}
	 * @param value		The referenced value prop
	 */
	private ReferenceDefault(Prop<T> value) {
		super();
		this.value = addProp(value);
	}

	@Override
	public Prop<T> value() { return value; }

}
