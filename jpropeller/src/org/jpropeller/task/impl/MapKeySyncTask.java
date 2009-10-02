package org.jpropeller.task.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.task.Task;
import org.jpropeller.util.Source;

/**
 * This {@link Task} will update a Prop containing a CMap 
 * so that the CMap contains entries for exactly those unique
 * keys in a collection of requiredKeys.
 * 
 * That is, any mappings from keys not in the list will be removed,
 * and any requiredKeys that do not have mappings will have a mapping
 * added, to a new value got from a valueSource.
 * 
 * @param <K>	The type of key in the map
 * @param <V> 	The type of value in the map
 */
public class MapKeySyncTask<K, V> implements Task {

	private final Prop<? extends CCollection<? extends K>> requiredKeys;
	private final Prop<? extends CMap<K, V>> map;
	private final Set<Prop<?>> sources;
	private final Source<V> valueSource;

	/**
	 * Create a {@link MapKeySyncTask}
	 * @param requiredKeys	The keys required to be in the map
	 * @param map			The map itself
	 * @param valueSource	Source of new values for the map
	 */
	public MapKeySyncTask(
			Prop<? extends CCollection<? extends K>> requiredKeys, 
			Prop<? extends CMap<K, V>> map,
			Source<V> valueSource) {
		super();
		this.requiredKeys = requiredKeys;
		this.map = map;
		this.valueSource = valueSource;
		
		IdentityHashSet<Prop<?>> sourcesM = new IdentityHashSet<Prop<?>>();
		sourcesM.add(requiredKeys);
		sources = Collections.unmodifiableSet(sourcesM);
	}

	@Override
	public Set<? extends Changeable> getSources() {
		return sources;
	}

	@Override
	public void respond(AtomicBoolean shouldCancel) {
		Props.acquire();
		try {
			Set<K> uniqueKeys = new HashSet<K>(requiredKeys.get());
			
			Map<K, V> newMap = new HashMap<K, V>();
			CMap<K, V> oldMap = map.get();

			//Copy across entries from old to new, where the key is in the unique key set 
			for (K dye : oldMap.keySet()) {
				if (uniqueKeys.contains(dye)) {
					newMap.put(dye, oldMap.get(dye));
				}
			}
			
			//Add new settings for any new dyes
			for (K dye : uniqueKeys) {
				if (!newMap.containsKey(dye)) {
					newMap.put(dye, valueSource.get());
				}
			}		
			
			//Update the dye settings
			oldMap.replace(newMap);
		} finally {
			Props.release();
		}
	}

}
