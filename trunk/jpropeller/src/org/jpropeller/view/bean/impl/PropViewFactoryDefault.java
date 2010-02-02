package org.jpropeller.view.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.impl.LabelPropView;

/**
 * A default implementation of {@link PropViewFactory} providing
 * views for props containing primitives, etc., choosing between
 * editable and read-only views as appropriate
 */
public class PropViewFactoryDefault implements PropViewFactory {

	private final static PropViewFactory editable = new PropViewFactoryEditable(); 
	private final static PropViewFactory readOnly = new PropViewFactoryReadOnly(); 
	
	/**
	 * 		Creates a new factory with only default views that will always return
	 * 		a label view if no other view can be found.
	 */
	public PropViewFactoryDefault() {
	}

	//We need to suppress warnings on casting propnames. Note that this is safe
	//since we can check both parametric types involved in the cast, against
	//the prop info and propname class
	@Override
	public <M> JView viewFor(final Reference<? extends Bean> model,
			final PropName<M> displayedName, Prop<Boolean> locked) {
		
		Prop<M> prop = model.value().get().features().get(displayedName);
		PropEditability editability = prop.getEditability();

		PropViewFactory delegate = (editability == PropEditability.EDITABLE) ? editable : readOnly;

		if(delegate.providesFor(displayedName)) {
			return delegate.viewFor(model, displayedName, locked);
		} else {
			return LabelPropView.create(model, displayedName);
		}
	}
	
	@Override
	public <M> JView viewFor(Reference<? extends Bean> model,
			PropName<M> displayedName) {
		return viewFor(model, displayedName, null);
	}

	@Override
	public boolean providesFor(PropName<?> displayedName) {
		return true;
	}
	
}
