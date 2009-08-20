package org.jpropeller.properties.change.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jpropeller.collection.impl.ReferenceCounter;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.system.Props;

/**
 *	Default implementation of {@link ChangeableFeatures}, which handles
 *  storing and giving access to listener lists
 */
public class ChangeableFeaturesDefault implements ChangeableFeatures {

	InternalChangeImplementation internalChangeImplementation;

	ReferenceCounter<Changeable> changeableListeners = new ReferenceCounter<Changeable>();
	ReferenceCounter<ChangeListener> listeners = new ReferenceCounter<ChangeListener>();

	Map<String, String> annotations = null;
	
	Changeable owner;
	
	/**
	 * Make a new {@link ChangeableFeaturesDefault}
	 * @param internalChangeImplementation
	 * 		The implementation of response to internal changes to be used
	 * by this {@link ChangeableFeaturesDefault}
	 * @param owner
	 * 		The {@link Changeable} that will have this {@link ChangeableFeatures} 
	 */
	public ChangeableFeaturesDefault(
			InternalChangeImplementation internalChangeImplementation, Changeable owner) {
		super();
		this.internalChangeImplementation = internalChangeImplementation;
		this.owner = owner;
	}

	@Override
	public void addChangeableListener(Changeable listener) {
		Props.getPropSystem().getChangeSystem().prepareListenerChange(owner);
		try {
			changeableListeners.addReference(listener);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeListenerChange(owner);
		}
	}

	@Override
	public void addListener(ChangeListener listener) {
		Props.getPropSystem().getChangeSystem().prepareListenerChange(owner);
		try {
			listeners.addReference(listener);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeListenerChange(owner);
		}
	}

	@Override
	public void removeChangeableListener(Changeable listener) {
		Props.getPropSystem().getChangeSystem().prepareListenerChange(owner);
		try {
			changeableListeners.removeReference(listener);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeListenerChange(owner);
		}
	}

	@Override
	public void removeListener(ChangeListener listener) {
		Props.getPropSystem().getChangeSystem().prepareListenerChange(owner);
		try {
			listeners.removeReference(listener);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeListenerChange(owner);
		}
	}

	@Override
	public Iterable<Changeable> changeableListenerList() {
		return changeableListeners;
	}

	@Override
	public Iterable<ChangeListener> listenerList() {
		return listeners;
	}

	@Override
	public Change internalChange(Changeable changed, Change change, List<Changeable> initial,
			Map<Changeable, Change> changes) {
		return internalChangeImplementation.internalChange(changed, change, initial, changes);
	}

	@Override
	public String getMetadata(String key) {
		if (annotations == null) {
			return null;
		} else {
			return annotations.get(key);
		}
	}

	@Override
	public boolean hasMetadata(String key) {
		if (annotations == null) {
			return false;
		} else {
			return annotations.containsKey(key);
		}
	}

	@Override
	public String putMetadata(String key, String value) {
		if (annotations == null) {
			annotations = new HashMap<String, String>();
		}
		return annotations.put(key, value);
	}

	@Override
	public String removeMetadata(String key) {
		if (annotations == null) {
			return null;
		} else {
			return annotations.remove(key);
		}
	}

	@Override
	public String putMetadata(String key) {
		return putMetadata(key, "true");
	}

}
