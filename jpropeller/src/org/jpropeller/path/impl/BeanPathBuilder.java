package org.jpropeller.path.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.BeanPathIterator;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.transformer.impl.BeanToPropTransformer;
import org.jpropeller.util.GeneralUtils;

/**
 * A means of building a {@link BeanPath} in a typesafe way, using
 * either {@link Transformer}s or {@link PropName}s for each stage
 * of the path (they can be intermixed as required)
 *
 * @param <R>		The initial type in the chain
 * @param <D>		The final type in the chain
 */
public class BeanPathBuilder<R extends Bean, D extends Bean> {

	private final static Logger logger = GeneralUtils.logger(BeanPathBuilder.class);
	
	//We use raw transformers so we can apply them to anything - we make
	//sure we only ever have valid types by the way we build instances
	//of this class
	@SuppressWarnings("rawtypes")
	private LinkedList<Transformer> transformers;

	/**
	 * Create a new {@link BeanPathBuilder}, starting with a single
	 * initial transform
	 * 
	 * @param initial		The initial transform
	 */
	//See first suppression
	@SuppressWarnings("rawtypes")
	private BeanPathBuilder() {
		transformers = new LinkedList<Transformer>();
	}

	/**
	 * Create a builder, which has no {@link Transformer}s yet
	 * 
	 * @param <R>		The type of the root of the path to be built
	 * @return			A new {@link BeanPathBuilder}
	 */
	public static <R extends Bean> BeanPathBuilder<R, R> create() {
		return new BeanPathBuilder<R, R>();
	}
	
	/**
	 * Create a {@link BeanPathBuilder} with a specified list of transformers.
	 * Please make sure that the list starts with a {@link Transformer} from
	 * S to something, and ends with a {@link Transformer} from something to T,
	 * and also that all the {@link Transformer}s (except the last) produce the type
	 * required by the next in the list.
	 * 
	 * @param transformers		List of transformers
	 */
	//See first suppression
	@SuppressWarnings("rawtypes")
	private BeanPathBuilder(LinkedList<Transformer> transformers) {
		this.transformers = transformers;
	}

	/**
	 * Make a new {@link BeanPathBuilder} that adds another {@link Transformer}
	 * to the end of the chain performed by this {@link BeanPathBuilder}
	 * 
	 * @param <E>			The destination type of the {@link BeanPathBuilder} we will produce
	 * @param nextTransform	The {@link Transformer} to add to this one
	 * @return				A {@link Transformer} that applies the same {@link Transformer}s, 
	 * 						in the same order, as this {@link Transformer}, then applies the 
	 * 						extra {@link Transformer} on the end
	 */
	//We make sure that we only accept a Transformer that will take any D as an input (since
	//it accepts <? super D>. We then make sure it produces something with a getter for
	//type E  - <? extends Prop<E>>. In this way we make sure that each Transformer
	//must provide data that the next will accept
	@SuppressWarnings("rawtypes")
	public <E extends Bean> BeanPathBuilder<R, E> via(Transformer<? super D, ? extends Prop<E>> nextTransform) {
		LinkedList<Transformer> newList = new LinkedList<Transformer>(transformers);
		newList.add(nextTransform);
		return new BeanPathBuilder<R, E>(newList);
	}

	/**
	 * Make a new {@link BeanPathBuilder} that adds another {@link Transformer}
	 * to the end of the chain performed by this {@link BeanPathBuilder}. The
	 * {@link Transformer} is transforms from a {@link Bean} to a named property
	 * of that bean - it is created automatically from the specified {@link PropName}
	 * 
	 * @param <E>		The destination type of the {@link BeanPathBuilder} we will produce
	 * @param nextName	The {@link PropName} used to look up a prop in the {@link Transformer}
	 * @return			A {@link Transformer} that applies the same 
	 * 					{@link Transformer}s, in the same order, as this 
	 * 					{@link Transformer}, then applies the extra {@link Transformer} on
	 * 					the end
	 */
	//We make sure that we only accept a Transformer that will take any D as an input (since
	//it accepts <? super D>. We then make sure it produces something with a getter for
	//type E  - <? extends Prop<E>>. In this way we make sure that each Transformer
	//must provide data that the next will accept
	@SuppressWarnings("rawtypes")
	public <E extends Bean> BeanPathBuilder<R, E> via(PropName<E> nextName) {
		Transformer<? super D, ? extends Prop<E>> nextTransform = new BeanToPropTransformer<E>(nextName);
		LinkedList<Transformer> newList = new LinkedList<Transformer>(transformers);
		newList.add(nextTransform);
		return new BeanPathBuilder<R, E>(newList);
	}

