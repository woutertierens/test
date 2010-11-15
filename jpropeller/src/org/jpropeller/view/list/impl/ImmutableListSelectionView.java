package org.jpropeller.view.list.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.system.Props;
import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.ui.impl.JTabButton;
import org.jpropeller.view.JView;
import org.jpropeller.view.Views;
import org.jpropeller.view.update.UpdateManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A {@link JView} of a {@link Prop}, allowing
 * selection of a value for that {@link Prop} from
 * an immutable {@link List} of options.
 * Selection uses a list of toggle buttons.
 * When value of the {@link Prop} is within the {@link List},
 * this is displayed by selection of the corresponding
 * toggle button - otherwise toggle buttons are all
 * unselected.  
 * 
 * TODO make renderer update on changes to any of the selected values
 * TODO make this work with a CList of selections - this will make
 * it very like a ListView, but with the difference that it can
 * cope with the model {@link Prop} having a value outside the list,
 * and that it makes the {@link Prop} the primary (most important) data item, 
 * rather than just a selection from a primary {@link CList}.
 * 
 * @param <T>	The type of value in the {@link Prop} and {@link List}
 */
public class ImmutableListSelectionView<T extends Immutable> implements JView, ChangeListener {
	
	private final JComponent component;
	private final ButtonGroup group;
	private final List<T> selections;
	private final Prop<T> model;
	private UpdateManager updateManager;
	private final Prop<Boolean> locked;
	private final Map<T, JToggleButton> dbToButton;
	private boolean updating = false;

	final Color unselectedForeground = UIManager.getColor("itis.foreground.unselected");
	final Color selectedForeground = UIManager.getColor("itis.foreground.selected");

	/**
	 * {@link ImmutableListSelectionView}
	 * 
	 * @param model 					The {@link Prop} to be viewed
	 * @param possibleSelections		The list of available values for the model {@link Prop},
	 * 									in order they will be presented to user. Only values
	 * 									from this list can be selected. If the model {@link Prop}'s value
	 * 									is ever outside this list, it will not be displayed - all
	 * 									buttons will be unselected, but selecting one will still set the
	 * 									model {@link Prop}'s value.
	 * @param renderer					{@link IconAndHTMLRenderer} to render any value in possibleSelections
	 * 									to the text and icon of a {@link JLabel}. Note that the selections
	 * 									will only be rendered ONCE, at construction - this is not a live 
	 * 									view of the selections, this is why the view only accepts immutable 
	 * 									values.
	 * @param locked					If this is non-null and has value true, the view will not
	 * 									make changes to the model {@link Prop}.
	 */
	public ImmutableListSelectionView(final Prop<T> model, List<T> possibleSelections, IconAndHTMLRenderer renderer, Prop<Boolean> locked) {

		this.model = model;
		this.locked = locked;
		selections = new ArrayList<T>(possibleSelections);
		
		dbToButton = new HashMap<T, JToggleButton>();

		//Make a toggle button for each selectable value, as a radio group
		group = new ButtonGroup();
		for (final T db : selections) {
			
			final JTabButton button = new JTabButton(JTabButton.INDEPENDENT_BUTTON);
			
			final JLabel label = new JLabel(renderer.getHTML(db), renderer.getIcon(db), SwingConstants.LEFT);
			label.setBorder(new EmptyBorder(5, 2, 5, 2));
			label.setIconTextGap(16);
			button.add(label);
			
			dbToButton.put(db, button);
			
			group.add(button);
			
			button.addChangeListener(new javax.swing.event.ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					//We avoid changing prop when the button is just changing from
					//our display update
					if (!updating && button.isSelected()) {
						model.set(db);
					}
					label.setForeground(button.isSelected() ? selectedForeground : unselectedForeground);
				}
			});
		}
		
		FormLayout layout = new FormLayout(Views.getViewSystem().getStandard3ColumnDefinition());
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		
		//Toggle buttons
		for (T db : selections) {
			JToggleButton button = dbToButton.get(db);
			builder.append(button, 3);
		}

		JPanel mainPanel = builder.getPanel();
		
		component = mainPanel;
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		model.features().addListener(this);
		if (locked != null) {
			locked.features().addListener(this);
		}

		//Initial update
		update();
		
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	@Override
	public void update() {
		updating = true;
		try {
			T value = model.get();
			
			boolean isNull = value == null;
			boolean enabled = !Props.isTrue(locked) && !isNull;
	
			ButtonModel shouldBeSelected = null;
			
			for (T db : selections) {
				JToggleButton button = dbToButton.get(db);
				if (button.isEnabled() != enabled) {
					button.setEnabled(enabled);
				}
				if (db == value) {
					shouldBeSelected = button.getModel();
				}
			}
			
			if (shouldBeSelected == null) {
				if (group.getSelection() != null) {
					group.clearSelection();
				}
			} else {
				if (group.getSelection() != shouldBeSelected) {
					group.setSelected(shouldBeSelected, true);
				}
			}
		} finally {
			updating = false;
		}
	}

	@Override
	public void dispose() {
		model.features().removeListener(this);
		if (locked != null) {
			locked.features().removeListener(this);
		}
	}

	//Simple view methods

	@Override
	public void cancel() {
		//Instant editing
	}

	@Override
	public void commit() {
		//Instant editing 
	}

	@Override
	public boolean isEditing() {
		//Instant editing
		return false;
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}

	@Override
	public JComponent getComponent() {
		return component;
	}

	@Override
	public boolean selfNaming() {
		return true;
	}
	
}
