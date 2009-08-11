package org.jpropeller.view.impl;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.SingleValueView;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} of any Object, which will display its
 * {@link Object#toString()} result as a {@link JLabel}
 */
public class LabelView implements JView, SingleValueView<Object>, ChangeListener {

	private Reference<?> ref;
	private UpdateManager updateManager;
	private JLabel label;

	/**
	 * Create a {@link LabelView}
	 * 
	 * @param ref		The reference to display
	 */
	public LabelView(Reference<?> ref) {
		super();
		this.ref = ref;
		
		label = new JLabel();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to just the value of the reference, update when it changes
		ref.value().features().addListener(this);

		//Initial update
		update();
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Always update - we only listen to the value, so we only see
		//a change when we need to update
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		Object value = ref.value().get();
		
		String s = "";
		if (value != null) {
			s = value.toString();
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
	public Reference<?> getModel() {
		return ref;
	}

	@Override
	public boolean isEditing() {
		//Never edits
		return false;
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		ref.features().removeListener(this);
	}

	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}

}
