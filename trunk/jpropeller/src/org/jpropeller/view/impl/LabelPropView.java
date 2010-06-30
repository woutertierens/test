package org.jpropeller.view.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.transformer.Transformer;
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

	private final PropViewHelp<Bean, T> help;
	private final Reference<? extends Bean> model;

	private final JLabel label;

	private final Transformer<T, String> formatter;

	private LabelPropView(Reference<? extends Bean> model, PropName<T> displayedName) {
		this(model, displayedName, null);
	}
	
	private LabelPropView(Reference<? extends Bean> model, PropName<T> displayedName, Transformer<T, String> formatter) {
		super();
		this.model = model;
		if (formatter != null) {
			this.formatter = formatter;
		} else {
			this.formatter = new DefaultFormatter();
		}
		
		label = new JLabel();
		label.setBorder(new EmptyBorder(5, 6, 5, 0));

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
	 * @param <T>			The type of value in the displayed prop 
	 * @param model 		The model {@link Reference} for this {@link View}
	 * @param displayedName	The name of the displayed property 
	 * @return				A {@link LabelPropView} 
	 */
	public static <T> LabelPropView<T> create(Reference<? extends Bean> model, PropName<T> displayedName) {
		return new LabelPropView<T>(model, displayedName);
	}

	/**
	 * Create a {@link LabelPropView}
	 * @param <T>			The type of value in the displayed prop 
	 * @param model 		The model {@link Reference} for this {@link View}
	 * @param displayedName	The name of the displayed property 
	 * @param formatter		{@link Transformer} used to convert from model values 
	 * 						to corresponding displayed strings.
	 * @return				A {@link LabelPropView} 
	 */
	public static <T> LabelPropView<T> create(Reference<? extends Bean> model, PropName<T> displayedName, Transformer<T, String> formatter) {
		return new LabelPropView<T>(model, displayedName, formatter);
	}

	/**
	 * Create a {@link LabelPropView} that will convert zero-based indices in model
	 * into 1-based indices for display.
	 * @param model 		The model {@link Reference} for this {@link View}
	 * @param displayedName	The name of the displayed property 
	 * @return				A {@link LabelPropView} 
	 */
	public static LabelPropView<Integer> createOneBasedDisplayInteger(Reference<? extends Bean> model, PropName<Integer> displayedName) {
		return new LabelPropView<Integer>(model, displayedName, new Transformer<Integer, String>() {
			@Override
			public String transform(Integer s) {
				if (s == null) {
					return "";
				} else {
					return "" + (s + 1);
				}
			}
		});
	}

	/**
	 * Create a {@link LabelPropView} that will convert zero-based indices in model
	 * into 1-based indices for display.
	 * @param model 		The model {@link Reference} for this {@link View}
	 * @param displayedName	The name of the displayed property 
	 * @return				A {@link LabelPropView} 
	 */
	public static LabelPropView<Long> createOneBasedDisplayLong(Reference<? extends Bean> model, PropName<Long> displayedName) {
		return new LabelPropView<Long>(model, displayedName, new Transformer<Long, String>() {
			@Override
			public String transform(Long s) {
				if (s == null) {
					return "";
				} else {
					return "" + (s + 1);
				}
			}
		});
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
		
		int newHorizAlignment;
		if (t instanceof Number) {
			newHorizAlignment = SwingConstants.TRAILING;
		} else {
			newHorizAlignment = SwingConstants.LEADING;
		}
		if (label.getHorizontalAlignment() != newHorizAlignment) {
			label.setHorizontalAlignment(newHorizAlignment);
		}

		String s = formatter.transform(t);

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

	/**
	 * Default formatter for a {@link LabelPropView}
	 */
	private final class DefaultFormatter implements Transformer<T, String> {

		private final NumberFormat format = new DecimalFormat("0.####");

		@Override
		public String transform(T t) {
			String s;
			if (t instanceof Number) {
				s = format.format(((Number)t).doubleValue());
			} else {
				s = (t==null) ? "" : t.toString();
			}
			return s;
		}
		
	}
	
}