	/**
	 * Produce a {@link BeanPath} using the {@link Transformer}s used to create this
	 * builder, then the last name specified
	 * 
	 * @param <Q> 			The type of value in the prop at the end of the path
	 * @param lastTransform The last {@link Transformer} in the path
	 * @return				The path itself
	 */
	public <Q> BeanPath<R, Q> to(Transformer<? super D, ? extends Prop<Q>> lastTransform) {
		return new BeanPathDefault<R, Q>(transformers, lastTransform);
	}

	/**
	 * Produce a {@link BeanPath} using the {@link PropName}s used to create this
	 * object, then the last name specified
	 * 
	 * @param <Q>		The type of value in the prop at the end of the path
	 * @param lastName	The last {@link PropName} in the path
	 * @return			The path itself
	 */
	public <Q> BeanPath<R, Q> to(PropName<Q> lastName) {
		return new BeanPathDefault<R, Q>(transformers, new BeanToPropTransformer<Q>(lastName));
	}
	
	//#######################################################
	
	/**
	 * The type if {@link BeanPath} produced by this builder
	 * 
	 * @param <R>		The type of the root of the path
	 * @param <P>		The type of {@link Prop} at the end of the path
	 * @param <D>		The type of value in the {@link Prop} at the end of the path
	 */
	private static class BeanPathDefault<R extends Bean, D> implements BeanPath<R, D> {

		Transformer<?, ? extends Prop<D>> lastTransform;
		
		//We rely on valid Transformer types when created
		@SuppressWarnings("rawtypes")
		List<Transformer> transforms;
		
		/**
		 * Create a new {@link BeanPath}
		 * @param transforms
		 * 		The transforms
		 * @param lastTransform
		 * 		The last transform
		 */
		//See first @SuppressWarnings("unchecked")
		@SuppressWarnings("rawtypes")
		private BeanPathDefault(List<Transformer> transforms,
				Transformer<?, ? extends Prop<D>> lastTransform) {
			super();
			this.transforms = transforms;
			this.lastTransform = lastTransform;
			
			for (Transformer t : transforms) {
				if (t == null) {
					logger.log(Level.SEVERE, "Null transform in bean path", new RuntimeException("Exception to show stack trace"));
				}
			}
		}

		@Override
		public BeanPathIterator<D> iteratorFrom(R root) {
			return new BeanPathIteratorDefault<D>(root, transforms, lastTransform);
		}
		
	}

	
	
	
	//#######################################################
	
	/**
	 * {@link BeanPathIterator} for {@link BeanPath}s built using this builder
	 *
	 * @param <D>		The type of data in that {@link Prop}
	 */
	private static class BeanPathIteratorDefault<D> implements BeanPathIterator<D> {

		Bean current;
		
		//We rely on anything constructing this class to have the correct type
		@SuppressWarnings("rawtypes")
		Transformer lastTransform;
		
		//See first suppression
		@SuppressWarnings("rawtypes")
		private Iterator<Transformer> iterator;
		Prop<D> finalProp;

		/**
		 * Create a new iterator
		 * @param root
		 * 		Root bean for the iteration
		 * @param transforms
		 * 		The transforms to follow first
		 * @param lastTransform
		 * 		The last transform to follow
		 */
		//We use raw transformers to avoid warnings - we know the transformers follow through correctly
		//since BeanPathBuilder enforces this as it is built
		@SuppressWarnings("rawtypes")
		private BeanPathIteratorDefault(Bean root, List<Transformer> transforms,
				Transformer lastTransform) {
			super();
			this.lastTransform = lastTransform;
			current = root;
			iterator = transforms.iterator(); 
		}

		//Again relying on correct construction
		@SuppressWarnings("unchecked")
		@Override
		public Prop<D> finalProp() {
			//We can only get the final value by following the final transformer from
			//the last value reached by iterator
			if (hasNext()) {
				throw new NoSuchElementException("Cannot get finalProp() value until the iterator has provided all values");
			}
			
			//If the current bean is null, return null
			if (current == null) {
				return null;
			}
			
			//If we don't know the final prop yet, find it by following last transform
			if (finalProp == null) {
				finalProp = (Prop<D>)lastTransform.transform(current);
			}
			
			return finalProp;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		//We know that we are constructed with valid Transformers to give the correct types at each step
		@SuppressWarnings("unchecked")
		@Override
		public Prop<?> next() {
			
			//If the current bean is null, return null
			if (current == null) return null;
			
			//Use the next transform to get from our current bean, to the desired prop
			Prop<?> nextProp = (Prop<?>)iterator.next().transform(current);
			
			//If the next prop is null, then null current bean and return null
			if (nextProp == null) {
				current = null;
				return null;
			}
			
			//Now our current bean will be the value of the prop
			current = (Bean)nextProp.get();
			
			//The iterator produces the prop - not the bean
			return nextProp;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove() from BeanPathIterator");
		}
		
	}
}
