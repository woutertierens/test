package org.jpropeller.path;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;

/**
 * New name for somewhat complicated generic {@link List} required by {@link BeanPath}
 * @author bwebster
 */
public interface PathNameList extends List<Transformer<? super Bean, Prop<? extends Bean>>>{
	
}
