package org.jpropeller.view.impl;

import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.info.Illustrated;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} of any Object, which will display its
 * {@link Object#toString()} result as a {@link JLabel}
 */
public class LabelView implements JView, ChangeListener {

	private Prop<?> prop;
	private UpdateManager updateManager;
	private JLabel label;

	
	/**
	 * Create a {@link LabelView}
	 * 
	 * @param prop		The {@link Prop} to display
	 */
	public LabelView(Prop<?> prop) {
		super();
		this.prop = prop;
		
		label = new JLabel();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to the prop
		prop.features().addListener(this);

		//Initial update
		update();
	}
	
	/**
	 * Create a {@link LabelView}
	 * 
	 * @param ref		The reference to display
	 */
	public LabelView(Reference<?> ref) {
		this(ref.value());
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Always update - we only listen to the value, so we only see
		//a change when we need to update
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		Object value = prop.get();
		
		String s = "";
		if (value != null) {
			if(value instanceof Illustrated) {
				label.setIcon(((Illustrated)value).illustration().get());
			} 
			s = value.toString();
			if(value instanceof Icon) {
				label.setIcon((Icon)value);
				s = "";
			}
		}
		
		if (!s.equals(label.getText())) {
			label.setText(s);
		}
	}

	@Override
	public JComponent getComponent() {
		return label;
	}

	@Override
	public boolean selfNaming() {
		return false;
	}

	@Override
	public void cancel() {
		//Never edits - nothing to do
	}

	@Override
	public void commit() throws CompletionException {
		//Never edits - nothing to do
	}

	@Override
	public boolean isEditing() {
		//Never edits
		return false;
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		prop.features().removeListener(this);
	}

	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}

}
