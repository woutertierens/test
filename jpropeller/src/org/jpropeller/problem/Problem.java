package org.jpropeller.problem;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;

/**
 * A {@link Problem} gives a description, source and
 * severity of a problem encountered in an item of data. 
 */
public interface Problem extends Bean {

	/**
	 * Gives the severity of the problem
	 */
	public enum ProblemSeverity {
		/**
		 * {@link ProblemSeverity#INFO} indicates a possibility
		 * of improvement, a hint or tip.
		 * The data is still valid, but might work better if changed.
		 */
		INFO,
		
		/**
		 * {@link ProblemSeverity#WARNING} indicates a problem
		 * that may or may not cause the data to be invalid or fail, 
		 * or not achieve the desired results. The data
		 * may still be accepted, but should almost certainly be changed.
		 */
		WARNING,
		
		/**
		 * {@link ProblemSeverity#ERROR} indicates a problem that
		 * will cause the data to be invalid or fail - in fact no
		 * attempt will be made to use it. The data must be changed
		 * in order to be accepted.
		 */
		ERROR
	}
	
	/**
	 * The severity of the problem
	 * 
	 * @return		{@link ProblemSeverity}
	 */
	Prop<ProblemSeverity> severity();

	/**
	 * A description of the problem in
	 * a human readable form.
	 * 
	 * @return		Problem description
	 */
	Prop<String> description();

	/**
	 * The location of the problem in a
	 * human readable form.
	 * 
	 * @return		Problem location		
	 */
	Prop<String> location();
	
}
