package org.jpropeller.view.bean.impl;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.JViewSource;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;

/**
 * This will use an existing JView in the same way as a Prop view - 
 * that is, it will arrange for the View always to display the contents
 * of a named prop of a bean.
 * 
 * @param <T>	The type of value in the viewed prop
 */
public class PropViewAdaptor<T> implements JView, UpdatableSingleValueView<Bean> {

	private Reference<? extends Bean> model;

	private JView view;
	
	private PropViewAdaptor(Reference<? extends Bean> model,
			PropName<T> displayedName, JViewSource<T> viewSource) {
		super();
		this.model = model;
	
		Reference<T> ref = PathPropBuilder.fromRef(displayedName.getPropClass(), model).toRef(displayedName);	
		view = viewSource.get(ref);
	}

	@Override
	public void dispose() {
		view.dispose();
	}

	/**
	 * Create a {@link PropViewAdaptor}
	 * 
	 * @param model			The {@link Reference} for this {@link View} 
	 * @param displayedName	The name of the displayed property 
	 * @param viewSource	Supplies {@link View}s of type T
	 * @return				A new {@link PropViewAdaptor}
	 * 
	 * @param <T>	The type of value in the viewed prop
	 */
	public static <T> PropViewAdaptor<T> create(Reference<? extends Bean> model,
			PropName<T> displayedName, JViewSource<T> viewSource) {
		return new PropViewAdaptor<T>(model, displayedName, viewSource);
	}

	@Override
	public Reference<? extends Bean> getModel() {
		return model;
	}
	
	/**
	 * Get the {@link JCheckBox} used for display/editing
	 * 
	 * @return		The {@link JCheckBox}
	 */
	public JComponent getComponent() {
		return view.getComponent();
	}
	
	@Override
	public void cancel() {
		view.cancel();
	}
	
	@Override
	public void commit() throws CompletionException {
		view.commit();
	}

	@Override
	public boolean isEditing() {
		return view.isEditing();
	}
	
	@Override
	public void update() {
		//Editor updates itself.
	}

	@Override
	public boolean selfNaming() {
		return view.selfNaming();
	}
	
	/**
	 * Create a factory to produce prop views by passing
	 * through to {@link View}s from a {@link JViewSource},
	 * using {@link PropViewAdaptor} instances
	 * @param source	Supplies {@link View}s of type T
	 * @param clazz		The class accepted by the JViewSource
	 * @return			A factory for prop views
	 * 
	 * @param <T>		The type of viewed element in props
	 */
	public static final <T> PropViewFactory factoryFor(final JViewSource<T> source, final Class<T> clazz) {
		return new PropViewFactory() {
		
			@SuppressWarnings("unchecked")
			@Override
			public <M> JView viewFor(Reference<? extends Bean> model,
					PropName<M> displayedName) {
				if (!providesFor(displayedName)) {
					return null;
				} else {
					return create(model, (PropName<T>)displayedName, source);
				}
			}

			@Override
			public boolean providesFor(PropName<?> displayedName) {
				return !displayedName.isTGeneric() && clazz.isAssignableFrom(displayedName.getPropClass());
			}
		};
	}
	
	@Override
	public Format format() {
		return view.format();
	}

	
}