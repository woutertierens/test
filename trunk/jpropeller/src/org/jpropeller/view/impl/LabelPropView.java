package org.jpropeller.view.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;

/**
 * A non-editing {@link View} for a single {@link Prop} of any
 * type in a {@link Bean}
 * @param <T>
 * 		The type of value in the displayed prop 
 */
public class LabelPropView<T> implements JView, UpdatableSingleValueView<Bean> {

	PropViewHelp<Bean, T> help;
	Reference<? extends Bean> model;
	PropName<T> displayedName;

	JLabel label;

	NumberFormat format = new DecimalFormat("0.####");
	
	private LabelPropView(Reference<? extends Bean> model, PropName<T> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		label = new JLabel();

		//Ignore locking, since we do not ever edit
		help = new PropViewHelp<Bean, T>(this, displayedName, null);
		help.connect();
	}

	@Override
	public void dispose() {
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
	public static <T> LabelPropView<T> create(Reference<? extends Bean> model, PropName<T> displayedName) {
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
	public void update() {
		T t = help.getPropValue();
		
		if (t == null) return;
		
		String s;
		
		if (t instanceof Number) {
			s = format.format(((Number)t).doubleValue());
			label.setHorizontalAlignment(SwingConstants.TRAILING);
		} else {
			s = (t==null) ? "" : t.toString();
			label.setHorizontalAlignment(SwingConstants.LEADING);
		}
		
		if (!label.getText().equals(s)) {
			label.setText(s);
		}		
	}

	@Override
	public boolean selfNaming() {
		//Just displays value
		return false;
	}

	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}

}
