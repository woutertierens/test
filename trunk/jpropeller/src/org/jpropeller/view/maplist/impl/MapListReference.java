package org.jpropeller.view.maplist.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.ReferenceDefault;

/**
 *	A {@link Reference} to a {@link CMap} of {@link CList} values, indexed
 *	by a given key type. Also has a list of those key values that are relevant - 
 *	this also gives an ordering to the keys where this is important. 
 * @param <K>	The type of key
 * @param <V>	The type of value
 * @param <L>	The type of {@link CList} in the map 
 */
public class MapListReference<K, V, L extends CList<V>> extends BeanDefault implements Reference<CMap<K, L>>{
	
	Prop<CMap<K, L>> value;
	Prop<CList<K>> keys;
	
	/**
	 * Create a {@link ReferenceDefault}
	 * @param value		The referenced value prop
	 * @param keys		The keys for the reference
	 * @param <K>		The type of key
	 * @param <V>		The type of value
	 * @param <L>		The type of {@link CList} in the map 
	 * @return 			A new {@link ReferenceDefault}
	 */
	public static <K, V, L extends CList<V>> MapListReference<K, V, L> 
		create(Prop<CMap<K, L>> value, Prop<CList<K>> keys) {
		return new MapListReference<K, V, L>(value, keys);
	}
	/**
	 * Create a {@link ReferenceDefault}
	 * @param value		The referenced value prop
	 * @param keys		The keys for the reference
	 */
	private MapListReference(Prop<CMap<K, L>> value, Prop<CList<K>> keys) {
		super();
		this.value = addProp(value);
		this.keys = addProp(keys);
	}
	
	/**
	 * The keys to be displayed
	 * @return		List of displayed keys
	 */
	public Prop<CList<K>> keys() { return keys;}

	@Override
	public Prop<CMap<K, L>> value() {return value;}

}
