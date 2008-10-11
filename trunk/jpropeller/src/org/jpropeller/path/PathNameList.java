package org.jpropeller.path;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;

/**
 * New name for somewhat complicated generic {@link List} required by {@link BeanPath}
 * @author bwebster
 */
public interface PathNameList extends List<PropName<? extends Prop<? extends Bean>, ? extends Bean>>{
	
}
