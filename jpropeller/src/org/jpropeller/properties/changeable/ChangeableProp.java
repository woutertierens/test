/*
 *  $Id: BeanProp.java,v 1.1 2008/03/24 11:19:44 shingoki Exp $
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
package org.jpropeller.properties.changeable;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;

/**
 * A {@link Prop} whose value is a {@link Changeable}
 *
 * @param <T>
 * 		The type of {@link Changeable} in the {@link ChangeableProp}
 */
public interface ChangeableProp<T extends Changeable> extends Prop<T> {

}
