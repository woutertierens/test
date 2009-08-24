package org.jpropeller.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import org.jpropeller.concurrency.impl.CancellingBackgroundResponder;

/**
 * A response used by {@link CancellingBackgroundResponder}, this can
 * be run but also cancelled during a run.
 */
public interface CancellableResponse {

	/**
	 * Carry out a response.
	 * This code should be capable of running on any thread, but will not be
	 * run on more than one thread concurrently. This generally means that
	 * no synchronization is necessary, but threading may require special consideration.
	 * For example, any Swing functions must be run using 
	 * {@link SwingUtilities#invokeAndWait(Runnable)}, or similar.
	 * The entire response must be complete when the method returns - the
	 * method must not use any other threads, unless it guarantees that they
	 * will have completed their work before the method returns. This is why
	 * {@link SwingUtilities#invokeAndWait(Runnable)} must be used instead
	 * of {@link SwingUtilities#invokeLater(Runnable)}.
	 * 
	 * Finally, the respond method should terminate as soon as possible
	 * if the shouldCancel parameter becomes true while the method is
	 * running. The {@link CancellableResponse} should not store old
	 * {@link AtomicBoolean} values, since the instance used  may change 
	 * from invocation to invocation.
	 * 
	 * If a response is cancelled, then it will generally be run again
	 * later (possibly immediately). Hence it should "cut short" the
	 * response as soon as possible. This does not need to be instant,
	 * and indeed the response does not really need to cancel at all,
	 * but it is best for performance if it stops at the next reasonable
	 * point. For example, if the response carries out some long calculation,
	 * it could abandon that calculation and instead return a reasonable
	 * default value, which will then be corrected later.
	 * 
	 * @param shouldCancel		Set to true to indicate that the response
	 * 							should finish early if possible. May be
	 * 							a different instance for each invocation
	 * 							of this method.
	 */
	public void respond(AtomicBoolean shouldCancel);

}
