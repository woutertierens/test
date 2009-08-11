package org.jpropeller.problem.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.problem.Problem;
import org.jpropeller.properties.Prop;

/**
 * Default immutable implementation of {@link Problem}
 */
public class ProblemDefault extends BeanDefault implements Problem {

	private Prop<String> description;
	private Prop<String> location;
	private Prop<ProblemSeverity> severity;
	
	/**
	 * Create a new {@link ProblemDefault}
	 * 
	 * @param description 	see {@link #description()}
	 * @param location 		see {@link #location()}
	 * @param severity 		see {@link #severity()}
	 */
	public ProblemDefault(String description, String location,
			ProblemSeverity severity) {
		super();
		this.severity = create(ProblemSeverity.class, "severity", severity);
		this.description = create("description", description);
		this.location = create("location", location);
	}

	@Override
	public Prop<String> description() {return description;}

	@Override
	public Prop<String> location() {return location;}

	@Override
	public Prop<ProblemSeverity> severity() {return severity;}

}
