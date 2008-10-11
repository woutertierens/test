package org.jpropeller.properties.bean;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;

/**
 * A {@link BeanProp} which is also an {@link EditableProp}
 * 
 * @param <T>
 * 		The type of value in the {@link Prop}
 */
public interface EditableBeanProp<T extends Bean> extends GenericEditableBeanProp<T>, EditableProp<T>, BeanProp<T>{
}
