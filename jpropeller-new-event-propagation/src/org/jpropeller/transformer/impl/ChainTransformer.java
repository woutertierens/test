package org.jpropeller.transformer.impl;

import java.util.Iterator;
import java.util.LinkedList;

import org.jpropeller.transformer.Transformer;

/**
 * A {@link Transformer} that can be used to produce more {@link Transformer}s
 * by chaining them together.
 *
 * @param <M>
 * 		A type which the final type in the chain will always extend 
 * (for this {@link ChainTransformer} and all {@link ChainTransformer}s built using it) 
 * @param <S>
 * 		The initial type in the chain
 * @param <T>
 * 		The final type in the chain
 */
public class ChainTransformer<M, S, T extends M> implements Transformer<S, T> {

	//We use raw transformers so we can apply them to anything - we make
	//sure we only ever have valid types by the way we build instances
	//of this class
	@SuppressWarnings("unchecked")
	private LinkedList<Transformer> transformers;

	/**
	 * Create a new {@link ChainTransformer}, starting with a single
	 * initial transform
	 * @param initial
	 * 		The initial transform
	 */
	//See first suppression
	@SuppressWarnings("unchecked")
	public ChainTransformer(Transformer<S, T> initial) {
		transformers = new LinkedList<Transformer>();
		transformers.add(initial);
	}

	/**
	 * Create a {@link ChainTransformer} with a specified list of transformers.
	 * Please make sure that the list starts with a {@link Transformer} from
	 * S to something, and ends with a {@link Transformer} from something to T,
	 * and also that all the {@link Transformer}s (except the last) produce the type
	 * required by the next in the list.
	 * @param transformers
	 */
	//See first suppression
	@SuppressWarnings("unchecked")
	private ChainTransformer(LinkedList<Transformer> transformers) {
		this.transformers = transformers;
	}
	
	//See first suppression
	@SuppressWarnings("unchecked")
	@Override
	public T transform(S s) {
		Object o = s;
		for (Transformer transformer : transformers) {
			o = transformer.transform(o);
		}
		
		//We know we end up with a T, because of how we are built
		return (T)o;
	}

	/**
	 * Make a new {@link ChainTransformer} that adds another {@link Transformer}
	 * to the end of the chain performed by this {@link ChainTransformer}
	 * @param <U>
	 * 		The destination type of the {@link ChainTransformer} we will produce
	 * @param nextTransform
	 * 		The {@link Transformer} to add to this one
	 * @return
	 * 		A {@link Transformer} that applies the same {@link Transformer}s, in the same
	 * order, as this {@link Transformer}, then applies the extra {@link Transformer} on
	 * the end
	 */
	//See first suppression
	@SuppressWarnings("unchecked")
	public <U extends M> ChainTransformer<M, S, U> then(Transformer<T, U> nextTransform) {
		LinkedList<Transformer> newList = new LinkedList<Transformer>(transformers);
		newList.add(nextTransform);
		return new ChainTransformer<M, S, U>(newList);
	}
	
	/**
	 * Return an iterator which will return the result of transforming a
	 * root value by the first transform in the chain, then the result of transforming
	 * THAT by the second transform in the chain, and so on.
	 * @param root
	 * 		The root value - input to the first {@link Transformer} in the chain
	 * @return
	 * 		An iterator over the stages of transformation
	 */
	public Iterator<M> iterateFrom(S root) {
		return new ChainIterator<M>(root, transformers);
	}
	
	private class ChainIterator<N> implements Iterator<N> {
		
		private Object current;
		//See first suppression
		@SuppressWarnings("unchecked")
		private Iterator<Transformer> iterator;

		//See first suppression
		@SuppressWarnings("unchecked")
		private ChainIterator(Object root, LinkedList<Transformer> transformers) {
			super();
			this.current = root;
			this.iterator = transformers.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		//See first suppression
		@SuppressWarnings("unchecked")
		public N next() {
			current = iterator.next().transform(current);
			return (N)current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove");
		}
		
	}
}
