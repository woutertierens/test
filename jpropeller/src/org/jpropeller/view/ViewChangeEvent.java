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

import java.util.EventObject;

/**
 * Event caused by a {@link View} changing its model
 * @param <M>
 * 		The type of model 
 */
public class ViewChangeEvent<M> extends EventObject {

	M oldModel;
	M newModel;
	View<M> source;

	/**
	 * Create a change event
	 * @param source The source of the event - the View whose model has changed
	 * @param oldModel The model the View was viewing previously
	 * @param newModel The model the View is now viewing
	 */
	public ViewChangeEvent(View<M> source, M oldModel, M newModel) {
		super(source);
		this.source = source;
		this.oldModel = oldModel;
		this.newModel = newModel;
	}

	/**
	 * The newly displayed model
	 * @return Returns the newModel, {@link View} is no displaying this
	 */
	public M getNewModel() {
		return newModel;
	}
	
	/**
	 * The previously displayed model
	 * @return Returns the oldModel, {@link View} previously displayed this
	 */
	public M getOldModel() {
		return oldModel;
	}
	
	/**
	 * The view that has changed
	 * @return
	 * 		THe changed view
	 */
	public View<M> getSourceView() {
		return source;
	}

}
