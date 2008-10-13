package org.jpropeller.path.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.BeanPathIterator;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;

/**
 * A means of building a {@link BeanPath} in a typesafe way
 *
 * @param <R>
 * 		The initial type in the chain
 * @param <D>
 * 		The final type in the chain
 */
public class BeanPathBuilder<R extends Bean, D extends Bean> {

	//We use raw transformers so we can apply them to anything - we make
	//sure we only ever have valid types by the way we build instances
	//of this class
	@SuppressWarnings("unchecked")
	private LinkedList<Transformer> transformers;

	/**
	 * Create a new {@link BeanPathBuilder}, starting with a single
	 * initial transform
	 * @param initial
	 * 		The initial transform
	 */
	//See first suppression
	@SuppressWarnings("unchecked")
	private BeanPathBuilder(Transformer<R, GenericProp<D>> initial) {
		transformers = new LinkedList<Transformer>();
		transformers.add(initial);
	}

	/**
	 * Create a builder, which goes first via a specified transformer
	 * @param <R>
	 * 		The type of the root of the path to be built
	 * @param <D>
	 * 		The type of the next step on the path
	 * @param initial
	 * 		The {@link Transformer} to take the initial step of the path
	 * @return
	 * 		A new {@link BeanPathBuilder}
	 */
	public static <R extends Bean, D extends Bean> BeanPathBuilder<R, D> createVia(Transformer<R, GenericProp<D>> initial) {
		return new BeanPathBuilder<R, D>(initial);
	}

	/**
	 * Create a builder, which goes first via a specified transformer
	 * @param <R>
	 * 		The type of the root of the path to be built
	 * @param <P>
	 * 		The type of the {@link Prop} at the end of the path
	 * @param <D>
	 * 		The type of value in the {@link Prop} at the end of the path
	 * @param lastTransform
	 * 		The {@link Transformer} to take the single step of the path
	 * @return
	 * 		A new {@link BeanPath}
	 */
	//The list is empty and will not be read or written - type does not matter
	@SuppressWarnings("unchecked")
	public static <R extends Bean, P extends GenericProp<D>, D> NewBeanPathDefault<R, P, D> createTo(Transformer<D, ? extends P> lastTransform) {
		return new NewBeanPathDefault<R, P, D>(new LinkedList<Transformer>(), lastTransform);
	}

	/**
	 * Create a {@link BeanPathBuilder} with a specified list of transformers.
	 * Please make sure that the list starts with a {@link Transformer} from
	 * S to something, and ends with a {@link Transformer} from something to T,
	 * and also that all the {@link Transformer}s (except the last) produce the type
	 * required by the next in the list.
	 * @param transformers
	 */
	//See first suppression
	@SuppressWarnings("unchecked")
	private BeanPathBuilder(LinkedList<Transformer> transformers) {
		this.transformers = transformers;
	}

	/**
	 * Make a new {@link BeanPathBuilder} that adds another {@link Transformer}
	 * to the end of the chain performed by this {@link BeanPathBuilder}
	 * @param <E>
	 * 		The destination type of the {@link BeanPathBuilder} we will produce
	 * @param nextTransform
	 * 		The {@link Transformer} to add to this one
	 * @return
	 * 		A {@link Transformer} that applies the same {@link Transformer}s, in the same
	 * order, as this {@link Transformer}, then applies the extra {@link Transformer} on
	 * the end
	 */
	//See first suppression
	@SuppressWarnings("unchecked")
	public <E extends Bean> BeanPathBuilder<R, E> via(Transformer<D, GenericProp<E>> nextTransform) {
		LinkedList<Transformer> newList = new LinkedList<Transformer>(transformers);
		newList.add(nextTransform);
		return new BeanPathBuilder<R, E>(newList);
	}
	
	/**
	 * Produce a {@link BeanPath} using the {@link PropName}s used to create this
	 * object, then the last name specified
	 * @param <P>
	 * 		The type of prop at the end of the path 
	 * @param <Q> 
	 * 		The type of value in the prop at the end of the path
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public <P extends GenericProp<Q>, Q> BeanPath<R, P, Q> to(Transformer<D, ? extends P> lastTransform) {
		return new NewBeanPathDefault<R, P, Q>(transformers, lastTransform);
	}
	
	
	
	
	//#######################################################
	
	/**
	 * The type if {@link BeanPath} produced by this builder
	 * @param <R>
	 * 		The type of the root of the patha 
	 * @param <P>
	 * 		The type of {@link Prop} at the end of the path
	 * @param <D>
	 * 		The type of value in the {@link Prop} at the end of the path
	 */
	private static class NewBeanPathDefault<R extends Bean, P extends GenericProp<D>, D> implements BeanPath<R, P, D> {

		Transformer<?, ? extends P> lastTransform;
		
		//We rely on valid Transformer types when created
		@SuppressWarnings("unchecked")
		List<Transformer> transforms;
		
		/**
		 * Create a new {@link BeanPath}
		 * @param transforms
		 * 		The transforms
		 * @param lastTransform
		 * 		The last transform
		 */
		//See first @SuppressWarnings("unchecked")
		@SuppressWarnings("unchecked")
		private NewBeanPathDefault(List<Transformer> transforms,
				Transformer<?, ? extends P> lastTransform) {
			super();
			this.transforms = transforms;
			this.lastTransform = lastTransform;
		}

		@Override
		public BeanPathIterator<P, D> iteratorFrom(R root) {
			return new NewBeanPathIteratorDefault<P, D>(root, transforms, lastTransform);
		}
		
	}

	
	
	
	//#######################################################
	
	/**
	 * {@link BeanPathIterator} for {@link BeanPath}s built using this builder
	 *
	 * @param <P>
	 * 		The type of {@link Prop} at the end of the iteration
	 * @param <D>
	 * 		The type of data in that {@link Prop}
	 */
	private static class NewBeanPathIteratorDefault<P extends GenericProp<D>, D> implements BeanPathIterator<P, D> {

		Bean current;
		
		//We rely on anything constructing this class to have the correct type
		@SuppressWarnings("unchecked")
		Transformer lastTransform;
		
		//See first suppression
		@SuppressWarnings("unchecked")
		private Iterator<Transformer> iterator;
		P finalProp;

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
		@SuppressWarnings("unchecked")
		private NewBeanPathIteratorDefault(Bean root, List<Transformer> transforms,
				Transformer lastTransform) {
			super();
			this.lastTransform = lastTransform;
			current = root;
			iterator = transforms.iterator(); 
		}

		//Again relying on correct construction
		@SuppressWarnings("unchecked")
		@Override
		public P finalProp() {
			//We can only get the final value by following the final transformer from
			//the last value reached by iterator
			if (hasNext()) {
				throw new NoSuchElementException("Cannot get finalProp() value until the iterator has provided all values");
			}
			
			//If we don't know the final prop yet, find it by following last transform
			if (finalProp == null) {
				finalProp = (P)lastTransform.transform(current);
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
		public GeneralProp<?> next() {
			
			//If the current bean is null, return null
			if (current == null) return null;
			
			//Use the next transform to get from our current bean, to the desired prop
			GenericProp<?> nextProp = (GenericProp<?>)iterator.next().transform(current);
			
			//If the next prop is null, then null current bean and return null
			if (nextProp == null) {
				current = null;
				return null;
			}
			
			//Now our current bean will be the value of the prop
			current = (Bean)nextProp.get();
			
			System.out.println("prop " + nextProp + " led to " + current);
			
			//The iterator produces the prop - not the bean
			return nextProp;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove() from BeanPathIterator");
		}
		
	}
}
