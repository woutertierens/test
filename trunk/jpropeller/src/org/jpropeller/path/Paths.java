package org.jpropeller.path;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.transformer.BeanPathVia;

/**
 * Utility methods for working with paths
 */
public class Paths {
	
	/**
	 * The single instance of {@link BeanPathVia} we will cast and return in response
	 * to all requests for different parametric types
	 */
	private final static BeanPathVia<?, ?> INSTANCE = Paths.<Bean>internalModelToValue();

	/**
	 * Make a {@link BeanPathVia} from a {@link Reference} to its value
	 * 
	 * @param <T>		The type of value in the {@link Reference}s value
	 * @return			{@link BeanPathVia}
	 */
	//We can safely cast the raw INSTANCE to a specific instance, since we know from the
	//internalModelToValue() method below (which has no warnings) that the parametric
	//types in this method signature are valid. Since all instances of BeanPathVia are
	//the same (they have no internal state, and only differ by erased parametric types),
	//it is valid to cast any instance to any other.
	@SuppressWarnings("unchecked")
	public final static <T extends Bean> BeanPathVia<Reference<T>, T> modelToValue() {
		return (BeanPathVia<Reference<T>, T>)INSTANCE;
	}
	
	/**
	 * Make a {@link BeanPathVia} from a {@link Reference} to its value
	 * This form takes the reference - this is just provided as a convenient
	 * alternative to explicitly specifying the type T
	 * 
	 * @param reference	The {@link Reference} - note that the {@link BeanPathVia} will
	 * 					work with any {@link Reference} of the correct parametric type.
	 * @param <T>		The type of value in the {@link Reference}s value
	 * @return			{@link BeanPathVia}
	 */	
	public final static <T extends Bean> BeanPathVia<Reference<T>, T> modelToValue(Reference<T> reference) {
		return modelToValue();
	}
	
	
	/**
	 * This is what the method should look like, creating a new instance
	 * of {@link BeanPathVia} each time one is requested.
	 * However because of type erasure we can instead return the same
	 * {@link BeanPathVia} instance each time, just forcing it to the
	 * right type
	 * @param <T>
	 * @return
	 */
	private final static <T extends Bean> BeanPathVia<Reference<T>, T> internalModelToValue() { 
		return new BeanPathVia<Reference<T>, T>() {
			@Override
			public Prop<T> transform(Reference<T> ref) {
				return ref.value();
			}
		};
	}
		
	
}
