package org.jpropeller.properties.calculated.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.concurrency.CancellableResponse;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.task.Task;
import org.jpropeller.util.Source;

/**
 * Simple util to assist in building {@link Calculation}s that
 * produce a {@link List}
 * 
 * Used as:
 * <code>
 * 		BuildListCalculation.on(a, b, c).returning(source);
 * </code>
 * 
 * Where a,b,c are {@link Changeable} inputs for the calculation,
 * and source is a {@link Source} always giving the current result based
 * on just those {@link Changeable} inputs.
 * 
 * @param <T> The type of contents of the list result produced
 */
public class BuildListCalculation<T> {

	private final Set<Changeable> srcSet;
	
	/**
	 * Creates a new calculation on a list of inputs.
	 * @param inputs	The list of inputs (sources) that will be watched.
	 */
	private BuildListCalculation(Changeable ... inputs) {
		Set<Changeable> srcSetM = new IdentityHashSet<Changeable>();
		for(Changeable ch : inputs) {
			srcSetM.add(ch);
		}
		srcSet = Collections.unmodifiableSet(srcSetM);		
	}

	/**
	 * Make a builder for a {@link Calculation} operating on given inputs (sources).
	 * Calling {@link #returning(Source)} on this
	 * builder will produce a {@link Calculation}
	 * @param inputs		The inputs (sources) of data for the {@link Calculation}
	 * @return				A {@link BuildListCalculation}
     * @param <T> 			The type of result produced
	 */
	public static <T> BuildListCalculation<T> on(Changeable... inputs) {
		return new BuildListCalculation<T>(inputs);
	}
	
	/**
	 * Produce a {@link Task}, with the sources provided to
	 * {@link #on(Changeable...)}, and the {@link CancellableResponse}
	 * provided to this method.
	 * @param resultSource		The {@link Source} of results of the {@link Calculation}
	 * @return					The {@link Calculation}
	 */
	public Calculation<List<T>> returning(final Source<List<T>> resultSource) {
		return new Calculation<List<T>>() {
			@Override
			public List<T> calculate() {
				return resultSource.get();
			}
			@Override
			public Set<? extends Changeable> getSources() {
				return srcSet;
			}
			
		};
	}
	
}
