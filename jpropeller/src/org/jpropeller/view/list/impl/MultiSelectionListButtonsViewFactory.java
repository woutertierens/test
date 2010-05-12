package org.jpropeller.view.list.impl;

import java.awt.GridLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.util.Source;
import org.jpropeller.util.Target;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.CompositeView;
import org.jpropeller.view.list.impl.ListButtonsViewFactory.ButtonsViewLayout;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A Factory producing {@link JView}s of lists,
 * allowing for elements to be added, deleted
 * and moved, in accordance with a multiselection.
 */
public class MultiSelectionListButtonsViewFactory {

	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @param source 		The source of new elements to add to the list
	 * @return 				The view
	 */
	public static <T> JView makeView(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			Source<T> source) {
		return makeView(list, selection, source, null, true, ButtonsViewLayout.HORIZONTAL);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @param source 		The source of new elements to add to the list
	 * @param target 		The target to which to put elements removed from the list 
	 * @return 				The view
	 */
	public static <T> JView makeView(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			Source<T> source, Target<T> target) {
		return makeView(list, selection, source, target, true, ButtonsViewLayout.HORIZONTAL);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param list		{@link Prop} containing the {@link CList} to act on
	 * @param selection {@link Prop} containing the selected indices
	 * @param source 		The source of new elements to add to the list
	 * @param target 		The target to which to put elements removed from the list 
	 * @param textLabels	True to show text on buttons
	 * @param layout		{@link ButtonsViewLayout} for this view
	 * @return 				The view
	 */
	public static <T> JView makeView(
			Prop<? extends CList<T>> list,
			Prop<? extends CCollection<Integer>> selection,
			Source<T> source, Target<T> target, boolean textLabels, ButtonsViewLayout layout) {

		//Keep list of all views
		List<View> views = new LinkedList<View>();

		MultiSelectionListMoveAction<T> moveUpAction = MultiSelectionListMoveAction.createUpAction(list, selection);
		MultiSelectionListMoveAction<T> moveDownAction = MultiSelectionListMoveAction.createDownAction(list, selection);
		MultiSelectionListDeleteAction<T> deleteAction = MultiSelectionListDeleteAction.create(list, selection, target);

		JButton add = null;
		if (source != null) {
			MultiSelectionListAddAction<T> addAction = MultiSelectionListAddAction.create(list, selection, source);
			views.add(addAction);
			add = new JButton(addAction);
		}
		
		views.add(deleteAction);
		views.add(moveUpAction);
		views.add(moveDownAction);
		
		JButton moveUp = new JButton(moveUpAction);
		JButton moveDown = new JButton(moveDownAction);
		JButton delete = new JButton(deleteAction);
		
		if (!textLabels) {
			shrinkButton(moveUp);
			shrinkButton(moveDown);
			shrinkButton(delete);
			if (source != null) {
				shrinkButton(add);
			}
		}
		
		JPanel panel;
		
		if (layout == ButtonsViewLayout.HORIZONTAL) {
			panel = new JPanel(new GridLayout(1, 4, 3, 3));
		} else {
			panel = new JPanel(new GridLayout(4, 1, 3, 3));
		}
		
		if (source != null) {
			panel.add(add);
		}
		panel.add(delete);
		panel.add(moveUp);
		panel.add(moveDown);
		
		panel.setOpaque(false);
		
		if (layout == ButtonsViewLayout.HORIZONTAL && textLabels) {
			FormLayout formLayout = new FormLayout("fill:0dlu:grow, pref, fill:0dlu:grow");
			DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
			builder.nextColumn();
			builder.append(panel);
			panel = builder.getPanel();
			panel.setOpaque(false);
		}		

		return new CompositeView<JComponent>(views, panel, false);
		
	}

	private static void shrinkButton(JButton b) {
		b.setText("");
		b.setMargin(new Insets(0,0,0,0));
	}
	
}
