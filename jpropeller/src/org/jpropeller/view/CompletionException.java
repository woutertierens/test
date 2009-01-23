/*
 *  $Id: MutablePropPrimitive.java,v 1.1 2008/03/24 11:19:49 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jpropeller.view;

/**
 * Exception thrown when a {@link View} cannot complete editing in
 * the specified manner
 */
public class CompletionException extends Exception {

	/**
	 * Create exception 
	 */
	public CompletionException() {
		super();
	}

	/**
	 * Create exception 
	 * @param message Descriptive human readable message for why commiting failed,
	 * no initial capital or final full stop, in format suitable for:
	 *	"The editing failed because " + message + "."
	 */
	public CompletionException(String message) {
		super(message);
	}

	/**
	 * Create exception 
	 * @param message Descriptive human readable message for why commiting failed,
	 * no initial capital or final full stop, in format suitable for:
	 *	"The editing failed because " + message + "."
	 * @param cause Cause of the exception
	 */
	public CompletionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create exception 
	 * @param cause Cause of the exception
	 */
	public CompletionException(Throwable cause) {
		super(cause);
	}

}
