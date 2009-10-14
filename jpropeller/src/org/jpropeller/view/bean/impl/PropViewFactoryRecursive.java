package org.jpropeller.view.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.PathReference;
import org.jpropeller.reference.impl.PathReferenceBuilder;
import org.jpropeller.view.JView;
import org.jpropeller.view.bean.impl.PropViewFactory;
import org.jpropeller.view.bean.impl.PropViewFactoryDefault;
import org.jpropeller.view.impl.LabelPropView;

/**
 * An implementation of {@link PropViewFactory} providing
 * views for props containing primitives or beans (Where beans are
 * contained in further BeanEditors recursively)
 */
public class PropViewFactoryRecursive implements PropViewFactory {

	private PropViewFactory basicFactory;

	/**
	 * Create a new {@link PropViewFactoryRecursive},
	 * based on a default {@link PropViewFactoryDefault}
	 */
	public PropViewFactoryRecursive() {
		this(new PropViewFactoryDefault(false));
	}
	
	/**
	 * Create a new {@link PropViewFactoryRecursive} based
	 * on a provided factory. Note that this factory will only
	 * return recursive views for {@link Bean}s that do NOT
	 * have a view provided by the basicFactory
	 * @param basicFactory		{@link PropViewFactory} to provide
	 * 							default views for props
	 */
	public PropViewFactoryRecursive(final PropViewFactory basicFactory) {
		this.basicFactory = basicFactory;
	}
	
	//We check that displayedName is a SINGLE access type, and so has
	//a prop type that extends Prop before casting
	//We need to suppress warnings on casting propnames. Note that this is safe
	//since we can check both parametric types involved in the cast, against
	//the prop info and propname class
	@SuppressWarnings("unchecked")
	@Override
	public <M> JView viewFor(final Reference<? extends Bean> model,
			final PropName<M> displayedName) {
		
		JView view = basicFactory.viewFor(model, displayedName);
		
		if (!displayedName.isTGeneric() && Bean.class.isAssignableFrom(displayedName.getPropClass())) {
			final PathReference<Bean> ref = 
				PathReferenceBuilder.fromRef(Bean.class, model).to((PropName<Bean>)displayedName);
			
			if(view == null) {
				view = RecursiveBeanEditor.create(ref, this, false);
			}
			
			return view;
		} else {
			if(view != null) {
				return view;
			} else {
				System.out.println("Could not find view for " + displayedName.getPropClass());
				return LabelPropView.create(model, displayedName);
			}
					
					
		}
	}

	@Override
	public boolean providesFor(PropName<?> displayedName) {
		return basicFactory.providesFor(displayedName);
	}

}
