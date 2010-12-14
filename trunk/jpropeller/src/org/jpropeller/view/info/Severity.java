package org.jpropeller.view.info;

import java.util.EnumMap;

import org.jpropeller.util.EnumNaming;

/**
 * Describes the interest/severity level of a problem, notification, message
 * etc.
 */
public enum Severity {
	
	/**
	 * {@link Severity#FINEST} indicates debugging, generally
	 * not shown to the user, at the least finest level.
	 */
	FINEST(true),
	
	/**
	 * {@link Severity#FINER} indicates debugging, generally
	 * not shown to the user, at the least finer level.
	 */
	FINER(true),
	
	/**
	 * {@link Severity#FINE} indicates debugging, generally
	 * not shown to the user, at the least fine level.
	 */
	FINE(true),
	
	/**
	 * {@link Severity#UPDATE} indicates an item of data that
	 * is only transiently interesting. This should normally be
	 * displayed to the user in any "real time" display, but
	 * does not form a particularly interesting historical log,
	 * so can be filtered from these views. 
	 */
	UPDATE(true),
	
	/**
	 * {@link Severity#INFO} indicates an interesting fact, 
	 * with a possibility of improvement, a hint or tip.
	 * Any associated data is still valid, but might work better if changed.
	 * This level of {@link Severity} will commonly be displayed to
	 * a normal user.
	 */
	INFO(false),
	
	/**
	 * {@link Severity#WARNING} indicates a problem
	 * that may or may not cause the data to be invalid or fail, 
	 * or not achieve the desired results. 
	 * 
	 * Any associated data may still be accepted, but should 
	 * be treated with caution. Settings should probably be changed
	 * before attempting any associated action again.
	 */
	WARNING(false),
	
	/**
	 * {@link Severity#ERROR} indicates a problem that
	 * prevents normal execution.
	 * Any associated data to be invalid or fail - in fact no
	 * attempt will be made to use it. 
	 * Action will be needed to resolve the problem - either settings
	 * will need to be changed, or a physical maintenance operation
	 * may be needed, or the process may need to be rerun.
	 */
	ERROR(false),
	
	/**
	 * {@link Severity#FAILURE} carries all the implications of
	 * {@link Severity#ERROR}, but in addition it indicates the error
	 * is not easily recoverable, and additional conditions, may apply:
	 * <ol>
	 * <li>Dangerous error - associated hardware may be at risk of damage, or causing damage</li>
	 * <li>An unrecoverable error - associated hardware may be known to be unusable</li>
	 * </ol>
	 * These should be displayed to the user
	 * in the most obvious, unignorable way possible. The user interface
	 * may wish to require the user to acknowledge the problem before
	 * continuing.
	 */
	FAILURE(false);
	
	private final boolean fineGrained;

	private Severity(boolean fineGrained) {
		this.fineGrained = fineGrained;
	}
	
	private static EnumMap<Severity, String> NAMES = EnumNaming.loadNames(Severity.class, Severity.values());
	
	public String toString() {
		return NAMES.get(this);
	}

	/**
	 * True if this level of severity is considered to be a fine-grained notification,
	 * and so not generally suitable for display in a log.
	 * @return	True if fine grained, false otherwise
	 */
	public boolean isFineGrained() {
		return fineGrained;
	}
	
}
