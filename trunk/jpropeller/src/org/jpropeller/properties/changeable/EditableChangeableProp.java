package org.jpropeller.properties.changeable;

import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;

/**
 * A {@link ChangeableProp} which is also an {@link EditableProp}
 * 
 * @param <T>
 * 		The type of value in the {@link Prop}
 */
public interface EditableChangeableProp<T extends Changeable> extends GenericEditableChangeableProp<T>, EditableProp<T>, ChangeableProp<T>{
}
