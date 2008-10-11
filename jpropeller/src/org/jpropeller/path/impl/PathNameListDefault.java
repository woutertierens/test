package org.jpropeller.path.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.PathNameList;
import org.jpropeller.properties.Prop;

/**
 * Implementation of {@link PathNameList} as a {@link LinkedList}
 * @author bwebster
 */
public class PathNameListDefault extends LinkedList<PropName<? extends Prop<? extends Bean>, ? extends Bean>> implements PathNameList {

    /**
     * Constructs an empty list.
     */
	public PathNameListDefault() {
		super();
	}

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param  c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
	public PathNameListDefault(
			Collection<? extends PropName<? extends Prop<? extends Bean>, ? extends Bean>> c) {
		super(c);
	}

}
