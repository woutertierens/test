package org.jpropeller.concurrency;

/**
 * A simple class that responds in some way to requests.
 * 
 * The timing of responses can vary, but in general they 
 * must obey the contract that EVERY request must be responded
 * to at some point after the request. 
 * Note that this does NOT imply that there must be one 
 * response per request - many requests can all have
 * the requirement met by a single response performed 
 * after them all.
 * @author shingoki
 */
public interface Responder {

	/**
	 * Request a response. A response will occur at some time in the future -
	 * note that there is not necessarily one response per request, since the
	 * responses may be delayed, and hence service more than one request.
	 */
	public void request();

}