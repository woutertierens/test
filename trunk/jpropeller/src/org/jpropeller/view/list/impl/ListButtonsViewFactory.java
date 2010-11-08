package org.jpropeller.view.list.impl;

import java.awt.GridLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.ListAndSelectionReference;
import org.jpropeller.util.Source;
import org.jpropeller.util.Target;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.CompositeView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A Factory producing {@link JView}s of lists,
 * allowing for elements to be added, deleted
 * and moved.
 */
public class ListButtonsViewFactory {

	/**
	 * Layouts for buttons
	 */
	public enum ButtonsViewLayout {
		/**
		 * Buttons horizontal
		 */
		HORIZONTAL,
		/**
		 * Buttons vertical
		 */
		VERTICAL
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source) {
		return makeView(model, source, null, true, ButtonsViewLayout.HORIZONTAL);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @param locked		{@link Prop} that controls editing - when true, buttons are
	 * 						disabled, otherwise enabled. If null, editing is always enabled.  
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Prop<Boolean> locked) {
		return makeView(model, source, null, true, ButtonsViewLayout.HORIZONTAL, locked);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @param target 		The target to which to put elements removed from the list 
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target) {
		return makeView(model, source, target, true, ButtonsViewLayout.HORIZONTAL);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @param target 		The target to which to put elements removed from the list
	 * @param locked		{@link Prop} that controls editing - when true, buttons are
	 * 						disabled, otherwise enabled. If null, editing is always enabled.  
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target, Prop<Boolean> locked) {
		return makeView(model, source, target, true, ButtonsViewLayout.HORIZONTAL, locked);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @param target 		The target to which to put elements removed from the list 
	 * @param textLabels	True to show text on buttons
	 * @param layout		{@link ButtonsViewLayout} for this view
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target, boolean textLabels, ButtonsViewLayout layout) {
		return makeView(model, source, target, textLabels, layout, null);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @param target 		The target to which to put elements removed from the list 
	 * @param textLabels	True to show text on buttons
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target, boolean textLabels) {
		return makeView(model, source, target, textLabels, ButtonsViewLayout.HORIZONTAL, null);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list, or null to have
	 * 						no add button
	 * @param target 		The target to which to put elements removed from the list 
	 * @param textLabels	True to show text on buttons
	 * @param layout		{@link ButtonsViewLayout} for this view
	 * @param locked		{@link Prop} that controls editing - when true, buttons are
	 * 						disabled, otherwise enabled. If null, editing is always enabled.
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target, boolean textLabels, ButtonsViewLayout layout, Prop<Boolean> locked) {
		return makeView(model, source, target, textLabels, layout, locked, null);
	}
	
	/**
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list, or null to have
	 * 						no add button
	 * @param target 		The target to which to put elements removed from the list 
	 * @param textLabels	True to show text on buttons
	 * @param layout		{@link ButtonsViewLayout} for this view
	 * @param locked		{@link Prop} that controls editing - when true, buttons are
	 * 						disabled, otherwise enabled. If null, editing is always enabled.
	 * @param postAddTarget	{@link Target} to which new list elements are passed just after they are added
	 * 						to the list.  
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target, boolean textLabels, ButtonsViewLayout layout, Prop<Boolean> locked, Target<T> postAddTarget) {

		//Keep list of all views
		List<View> views = new LinkedList<View>();

		ListMoveAction<T> moveUpAction = ListMoveAction.createUpAction(model, locked);
		ListMoveAction<T> moveDownAction = ListMoveAction.createDownAction(model, locked);
		ListDeleteAction<T> deleteAction = ListDeleteAction.create(model, target, locked);
		
		views.add(moveUpAction);
		views.add(moveDownAction);
		views.add(deleteAction);

		JButton moveUp = new JButton(moveUpAction);
		JButton moveDown = new JButton(moveDownAction);
		JButton delete = new JButton(deleteAction);

		JButton add = null;
		if (source != null) {
			ListAddAction<T> addAction = ListAddAction.create(model, source, locked, postAddTarget);
			views.add(addAction);
			add = new JButton(addAction);
		}
		
		if (!textLabels) {
			shrinkButton(moveUp);
			shrinkButton(moveDown);
			shrinkButton(delete);
			if (add != null) {
				shrinkButton(add);
			}
		}
		
		JPanel panel;
		
		if (layout == ButtonsViewLayout.HORIZONTAL) {
			panel = new JPanel(new GridLayout(1, 4, 3, 3));
		} else {
			panel = new JPanel(new GridLayout(4, 1, 3, 3));
		}
		if (add != null) {
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
