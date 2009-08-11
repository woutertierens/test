package org.jpropeller.view.action.impl;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.reference.Reference;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.JViewSource;
import org.jpropeller.view.SingleValueView;
import org.jpropeller.view.Views;
import org.jpropeller.view.bean.impl.PropViewAdaptor;
import org.jpropeller.view.bean.impl.PropViewFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A {@link JView} that ignores its model (which can be any type),
 * and simply displays some {@link Action}s as {@link JButton}s.
 *
 * @param <T>		The type of value in the model {@link Reference}
 */
public class ButtonView<T> implements JView, SingleValueView<T> {

	private Reference<? extends T> model;
	private final JPanel panel;
	
	/**
	 * Create a {@link ButtonView}
	 * @param model		The model - contents are ignored
	 * @param actions	The actions - each will be displayed as a button in a vertical layout.
	 */
	public ButtonView(Reference<? extends T> model, Action ...actions) {
		this.model = model;
		
		FormLayout layout = new FormLayout(Views.getViewSystem().getStandard3ColumnDefinition());
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		
		for(Action action : actions) {
			builder.append(new JButton(action), 3);
			builder.nextLine();
		}
		
		panel = builder.getPanel();
	}
	
	
	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		return false;
	}

	@Override
	public void cancel() {
		//Instant
	}

	@Override
	public void commit() throws CompletionException {
		//Instant
	}

	@Override
	public Reference<? extends T> getModel() {
		return model;
	}

	@Override
	public boolean isEditing() {
		return false;
	}

	@Override
	public void dispose() {
		//Instant
	}

	@Override
	public void update() {
		//Instant
	}
	
	/**
	 * Create a new {@link JViewSource} that will provide
	 * a {@link ButtonView} containing specified {@link Action}s
	 * for any model
	 * @param <T>		The type of value in model
	 * @param actions	The {@link Action}s to be used in the {@link ButtonView}s
	 * @return			A source of {@link ButtonView}s
	 */
	public static <T> JViewSource<T> asSource(final Action ... actions) {
		return new JViewSource<T>() {
			@Override
			public JView get(Reference<? extends T> model) {
				return new ButtonView<T>(model, actions);
			}
		};		
	}
	
	/**
	 * Create a new {@link PropViewFactory} that will provide
	 * a {@link ButtonView} containing specified {@link Action}s
	 * as a prop view
	 * @param actions	The {@link Action}s to be used in the {@link ButtonView}s
	 * @return			A source of {@link ButtonView}s
	 */
	public static PropViewFactory asFactory(final Action ... actions) {
		return PropViewAdaptor.factoryFor(asSource(actions));		
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}

}
