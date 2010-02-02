package org.jpropeller.view.bean.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
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

	private static final Map<Class<?>, PropViewFactory> defaultViews;
	
	/**
	 * Default editor.
	 */
	public static final PropViewFactory doubleFactory = new PropViewFactory(){
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return NumberSpinnerEditor.createDouble(model, (PropName<Double>) displayedName, null);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Double.class;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return NumberSpinnerEditor.createDouble(model, (PropName<Double>) displayedName, locked);
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
			return NumberSpinnerEditor.createInteger(model, (PropName<Integer>) displayedName, null);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Integer.class;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return NumberSpinnerEditor.createInteger(model, (PropName<Integer>) displayedName, locked);
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
			return NumberSpinnerEditor.createFloat(model, (PropName<Float>) displayedName, null);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Float.class;
		}
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return NumberSpinnerEditor.createFloat(model, (PropName<Float>) displayedName, locked);
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
			return NumberSpinnerEditor.createLong(model, (PropName<Long>) displayedName, null);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Long.class;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return NumberSpinnerEditor.createLong(model, (PropName<Long>) displayedName, locked);
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
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return StringTextFieldEditor.create(model, (PropName<String>) displayedName, locked);
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
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return ColorEditor.create(model, (PropName<Color>) displayedName, locked);
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
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return BooleanCheckboxEditor.create(model, (PropName<Boolean>) displayedName, locked);
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
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return ImmutableIconPropView.create(model, (PropName<ImmutableIcon>) displayedName, locked);
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
	
	//We need to suppress warnings on casting propnames. Note that this is safe
	//since we can check both parametric types involved in the cast, against
	//the prop info and propname class
	@Override
	public <M> JView viewFor(final Reference<? extends Bean> model,
			final PropName<M> displayedName, Prop<Boolean> locked) {
		
		//Prop<M> prop = model.value().get().features().get(displayedName);
		Class<?> c = displayedName.getPropClass();
		
		if(views.containsKey(c)) {
			return views.get(c).viewFor(model, displayedName, locked);
		}
		
		if(defaultViews.containsKey(c)) {
			return defaultViews.get(c).viewFor(model, displayedName, locked);
		}
		
		return fallback ? 
				LabelPropView.create(model, displayedName) : 
					null;
	}
	
	//We need to suppress warnings on casting propnames. Note that this is safe
	//since we can check both parametric types involved in the cast, against
	//the prop info and propname class
	@Override
	public <M> JView viewFor(final Reference<? extends Bean> model,
			final PropName<M> displayedName) {
		return viewFor(model, displayedName, null);
	}

	@Override
	public boolean providesFor(PropName<?> displayedName) {
		return 	defaultViews.containsKey(displayedName) 
			|| 	!displayedName.isTGeneric();
	}
	
}
