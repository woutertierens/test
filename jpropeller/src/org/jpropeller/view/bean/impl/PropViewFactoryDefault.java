package org.jpropeller.view.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.impl.LabelPropView;
import org.jpropeller.view.primitive.impl.BooleanCheckboxEditor;
import org.jpropeller.view.primitive.impl.NumberSpinnerEditor;
import org.jpropeller.view.primitive.impl.StringTextFieldEditor;

/**
 * A default implementation of {@link PropViewFactory} providing
 * views for props containing primitives, etc.
 * @author bwebster
 */
public class PropViewFactoryDefault implements PropViewFactory {

	//Map<Class<?>, JView<?>> classToView = new HashMap<Class<?>, JView<?>>();
	
	//We check that displayedName is a SINGLE access type, and so has
	//a prop type that extends Prop before casting
	@SuppressWarnings("unchecked")
	@Override
	public <P extends GeneralProp<M>, M> JView<?> viewFor(Reference<? extends Bean> model,
			PropName<P, M> displayedName) {

		P prop = model.value().get().props().get(displayedName);
		
		//We can only deal with single access props
		if (prop.getInfo().getAccessType() != PropAccessType.SINGLE) return null;
		
		//We know we have access type SINGLE, so we can cast to Prop<M>
		PropName<? extends Prop<M>, M> sName = (PropName<? extends Prop<M>, M>)displayedName;
		return viewForSingle(model, sName);
	}

	//We need to suppress warnings on casting propnames. Note that this is safe
	//since we can check both parametric types involved in the cast, against
	//the prop info and propname class
	@SuppressWarnings("unchecked")
	private <P extends Prop<M>, M> JView<?> viewForSingle(Reference<? extends Bean> model,
			PropName<P, M> displayedName) {
		
		P prop = model.value().get().props().get(displayedName);
		
		//We can only deal with single access props
		if (prop.getInfo().getAccessType() != PropAccessType.SINGLE) return null;
		
		//If the prop is non-editable, display as a label
		if (prop.getInfo().getEditability() == PropEditability.DEFAULT) {
			return LabelPropView.create(model, displayedName);
		//If prop is editable, try to use a known editor type
		} else if (prop.getInfo().getEditability() == PropEditability.EDITABLE) {
			Class<?> c = prop.getName().getPropClass();
			if (c.equals(Double.class)) {
				return NumberSpinnerEditor.createDouble(model, (PropName<? extends EditableProp<Double>, Double>) displayedName);
			} else if (c.equals(Float.class)) {
				return NumberSpinnerEditor.createFloat(model, (PropName<? extends EditableProp<Float>, Float>) displayedName);
			} else if (c.equals(Integer.class)) {
				return NumberSpinnerEditor.createInteger(model, (PropName<? extends EditableProp<Integer>, Integer>) displayedName);
			} else if (c.equals(Long.class)) {
				return NumberSpinnerEditor.createLong(model, (PropName<? extends EditableProp<Long>, Long>) displayedName);
			} else if (c.equals(String.class)) {
				return StringTextFieldEditor.create(model, (PropName<? extends EditableProp<String>, String>) displayedName);
			} else if (c.equals(Boolean.class)) {
				return BooleanCheckboxEditor.create(model, (PropName<? extends EditableProp<Boolean>, Boolean>) displayedName);
				
			//If we have no specific editor, just display value
			} else {
				return LabelPropView.create(model, displayedName);
			}
		} else {
			return null;
		}
	}
}
