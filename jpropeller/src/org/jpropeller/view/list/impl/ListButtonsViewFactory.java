package org.jpropeller.view.list.impl;

import java.awt.GridLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.collection.CList;
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
	 * Make a {@link JView} for {@link CList}s
	 * 
	 * @param <T>			The type of list contents
	 * 
	 * @param model 		The model to be viewed
	 * @param source 		The source of new elements to add to the list
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source) {
		return makeView(model, source, null, true, false);
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
		return makeView(model, source, target, true, false);
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
	 * @param squareLayout	True to layout in a square, false to use a horizontal line
	 * @return 				The view
	 */
	public static <T> JView makeView(ListAndSelectionReference<T> model, Source<T> source, Target<T> target, boolean textLabels, boolean squareLayout) {

		//Keep list of all views
		List<View> views = new LinkedList<View>();

		ListMoveAction<T> moveUpAction = ListMoveAction.createUpAction(model);
		ListMoveAction<T> moveDownAction = ListMoveAction.createDownAction(model);
		ListDeleteAction<T> deleteAction = ListDeleteAction.create(model, target);
		ListAddAction<T> addAction = ListAddAction.create(model, source);
		
		views.add(moveUpAction);
		views.add(moveDownAction);
		views.add(deleteAction);
		views.add(addAction);
		
		JButton moveUp = new JButton(moveUpAction);
		JButton moveDown = new JButton(moveDownAction);
		JButton delete = new JButton(deleteAction);
		JButton add = new JButton(addAction);
		
		if (!textLabels) {
			shrinkButton(moveUp);
			shrinkButton(moveDown);
			shrinkButton(delete);
			shrinkButton(add);
		}
		
		JPanel panel;
		
		if (!textLabels) {
			panel = new JPanel(new GridLayout(1, 4, 3, 3));
			panel.add(moveUp);
			panel.add(moveDown);
			panel.add(add);
			panel.add(delete);
		} else {
			FormLayout layout = new FormLayout("fill:0dlu:grow, pref, $lcgap, pref, $lcgap, pref, $lcgap, pref, fill:0dlu:grow");
			layout.setColumnGroups(new int[][]{{2,4,6,8}});
			DefaultFormBuilder builder = new DefaultFormBuilder(layout);
			builder.nextColumn();
			builder.append(moveUp);
			builder.append(moveDown);
			builder.append(add);
			builder.append(delete);
			panel = builder.getPanel();
		}
		
		panel.setOpaque(false);
		
		return new CompositeView<JComponent>(views, panel, false);
		
	}

	private static void shrinkButton(JButton b) {
		b.setText("");
		b.setMargin(new Insets(0,0,0,0));
	}
	
}
