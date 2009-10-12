package org.jpropeller.view.impl;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdateManager;

/**
 * Uses a boolean {@link Prop} to choose between two
 * different views to display - one is shown when the 
 * boolean {@link Prop} is true, and the other when false.
 * 
 * The state of the boolean {@link Prop} is NOT displayed.
 */
public class OptionView implements JView, ChangeListener {

	private final Prop<Boolean> option;

	private final CardLayout layout;
	private final JPanel panel;
	
	private final CompositeViewHelper helper;	
	private final UpdateManager updateManager;
	
	/**
	 * Make an {@link OptionView} that displays nothing when the
	 * option is false.
	 * @param option		The {@link Boolean} {@link Prop} used to select
	 * 						whether to display the view
	 * @param view		The view displayed when option is true.
	 * 						If null, an empty panel is displayed instead.
	 */
	public OptionView(Prop<Boolean> option, JView view) {
		this(option, view, null);
	}
	
	/**
	 * Make an {@link OptionView}
	 * @param option		The {@link Boolean} {@link Prop} used to select
	 * 						which view to display
	 * @param trueView		The view displayed when option is true.
	 * 						If null, an empty panel is displayed instead.
	 * @param falseView		The view displayed when option is false
	 * 						If null, an empty panel is displayed instead.
	 */
	public OptionView(Prop<Boolean> option, JView trueView, JView falseView) {
		
		this.option = option;
		
		List<View> views = new ArrayList<View>(2);
		if (trueView != null) views.add(trueView);
		if (falseView != null) views.add(falseView);
		helper = new CompositeViewHelper(views);

		//Make cards to show views
		layout = new CardLayout();
		panel = new JPanel(layout);
		panel.add(nullViewIsPanel(trueView), Boolean.TRUE.toString());
		panel.add(nullViewIsPanel(falseView), Boolean.FALSE.toString());
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		//Start out updated
		updateManager.updateRequiredBy(this);

		//Listen to the model selection
		option.features().addListener(this);
	}
	
	private final static JComponent nullViewIsPanel(JView view) {
		if (view == null) {
			return new JPanel();
		} else {
			return view.getComponent();
		}
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		//Show correct card
		Boolean b = option.get();
		if (b == null) b = false;
		layout.show(panel, b.toString());

		helper.update();
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		option.features().removeListener(this);

		helper.dispose();
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		return true;
	}
	
	//JView methods delegated to helper
	
	@Override
	public void cancel() {
		helper.cancel();
	}

	@Override
	public void commit() throws CompletionException {
		helper.commit();
	}

	@Override
	public boolean isEditing() {
		return helper.isEditing();
	}
	
	@Override
	public Format format() {
		return Format.LARGE;
	}

}
