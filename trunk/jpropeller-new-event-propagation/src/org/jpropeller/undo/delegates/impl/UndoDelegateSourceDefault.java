package org.jpropeller.undo.delegates.impl;

import java.util.HashMap;

import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.collection.impl.ObservableMapDefault;
import org.jpropeller.properties.calculated.background.impl.BackgroundCalculatedProp;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.changeable.impl.EditableChangeablePropDefault;
import org.jpropeller.properties.changeable.impl.GenericChangeablePropDefault;
import org.jpropeller.properties.changeable.impl.GenericEditableChangeablePropDefault;
import org.jpropeller.properties.immutable.impl.EditablePropImmutable;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.list.impl.EditableListIndexProp;
import org.jpropeller.properties.list.impl.EditableListPropDefault;
import org.jpropeller.properties.list.impl.ListIndexProp;
import org.jpropeller.properties.list.impl.ListSelectionProp;
import org.jpropeller.properties.list.impl.ListSelectionReferenceProp;
import org.jpropeller.properties.map.impl.EditableMapPropDefault;
import org.jpropeller.properties.path.impl.EditablePathProp;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.undo.delegates.UndoDelegate;
import org.jpropeller.undo.delegates.UndoDelegateSource;
import org.jpropeller.undo.delegates.UndoDelegateSourceException;

/**
 * Default implementation of {@link UndoDelegateSource}, for all
 * JPropeller built-in {@link Changeable}s
 */
public class UndoDelegateSourceDefault implements UndoDelegateSource {

	HashMap<Class<?>, UndoDelegate<?>> delegates;
	
	/**
	 * Create an {@link UndoDelegateSourceDefault}
	 */
	public UndoDelegateSourceDefault() {
		super();
		delegates = new HashMap<Class<?>, UndoDelegate<?>>();
		
		NullUndoDelegate nullDelegate = new NullUndoDelegate();
		GenericEditablePropUndoDelegate defaultDelegate = new GenericEditablePropUndoDelegate(); 
		
		//Special delegates
		put(ObservableListDefault.class, new ObservableListUndoDelegate());
		put(ObservableMapDefault.class, new ObservableMapUndoDelegate());
		put(ObservableMapDefault.class, new ObservableMapUndoDelegate());
		
		//Default delegates for GenericEditableProps
		put(EditableChangeablePropDefault.class, defaultDelegate);
		put(GenericEditableChangeablePropDefault.class, defaultDelegate);
		put(EditablePropImmutable.class, defaultDelegate);
		put(ListSelectionProp.class, defaultDelegate);
		put(ListSelectionReferenceProp.class, defaultDelegate);
		
		//Transient/immutable props
		put(BackgroundCalculatedProp.class, nullDelegate);
		put(CalculatedProp.class, nullDelegate);
		put(ChangeablePropDefault.class, nullDelegate);
		put(GenericChangeablePropDefault.class, nullDelegate);
		put(PropImmutable.class, nullDelegate);
		put(EditableListPropDefault.class, nullDelegate);
		put(ListIndexProp.class, nullDelegate);
		put(EditableListIndexProp.class, nullDelegate);
		put(EditableMapPropDefault.class, nullDelegate);
		put(EditablePathProp.class, nullDelegate);
		put(PathProp.class, nullDelegate);
		
	}
	
	/**
	 * Put a new delegate in the source
	 * @param clazz
	 * 		The class on which the delegate will work
	 * @param delegate
	 * 		The delegate - MUST accept any {@link Changeable} of
	 * the specified class
	 */
	private void put(Class<?> clazz, UndoDelegate<?> delegate) {
		delegates.put(clazz, delegate);
	}

	@Override
	public UndoDelegate<?> get(Changeable changeable) throws UndoDelegateSourceException {
		UndoDelegate<?> delegate = delegates.get(changeable.getClass());
		if (delegate == null) {
			throw new UndoDelegateSourceException("No delegate for " + changeable + ", class " + changeable.getClass());
		} else {
			return delegate;
		}
	}

}
