package org.jpropeller.undo.delegates.impl;

import java.util.HashMap;

import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.collection.impl.CMapDefault;
import org.jpropeller.properties.calculated.background.impl.BackgroundCalculatedProp;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.constrained.impl.ConstrainedProp;
import org.jpropeller.properties.constrained.impl.SelectionFromCollectionProp;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.list.selection.impl.ListIndexProp;
import org.jpropeller.properties.list.selection.impl.ListSelectionProp;
import org.jpropeller.properties.list.selection.impl.ListSelectionReferenceProp;
import org.jpropeller.properties.map.impl.MapPropDefault;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.set.impl.SetPropDefault;
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
		PropUndoDelegate defaultDelegate = new PropUndoDelegate(); 
		
		//Special delegates
		put(CListDefault.class, new ObservableListUndoDelegate());
		put(CMapDefault.class, new ObservableMapUndoDelegate());
		put(CMapDefault.class, new ObservableMapUndoDelegate());
		
		//Default delegates for props that may have an editable value
		put(ChangeablePropDefault.class, defaultDelegate);
		put(PropImmutable.class, defaultDelegate);
		put(ListSelectionProp.class, defaultDelegate);
		put(ListSelectionReferenceProp.class, defaultDelegate);
		put(ListPropDefault.class, nullDelegate);
		put(SetPropDefault.class, nullDelegate);
		put(MapPropDefault.class, nullDelegate);
		put(ConstrainedProp.class, defaultDelegate);
		put(SelectionFromCollectionProp.class, defaultDelegate);

		//FIXME assess this
		//Transient/immutable props, and those that just mirror the state of another prop
		put(BackgroundCalculatedProp.class, nullDelegate);
		put(CalculatedProp.class, nullDelegate);
		put(ListIndexProp.class, nullDelegate);
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
