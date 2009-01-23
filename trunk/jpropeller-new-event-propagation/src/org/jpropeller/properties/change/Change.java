package org.jpropeller.properties.change;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.ObservableMap;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.view.View;

/**
 * Carries details of any change that a {@link Bean} or {@link GeneralProp}
 * may have.
 * Subinterfaces carry specific details of changes associated with
 * specific types of Bean/prop - this interface deals with the minimum
 * information for any change. 
 */
public interface Change {

	/**
	 * This value is slightly complicated, and is really
	 * only useful for optimisations, for example where
	 * a {@link View} might wish to skip some updates when
	 * it knows that it is still viewing the same instance.
	 * If this value does not make any sense to you, it is
	 * safe to ignore it, and assume that when you see a 
	 * change on a {@link Bean} or prop, anything and
	 * everything about that {@link Bean} or prop may have
	 * changed.
	 * 
	 * If the change refers to a {@link Bean}, then
	 * this is true only if, for the old value x and new
	 * value y of any prop of the bean, we know that
	 * x == y
	 * 
	 * If the change refers to a {@link GeneralProp}, then
	 * this is true only if, for the old value x and new
	 * value y of the prop, x == y
	 * 
	 * If the change refers to an {@link ObservableList}, then
	 * this is true only if the list still has the exact 
	 * same instances in it, in the same order - that is,
	 * the change has not involved a set, add, remove, etc.
	 * Each element is == to its old value. 
	 * 
	 * If the change refers to an {@link ObservableMap}, then
	 * this is true only if the map still has the exact 
	 * same mappings in it, - that is,
	 * the change has not involved a put, remove, etc.
	 * Each mapping is to a new value == to the old value 
	 * for that key.
	 *
	 * Note that in all these cases, we DON'T guarantee that
	 * if {@link #sameInstances()} returns false, the
	 * instances are different. That is, where instances
	 * are different, {@link #sameInstances()} MUST return false,
	 * but where they are the same it may not always return
	 * true. For example, in some cases it is not possible to
	 * tell whether the instance will be the same or different,
	 * in this case {@link #sameInstances()} will return false
	 * for safety.
	 * 
	 * @return
	 * 		True if we are sure that the instances in the prop,
	 * or all props of a bean, are the same (identical, ==)
	 */
	public boolean sameInstances();
	
	/**
	 * This is true only for the first change in any {@link GeneralProp}.
	 * Such a change will nearly always cause changes in other 
	 * {@link GeneralProp}s and/or {@link Bean}s - this is known as
	 * propagation of the change. All changes that are triggered in
	 * this "chain reaction" are NOT initial changes. When propagation
	 * is finished, another initial change may spark another change propagation.
	 * @return
	 * 		True if the change is the first in a propagation.
	 */
	public boolean initial();
	
	/**
	 * The types of change
	 * @return
	 * 		{@link ChangeType} for this {@link Change}
	 */
	public ChangeType type();
	
	/**
	 * Extend an existing change, to return a new {@link Change} that encompasses
	 * all changes made by the existing change AND this change.
	 * If this change is already covered by the existing change, then null should
	 * be returned. If this change already covers the existing change, then the
	 * change may return itself to avoid creating a new change.
	 * @param existing
	 * 		An existing change
	 * @return
	 * 		A new change that covers the existing change AND this change, or
	 * null if this change is already covered by the existing change.
	 */
	public Change extend(Change existing);
	
}
