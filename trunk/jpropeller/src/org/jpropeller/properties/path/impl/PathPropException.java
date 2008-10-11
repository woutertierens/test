/*
 *  $Id: PathPropException.java,v 1.1 2008/03/24 11:19:55 shingoki Exp $
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
package org.jpropeller.properties.path.impl;

/**
 * An exception thrown when a PathProp cannot look up its value
 * @author shingoki
 */
public class PathPropException extends RuntimeException {
	private static final long serialVersionUID = -3038172194366314897L;

	/**
	 * Create an exception
	 */
	public PathPropException() {
	}

	/**
	 * Create an exception
	 * @param message
	 */
	public PathPropException(String message) {
		super(message);
	}

	/**
	 * Create an exception
	 * @param cause
	 */
	public PathPropException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create an exception
	 * @param message
	 * @param cause
	 */
	public PathPropException(String message, Throwable cause) {
		super(message, cause);
	}

}
