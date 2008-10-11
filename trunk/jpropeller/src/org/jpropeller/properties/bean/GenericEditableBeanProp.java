package org.jpropeller.properties.bean;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.Prop;

/**
 * A {@link BeanProp} which is also an {@link GenericEditableProp}
 * 
 * @param <T>
 * 		The type of value in the {@link Prop}
 */
public interface GenericEditableBeanProp<T extends Bean> extends GenericBeanProp<T>, GenericEditableProp<T>{
}
