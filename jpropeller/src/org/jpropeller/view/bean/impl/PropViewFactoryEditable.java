package org.jpropeller.view.bean.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.reference.Reference;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.view.JView;
import org.jpropeller.view.impl.ImmutableIconPropView;
import org.jpropeller.view.impl.LabelPropView;
import org.jpropeller.view.primitive.impl.BooleanCheckboxEditor;
import org.jpropeller.view.primitive.impl.ColorEditor;
import org.jpropeller.view.primitive.impl.NumberSpinnerEditor;
import org.jpropeller.view.primitive.impl.StringTextFieldEditor;

/**
 * A default implementation of {@link PropViewFactory} providing
 * views for editable props containing primitives, etc.
 */
public class PropViewFactoryEditable implements PropViewFactory {

	private static final ListViewFactory listFactory = new ListViewFactory();
	
	private static final Map<Class<?>, PropViewFactory> defaultViews;
	
	/**
	 * Default editor.
	 */
	public static final PropViewFactory doubleFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return NumberSpinnerEditor.createDouble(model, (PropName<Double>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Double.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory intFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return NumberSpinnerEditor.createInteger(model, (PropName<Integer>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Integer.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory floatFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return NumberSpinnerEditor.createFloat(model, (PropName<Float>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Float.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory longFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return NumberSpinnerEditor.createLong(model, (PropName<Long>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Long.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory stringFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return StringTextFieldEditor.create(model, (PropName<String>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == String.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory colorFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return ColorEditor.create(model, (PropName<Color>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Color.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory booleanFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return BooleanCheckboxEditor.create(model, (PropName<Boolean>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Boolean.class;
		}
	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory iconFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return ImmutableIconPropView.create(model, (PropName<ImmutableIcon>) displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == ImmutableIcon.class;
		}
	};
	
	static {
		defaultViews = new HashMap<Class<?>, PropViewFactory>();
		
		defaultViews.put(Double.class, doubleFactory);
		defaultViews.put(Long.class, longFactory);
		defaultViews.put(Float.class, floatFactory);
		defaultViews.put(Integer.class, intFactory);
		
		defaultViews.put(String.class, stringFactory);
		
		defaultViews.put(Boolean.class, booleanFactory);
		defaultViews.put(ImmutableIcon.class, iconFactory);
		defaultViews.put(Color.class, colorFactory);		
	}

	private boolean fallback;
	
	private final Map<Class<?>, PropViewFactory> views; 
	
	
	/**
	 * 		Creates a new factory with only default views that will always return
	 * 		a label view if no other view can be found.
	 */
	public PropViewFactoryEditable() {
		this(true);
	}
	

	/**
	 * 		Creates a new factory with only default views.
	 * @param fallback 		true: Falls back to a label view if no other view can be found.
	 * 						false: Returns null if no other view can be found. 
	 */
	public PropViewFactoryEditable(boolean fallback) {
		this(fallback, new HashMap<Class<?>, PropViewFactory>());
	}
		
	/**
	 * 		Creates a new factory.
	 * @param fallback 		true: Falls back to a label view if no other view can be found.
	 * 						false: Returns null if no other view can be found. 
	 * @param views 		The map of views that will be tried before anything else.
	 */
	public PropViewFactoryEditable(boolean fallback, Map<Class<?>, PropViewFactory> views) { 
		this.fallback = fallback;
		this.views = views;
	}
	
	//We check that displayedName is a SINGLE access type, and so has
	//a prop type that extends Prop before casting
	//We need to suppress warnings on casting propnames. Note that this is safe
	//since we can check both parametric types involved in the cast, against
	//the prop info and propname class
	@Override
	public <M> JView viewFor(final Reference<? extends Bean> model,
			final PropName<M> displayedName) {
		
		if(listFactory.providesFor(displayedName)) {
			return listFactory.viewFor(model, displayedName);
			
		} else {		
			//Prop<M> prop = model.value().get().features().get(displayedName);
			Class<?> c = displayedName.getPropClass();
			
			if(views.containsKey(c)) {
				return views.get(c).viewFor(model, displayedName);
			}
			
			if(defaultViews.containsKey(c)) {
				return defaultViews.get(c).viewFor(model, displayedName);
			}
			
			return fallback ? 
					LabelPropView.create(model, displayedName) : 
						null;
		}
	}


	@Override
	public boolean providesFor(PropName<?> displayedName) {
		return 	listFactory.providesFor(displayedName) 
			|| 	defaultViews.containsKey(displayedName) 
			|| 	!displayedName.isTGeneric();
	}



	
}
