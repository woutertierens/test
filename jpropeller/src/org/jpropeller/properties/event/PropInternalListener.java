/*
 *  $Id: PropListener.java,v 1.1 2008/03/24 11:19:32 shingoki Exp $
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

import org.jpropeller.map.PropMap;

/**
 * A listener to {@link PropMap}, to be notified when a Prop
 * in one of those {@link PropMap} changes
 * This is part of the prop system itself, and so is used
 * by Props, {@link PropMap}s etc. to listen for events. It must
 * NOT be used by the view layer, which must instead use
 * PropListener, and propChanged. Events sent to the view
 * layer may be synchronized or delayed, etc. in order to
 * maintain valid functioning of the prop system.
 * @author shingoki
 *
 */
public interface PropInternalListener {

	/**
	 * Called when a prop changes inside a {@link PropMap} to
	 * which the listener is listening
	 * @param <T>
	 * 		The type of Prop
	 * @param event
	 * 		Carries details of the change
	 */
	public <T> void	propInternalChanged(PropEvent<T> event);
	
}
