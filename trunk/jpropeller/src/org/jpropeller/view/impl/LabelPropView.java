package org.jpropeller.view.impl;

import javax.swing.JLabel;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdatableView;

/**
 * A non-editing {@link View} for a single {@link Prop} of any
 * type in a {@link Bean}
 * @param <T>
 * 		The type of value in the displayed prop 
 */
public class LabelPropView<T> implements UpdatableView<Bean>, JView<Bean> {

	PropViewHelp<Bean, T> help;
	Reference<? extends Bean> model;
	PropName<? extends Prop<T>, T> displayedName;

	JLabel label;

	private LabelPropView(Reference<? extends Bean> model, PropName<? extends Prop<T>, T> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		label = new JLabel();

		help = new PropViewHelp<Bean, T>(this, displayedName, null);
		help.connect();
	}

	@Override
	public void disposeNow() {
		help.dispose();
	}

	/**
	 * Create a {@link LabelPropView}
	 * @param <T>
	 * 		The type of value in the displayed prop 
	 * @param model 
	 * 		The model {@link Reference} for this {@link View}
	 * @param displayedName
	 * 		The name of the displayed property 
	 * @return
	 * 		A {@link LabelPropView} 
	 */
	public static <T> LabelPropView<T> create(Reference<? extends Bean> model, PropName<? extends Prop<T>, T> displayedName) {
		return new LabelPropView<T>(model, displayedName);
	}
	
	@Override
	public Reference<? extends Bean> getModel() {
		return model;
	}

	/**
	 * Get the {@link JLabel} used for display/editing
	 * @return
	 * 		The label
	 */
	public JLabel getComponent() {
		return label;
	}
	
	@Override
	public void cancel() {
		//Nothing to do, view only
	}

	@Override
	public void commit() {
		//Nothing to do, view only
	}

	@Override
	public boolean isEditing() {
		//Never editing, view only
		return false;
	}

	@Override
	public void updateNow() {
		T t = help.getPropValue();
		
		String s = (t==null) ? "" : t.toString();
		
		if (!label.getText().equals(s)) {
			label.setText(s);
		}		
	}

}
