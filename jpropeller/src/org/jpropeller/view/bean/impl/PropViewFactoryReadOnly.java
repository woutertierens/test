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

/**
 * An implementation of {@link PropViewFactory} providing
 * views for read-only props containing primitives, etc.
 */
public class PropViewFactoryReadOnly implements PropViewFactory {

	private static final Map<Class<?>, PropViewFactory> defaultViews;

	/**
	 * Default editor.
	 */
	public static final PropViewFactory intFactory = new PropViewFactory(){
//		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
//			if (model.features().get(displayedName).features().hasMetadata(BeanEditor.ZERO_BASED_TO_ONE_BASED_DISPLAY)) {
//				return LabelPropView.createOneBasedDisplayInteger(model, (PropName<Integer>) displayedName);
//			} else {
			return LabelPropView.create(model, displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Integer.class;
		}
		
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			//Same view - labels never edit 
			return viewFor(model, displayedName);
		}

	};

	/**
	 * Default editor.
	 */
	public static final PropViewFactory longFactory = new PropViewFactory(){
//		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
//			if (model.features().get(displayedName).features().hasMetadata(BeanEditor.ZERO_BASED_TO_ONE_BASED_DISPLAY)) {
//				return LabelPropView.createOneBasedDisplayLong(model, (PropName<Long>) displayedName);
//			} else {
			return LabelPropView.create(model, displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Long.class;
		}
		
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			//Same view - labels never edit 
			return viewFor(model, displayedName);
		}

	};

	
	/**
	 * Label editor factor.
	 */
	public static final PropViewFactory labelFactory = new PropViewFactory(){
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName) {
			return LabelPropView.create(model, displayedName);
		}

		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return LabelPropView.create(model, displayedName);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			//Can display anything
			return true;
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
			return ImmutableIconPropView.create(model, (PropName<ImmutableIcon>) displayedName);
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
			return ColorEditor.create(model, (PropName<Color>) displayedName, null, false);
		}

		@Override
		public boolean providesFor(PropName<?> displayedName) {
			return !displayedName.isTGeneric() && displayedName.getPropClass() == Color.class;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <M> JView viewFor(Reference<? extends Bean> model,
				PropName<M> displayedName, Prop<Boolean> locked) {
			return ColorEditor.create(model, (PropName<Color>) displayedName, locked, false);
		}

	};
	
	static {
		defaultViews = new HashMap<Class<?>, PropViewFactory>();
		
		defaultViews.put(Double.class, labelFactory);
		defaultViews.put(Long.class, longFactory);
		defaultViews.put(Float.class, labelFactory);
		defaultViews.put(Integer.class, intFactory);
		
		defaultViews.put(String.class, labelFactory);
		
		//TODO Need a real read-only view
		defaultViews.put(Boolean.class, booleanFactory);
		
		defaultViews.put(ImmutableIcon.class, iconFactory);
		
		//TODO Need better view - just a color swatch
		defaultViews.put(Color.class, colorFactory);		
		
	}

	private boolean fallback;
	
	private final Map<Class<?>, PropViewFactory> views; 
	
	
	/**
	 * 		Creates a new factory with only default views that will always return
	 * 		a label view if no other view can be found.
	 */
	public PropViewFactoryReadOnly() {
		this(true);
	}
	

	/**
	 * 		Creates a new factory with only default views.
	 * @param fallback 		true: Falls back to a label view if no other view can be found.
	 * 						false: Returns null if no other view can be found. 
	 */
	public PropViewFactoryReadOnly(boolean fallback) {
		this(fallback, new HashMap<Class<?>, PropViewFactory>());
	}
		
	/**
	 * 		Creates a new factory.
	 * @param fallback 		true: Falls back to a label view if no other view can be found.
	 * 						false: Returns null if no other view can be found. 
	 * @param views 		The map of views that will be tried before anything else.
	 */
	public PropViewFactoryReadOnly(boolean fallback, Map<Class<?>, PropViewFactory> views) { 
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
	
	@Override
	public <M> JView viewFor(Reference<? extends Bean> model,
			PropName<M> displayedName) {
		return viewFor(model, displayedName, null);
	}


	@Override
	public boolean providesFor(PropName<?> displayedName) {
		return views.containsKey(displayedName.getPropClass()) || defaultViews.containsKey(displayedName.getPropClass()) || fallback; 
	}
	
}
