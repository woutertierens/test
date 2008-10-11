/*
 *  $Id: PropMap.java,v 1.1 2008/03/24 11:20:18 shingoki Exp $
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
package org.jpropeller.map;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.PropListener;

/**
 * A set of {@link GeneralProp}s, with methods for listening to props, looking them
 * up by name, and for props in the set to notify the set when they
 * have changed.
 * 
 * NOTE: implementations must enforce the rules for {@link Bean}s, {@link PropMap}s and {@link GeneralProp}s:
 * 
 * 		Only props with {@link GeneralProp#props()} returning null may be added to a {@link PropMap}, and
 * when they are added, they must have their {@link PropMap} set to the {@link PropMap} they
 * are added to
 * 
 * 		The return value of {@link #getBean()} may be null when a {@link PropMap} is initialised, but
 * when it changes to a non-null value, it must NOT change again. Ideally the
 * bean should be set on construction and be immutable. 
 * 
 * 		Properties should not be exchanged or removed - that is, the value of 
 * a prop may change, but a {@link PropMap} should contain the same {@link GeneralProp} instance for 
 * each property throughout its lifetime, so that users of a {@link Bean} may retain 
 * a reference to the prop rather than the {@link Bean}, if they wish. (Also because 
 * there are events when {@link GeneralProp} values change, but no events for the 
 * {@link GeneralProp} instance changing, etc. Changing {@link GeneralProp} instances 
 * instead of {@link GeneralProp} values via setter methods,
 * e.g. {@ling EditableProp#set(Object)} essentially defeats the point of the system)
 * 
 * 		All {@link GeneralProp}s in a {@link PropMap} must have an appropriate type 
 * of {@link PropName} - that is, a {@link GeneralProp} cannot be added if it has a 
 * {@link PropName} for an {@link EditableProp} but is NOT an {@link EditableProp},
 * and similarly for UneditablePropName and UneditableProp
 * 
 * 		A {@link PropMap} can be iterated as a list of the {@link Prop}s in the map,
 * in the order they were added
 * 
 * @author shingoki
 *
 */
public interface PropMap extends Iterable<GeneralProp<?>>{

	/**
	 * Add a listener to be notified of changes to any Prop in the {@link PropMap} 
	 * This is for INTERNAL use - by props, etc. in the model layer, NOT
	 * for use by the view layer. See {@link PropInternalListener} and {@link PropListener}
	 * @param listener
	 * 		The listener
	 */
	public void addInternalListener(PropInternalListener listener);

	/**
	 * A listener no longer to be notified of changes to any Prop in the {@link PropMap} 
	 * This is for INTERNAL use - by props, etc. in the model layer, NOT
	 * for use by the view layer. See {@link PropInternalListener} and {@link PropListener}
	 * @param listener
	 * 		The listener
	 */
	public void removeInternalListener(PropInternalListener listener);
	
	/**
	 * Add a listener to be notified of changes to any Prop in the {@link PropMap} 
	 * This is for EXTERNAL/view use. See {@link PropInternalListener} and {@link PropListener}
	 * @param listener
	 * 		The listener
	 */
	public void addListener(PropListener listener);

	/**
	 * A listener no longer to be notified of changes to any Prop in the {@link PropMap} 
	 * This is for EXTERNAL/view use. See {@link PropInternalListener} and {@link PropListener}
	 * @param listener
	 * 		The listener
	 */
	public void removeListener(PropListener listener);

	/**
	 * Get the prop in the set with the specified name. If there is no
	 * such prop, null is returned
	 * @param <P> 
	 * 		The type of {@link Prop} to be found, e.g. {@link EditableProp}
	 * @param <S>
	 * 		The type of data in the {@link Prop} to be found
	 * @param name
	 * 		The name of the Prop
	 * @return
	 * 		The prop with the name, or null if there is no such Prop in the set
	 */
	public <P extends GeneralProp<S>, S> P get(PropName<P, S> name);

	/**
	 * Get the prop in the set with the specified name. If there is no
	 * such prop, null is returned
	 * Note that because a {@link GenericPropName} is used rather than a {@link PropName},
	 * the {@link GeneralProp} returned cannot be guaranteed to have a type
	 * S EXACTLY matching the type of any other {@link GeneralProp} looked up
	 * with the same {@link GenericPropName}. {@link #get(PropName)} DOES make
	 * this guarantee, by using a {@link PropName} rather than a {@link GenericPropName}
	 * @param <P> 
	 * 		The type of {@link Prop} to be found, e.g. {@link EditableProp}
	 * @param <S>
	 * 		The type of data in the {@link Prop} to be found
	 * @param name
	 * 		The name of the Prop
	 * @return
	 * 		The prop with the name, or null if there is no such Prop in the set
	 */
	public <P extends GeneralProp<S>, S> P getUnsafe(GenericPropName<P, S> name);

	/**
	 * A list of Props in the set, in the order they were added to the set
	 * @return
	 * 		Ordered list of Props in the set
	 */
	public List<GeneralProp<?>> getList();
	
	/**
	 * Called by a Prop in this set, the event will be handled and sent onwards
	 * to listeners as appropriate
	 * @param event
	 * 		Event carrying details of the change
	 */
	public void propChanged(PropEvent<?> event);
	
	/**
	 * The Bean to which this {@link PropMap} belongs - it can only belong to one
	 * Bean
	 * @return
	 * 		The Bean to which this {@link PropMap} belongs
	 */
	public Bean getBean();
	
}
