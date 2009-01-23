package org.jpropeller.view.list.impl;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.list.ListSelectionEditableValueReference;
import org.jpropeller.properties.list.impl.ListSelectionEditableValueReferenceDefault;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.PathReference;
import org.jpropeller.reference.impl.PathReferenceBuilder;
import org.jpropeller.transformer.BeanPathToEditable;
import org.jpropeller.ui.external.SimpleInternalFrame;
import org.jpropeller.util.Source;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.bean.impl.BeanPropListEditor;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.impl.ListAddAction;
import org.jpropeller.view.table.impl.ListDeleteAction;
import org.jpropeller.view.table.impl.ListMoveAction;
import org.jpropeller.view.table.impl.SingleSelectionListTableView;
import org.jpropeller.view.update.Updatable;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A {@link JView} for {@link ObservableList}s of {@link Bean}s, displaying
 * the entire list as a table, and using
 * a {@link BeanPropListEditor} to edit the selected instance, and providing
 * buttons to allow list elements to be added, deleted, and moved.
 * @param <T>
 * 		The type of {@link Bean} in the {@link ListEditView}
 */
public class ListEditView<T extends Bean> implements JView<ObservableList<T>>{

	ListSelectionEditableValueReference<T> model;
	TableRowView<? super T> rowView;
	SingleSelectionListTableView<T> tableView;
	Source<T> source;
	Class<T> clazz;
	JComponent panel;
	private JComponent buttonPanel;
	private JComponent editorPanel;
	private BeanPropListEditor<T> selectedEditor;
	private ListMoveAction<T> moveUpAction;
	private ListMoveAction<T> moveDownAction;
	private ListDeleteAction deleteAction;
	private ListAddAction<T> addAction;
	private JPanel tablePanel;
	
	List<View<?>> views = new LinkedList<View<?>>();
	
	/**
	 * Create a {@link ListEditView}
	 * @param model
	 * 		The model to be edited
	 * @param clazz
	 * 		The class of list elements
	 * @param rowView
	 * 		A view to convert from list elements to rows of the table
	 * @param source
	 * 		The source for new elements to add to the list
	 */
	public ListEditView(ListSelectionEditableValueReference<T> model, Class<T> clazz, TableRowView<? super T> rowView, Source<T> source) {
		this.model = model;
		this.rowView = rowView;
		this.source = source;
		this.clazz = clazz;

		//Make table view, put table in a scroll pane
		tableView = new SingleSelectionListTableView<T>(model, rowView);
		JTable table = tableView.getComponent();
		JScrollPane tableScroll = new JScrollPane(table);
		tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tableScroll);
		//tablePanel.setBorder(Borders.DIALOG_BORDER);
		views.add(tableView);
		
		//Panel with list editing buttons
		buttonPanel = buildButtonPanel();
	
		//Panel to edit current selection
		editorPanel = buildEditorPanel();

		//Left and right sections of main panel
		JComponent leftPanel = buildLeftPanel();
		JComponent rightPanel = buildRightPanel();
		
		//Build the entire panel
		FormLayout layout = new FormLayout(
				"fill:default:grow, 7dlu, 175dlu",
				"fill:default:grow");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.append(leftPanel);
		builder.append(rightPanel);
		
		panel = builder.getPanel();
		
	}

	private JComponent buildLeftPanel() {
		//Build the entire panel
		FormLayout layout = new FormLayout(
				"fill:default:grow",
				"fill:default:grow, 7dlu, default");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append(tablePanel);
		builder.nextRow();
		builder.append(buttonPanel);
		
		SimpleInternalFrame sif = new SimpleInternalFrame("List");
		sif.add(builder.getPanel());
		
		return sif;
	}

	private JComponent buildRightPanel() {
		SimpleInternalFrame sif = new SimpleInternalFrame("Edit Selected Item");
		sif.add(editorPanel);
		return sif;
	}

	private JComponent buildEditorPanel() {
		//Make a transform from the list reference to the selected value
		BeanPathToEditable<ListSelectionEditableValueReference<T>, T> refToSelected = 
			ListSelectionEditableValueReferenceDefault.transformerToSelectedValue();
		
		//Editor for current selection
		final PathReference<T> selectedReference = PathReferenceBuilder.from(model, clazz).to(refToSelected);
		selectedEditor = BeanPropListEditor.create(selectedReference);
		JScrollPane selectedEditorScroll = new JScrollPane(selectedEditor.getComponent());
		selectedEditorScroll.setBorder(null);
		
		views.add(selectedEditor);
		
		return selectedEditorScroll;
	}
	
	private JComponent buildButtonPanel() {
		moveUpAction = ListMoveAction.createUpAction(model);
		moveDownAction = ListMoveAction.createDownAction(model);
		deleteAction = ListDeleteAction.create(model);
		addAction = ListAddAction.create(model, source);
		
		views.add(moveUpAction);
		views.add(moveDownAction);
		views.add(deleteAction);
		views.add(addAction);
		
		JButton moveUp = new JButton(moveUpAction);
		JButton moveDown = new JButton(moveDownAction);
		JButton delete = new JButton(deleteAction);
		JButton add = new JButton(addAction);
		
		FormLayout layout = new FormLayout("fill:0dlu:grow, pref, $lcgap, pref, $lcgap, pref, $lcgap, pref, fill:0dlu:grow");
		layout.setColumnGroups(new int[][]{{2,4,6,8}});
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.nextColumn();
		builder.append(moveUp);
		builder.append(moveDown);
		builder.append(add);
		builder.append(delete);
		
		return builder.getPanel();
	}
	
	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		return true;
	}

	@Override
	public void cancel() {
		for (View<?> view : views) {
			view.cancel();
		}
	}

	@Override
	public void commit() throws CompletionException {
		for (View<?> view : views) {
			view.commit();
		}
	}

	@Override
	public Reference<? extends ObservableList<T>> getModel() {
		return model;
	}

	@Override
	public boolean isEditing() {
		boolean editing = false;
		for (View<?> view : views) {
			if (view.isEditing()) editing = true;
		}
		return editing;
	}

	@Override
	public void dispose() {
		for (View<?> view : views) {
			if (view instanceof Updatable) {
				((Updatable)view).dispose();
			}
		}
	}

	@Override
	public void update() {
		//Subeditors will update themselves
	}

}
