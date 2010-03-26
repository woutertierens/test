package org.jpropeller.view.primitive.impl;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;

/**
 * JSpinner that displays a Prop<Enum>
 * @param <T> Type of enum.
 */
public class EnumPropSpinnerEditor<T extends Enum<T>> implements JView, org.jpropeller.properties.change.ChangeListener {

	private final Prop<T> model;
	private final T[] values;
	private final EnumSpinnerModel spinnerModel;
	private JSpinner spinner;
	private final Prop<Boolean> locked;
	private final Class<T> type;
	
	private class EnumSpinnerModel extends AbstractSpinnerModel {

		private int current = 0;
		private T value;
		
		private EnumSpinnerModel() {
			value = values[0];
		}
		
		private int delta(final int delta) {
			int next = (current+delta);
			if(next < 0) {
				return values.length-1;
			} else {
				return next % values.length;
			}
		}
		
		@Override
		public Object getNextValue() {
			return values[delta(1)];
		}

		@Override
		public Object getPreviousValue() {
			return values[delta(-1)];
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			if (value == null) return;
			if(type.isAssignableFrom(value.getClass())) {
				this.value = type.cast(value);
				this.current = this.value.ordinal();
				fireStateChanged();
			}
		}
		
	}

	/**
	 * Create a new editor.
	 * @param type		The type of the enum.
	 * @param values	The possible values of the enum (can't find via reflection).
	 * @param model		The prop to edit.
	 */
	public EnumPropSpinnerEditor(Class<T> type, T[] values, final Prop<T> model) {
		this(type, values, model, null);
	}
	/**
	 * Create a new editor.
	 * @param type		The type of the enum.
	 * @param values	The possible values of the enum (can't find via reflection).
	 * @param model		The prop to edit.
	 * @param locked	Prop containing true if editor should NOT edit, or null to ignore
	 */
	public EnumPropSpinnerEditor(Class<T> type, T[] values, final Prop<T> model, final Prop<Boolean> locked) {
		this.type = type;
		this.model = model;
		this.values = values;
		this.spinnerModel = new EnumSpinnerModel();
		this.spinner = new JSpinner(spinnerModel);
		applyFocusFix(spinner);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				commit();
			}
		});
		model.features().addListener(this);
		
		this.locked = locked;
		if (locked != null) {
			locked.features().addListener(this);
		}
		
		Props.getPropSystem().getUpdateManager().registerUpdatable(this);
	}
	
	private void applyFocusFix(final JSpinner spinner) {
		final JComponent editor = spinner.getEditor();
		
		//If the editor is a default editor, we can work around
		//failure to commit when selecting a menu (and possibly other
		//problems) by monitoring the editor, and committing its edits when it loses
		//focus. This should happen automatically, and sometimes does by some
		//means I have not yet located, but fails when immediately selecting a menu 
		//after editing, etc.
		if (editor instanceof DefaultEditor) {
			final DefaultEditor dEditor = (DefaultEditor)editor;
			dEditor.getTextField().addFocusListener(new FocusListener() {
				
				//On focus lost, try to commit any pending edit.
				//If this fails, then revert editor to the actual
				//current value to avoid confusion
				public void focusLost(FocusEvent e) {
					try {
						spinner.commitEdit();
					} catch (ParseException e1) {
						cancel();
					}
				}
				
				//Do nothing on focus gained
				public void focusGained(FocusEvent e) {
				}
			});
		}
	}
	
	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}

	@Override
	public JComponent getComponent() {
		return spinner;
	}

	@Override
	public boolean selfNaming() {
		return false;
	}

	@Override
	public void cancel() {
		//No such thing!
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void commit()  {
		if(model.get() != spinnerModel.getValue()) {
			model.set((T) spinnerModel.getValue());
		}
	}

	@Override
	public boolean isEditing() {
		return false;
	}

	@Override
	public void dispose() {
		model.features().removeListener(this);
		if (locked != null) {
			locked.features().removeListener(this);
		}
		Props.getPropSystem().getUpdateManager().deregisterUpdatable(this);
	}

	@Override
	public void update() {
		if(spinnerModel.getValue() != model.get()) {
			spinnerModel.setValue(model.get());
		}
		spinner.setEnabled(!Props.isTrue(locked));
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		Props.getPropSystem().getUpdateManager().updateRequiredBy(this);
	}
	
}
