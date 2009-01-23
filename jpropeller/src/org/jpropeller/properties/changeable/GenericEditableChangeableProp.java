package org.jpropeller.properties.changeable;

import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;

/**
 * A {@link ChangeableProp} which is also an {@link GenericEditableProp}
 * 
 * @param <T>
 * 		The type of value in the {@link Prop}
 */
public interface GenericEditableChangeableProp<T extends Changeable> extends GenericChangeableProp<T>, GenericEditableProp<T>{
}
