/*
 *  $Id: PropEventOrigin.java,v 1.1 2008/03/24 11:19:33 shingoki Exp $
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
package org.jpropeller.properties.event;

/**
 * The possible origins of a PropEvent
 */
public enum PropEventOrigin {
	/**
	 * The event came from "outside the system" - 
	 * as the result of a direct edit by the user of the system of Props (not
	 * necessarily a person)
	 */
	USER,
	
	/**
	 * The change came from "inside the system", 
	 * as a consequence of another Prop change, OR;
	 */
	CONSEQUENCE,
	
	/**
	 * An automatic update due to an inevitable
	 * occurrence such as time passing, a process completing, or an external
	 * monitored device changing, etc.
	 */
	INEVITABLE
}