package org.jpropeller.view.list.impl;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.reference.Reference;
import org.jpropeller.transformer.BeanPathTo;
import org.jpropeller.ui.external.SimpleInternalFrame;
import org.jpropeller.util.Source;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.bean.impl.BeanEditor;
import org.jpropeller.view.impl.CompositeViewHelper;
import org.jpropeller.view.impl.FlexibleView;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.impl.SingleSelectionListTableView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A {@link JView} for {@link CList}s of {@link Bean}s, displaying
 * the entire list as a table, and using
 * a {@link BeanEditor} to edit the selected instance, and providing
 * buttons to allow list elements to be added, deleted, and moved.
 * @param <T>
 * 		The type of {@link Bean} in the {@link ListEditView}
 */
public class ListEditView<T> implements JView{

	ListAndSelectionAndValueReference<T> model;
	TableRowView<? super T> rowView;
	SingleSelectionListTableView<T> tableView;
	Source<T> source;
	Class<T> clazz;
	JComponent panel;
	private JComponent buttonPanel;
	private FlexibleView selectedView;
	private JPanel tablePanel;
	
	List<View> views = new LinkedList<View>();

	CompositeViewHelper helper;
	private Reference<T> selectedReference;
	
	/**
	 * Create a {@link ListEditView}, using a new 
	 * {@link ListAndSelectionAndValueReference}
	 * @param <S>
	 * 		The type of contents of the list 
	 * @param list
	 * 		The list to be edited
	 * @param clazz
	 * 		The class of list elements
	 * @param rowView
	 * 		A view to convert from list elements to rows of the table
	 * @param source
	 * 		The source for new elements to add to the list
	 * @param editSelected
	 * 		If true, an extra panel is shown to edit the selected item
	 * @param editedClasses
	 * 		The classes for which a specific editor will be looked up,
	 * 		to be used when an element of that class is selected
	 * @return 
	 * 		A new {@link ListEditView}
	 */
	public static <S> ListEditView<S> create(CList<S> list, Class<S> clazz, TableRowView<? super S> rowView, Source<S> source, boolean editSelected, Class<?>... editedClasses) {
		ListAndSelectionAndValueReference<S> model = 
			new ListAndSelectionAndValueReferenceDefault<S>(clazz, list);
		return new ListEditView<S>(model, clazz, rowView, source, editSelected, editedClasses);
	}
	
	/**
	 * Create a {@link ListEditView}, using a new 
	 * {@link ListAndSelectionAndValueReference}
	 * @param <S>
	 * 		The type of contents of the list 
	 * @param model
	 * 		The model to be edited
	 * @param clazz
	 * 		The class of list elements
	 * @param rowView
	 * 		A view to convert from list elements to rows of the table
	 * @param source
	 * 		The source for new elements to add to the list
	 * @param editSelected
	 * 		If true, an extra panel is shown to edit the selected item
	 * @param editedClasses
	 * 		The classes for which a specific editor will be looked up,
	 * 		to be used when an element of that class is selected
	 * @return 
	 * 		A new {@link ListEditView}
	 */
	public static <S> ListEditView<S> create(ListAndSelectionAndValueReference<S> model, Class<S> clazz, TableRowView<? super S> rowView, Source<S> source, boolean editSelected, Class<?>... editedClasses) {
		return new ListEditView<S>(model, clazz, rowView, source, editSelected, editedClasses);
	}
	
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
	 * @param editSelected
	 * 		If true, an extra panel is shown to edit the selected item
	 * @param editedClasses
	 * 		The classes for which a specific editor will be looked up,
	 * 		to be used when an element of that class is selected
	 */
	private ListEditView(ListAndSelectionAndValueReference<T> model, Class<T> clazz, TableRowView<? super T> rowView, Source<T> source, boolean editSelected, Class<?>... editedClasses) {
		this.model = model;
		this.rowView = rowView;
		this.source = source;
		this.clazz = clazz;

		//Make a transform from the list reference to the selected value
		BeanPathTo<ListAndSelectionAndValueReference<T>, T> refToSelected = 
			ListAndSelectionAndValueReferenceDefault.transformerToSelectedValue();
		
		selectedReference = PathPropBuilder.from(clazz, model).toRef(refToSelected);
		
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
	
		//Left and right sections of main panel
		JComponent leftPanel = buildLeftPanel(editSelected);

		if (editSelected) {
			//Panel to edit current selection
			JComponent rightPanel = buildSelectedViewPanel(editedClasses);
	
			//Build the entire panel
			FormLayout layout = new FormLayout(
					"fill:default:grow, 7dlu, 190dlu",
					"fill:default:grow");
			
			DefaultFormBuilder builder = new DefaultFormBuilder(layout);
	
			builder.append(leftPanel);
			builder.append(rightPanel);
			
			panel = builder.getPanel();
		} else {
			panel = leftPanel;
		}
		
		helper = new CompositeViewHelper(views);
	}

	/**
	 * Get a reference that always contains the value
	 * selected in the list.
	 * @return		Reference to selected item
	 */
	public Reference<T> referenceToSelected() {
		return selectedReference;
	}
	
	private JComponent buildLeftPanel(boolean editSelected) {
		//Build the entire panel
		FormLayout layout = new FormLayout(
				"fill:default:grow",
				"fill:default:grow, 3dlu, default");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		
		if (editSelected) {
			builder.setDefaultDialogBorder();
		}

		builder.append(tablePanel);
		builder.nextRow();
		builder.append(buttonPanel);
		
		if (editSelected) {
			SimpleInternalFrame sif = new SimpleInternalFrame("List");
			sif.add(builder.getPanel());
			return sif;
		} else {
			return builder.getPanel();
		}
	}

	private JComponent buildSelectedViewPanel(Class<?> ... editedClasses) {
		
		//Always edit at least beans as a specific class
		if (editedClasses.length == 0) {
			selectedView = new FlexibleView(selectedReference, Bean.class);
		} else {
			selectedView = new FlexibleView(selectedReference, editedClasses);
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(selectedView.getComponent());
		panel.setBorder(Borders.DIALOG_BORDER);
		
		JScrollPane selectedViewScroll = new JScrollPane(panel);
		selectedViewScroll.setBorder(null);
		
		views.add(selectedView);

		SimpleInternalFrame sif = new SimpleInternalFrame("Edit Selected Item");
		sif.add(selectedViewScroll);
		return sif;
	}
	
	private JComponent buildButtonPanel() {
		JView view = ListButtonsViewFactory.makeView(model, source);
		views.add(view);
		return view.getComponent();
	}
	
	/**
	 * Gets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultEditor(Class)}
	 * @param columnClass The edited class
	 * @return The default editor
	 */
	public TableCellEditor getDefaultEditor(Class<?> columnClass) {
		return tableView.getDefaultEditor(columnClass);
	}
	
	/**
	 * Gets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultRenderer(Class)}
	 * @param columnClass The rendered class
	 * @return The default renderer
	 */
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return tableView.getDefaultRenderer(columnClass);
	}

	/**
	 * Sets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultEditor(Class, TableCellEditor)}
	 * @param columnClass The edited class
	 * @param editor The default editor
	 */
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		tableView .setDefaultEditor(columnClass, editor);
	}

	/**
	 * Sets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultRenderer(Class, TableCellRenderer)}
	 * @param columnClass The rendered class
	 * @param renderer The default renderer
	 */
	public void setDefaultRenderer(Class<?> columnClass,
			TableCellRenderer renderer) {
		tableView.setDefaultRenderer(columnClass, renderer);
	}
	
    /**
     * Sets the height, in pixels, of all cells to <code>rowHeight</code>,
     * revalidates, and repaints.
     * The height of the cells will be equal to the row height minus
     * the row margin.
     *
     * @param   rowHeight                       new row height
     * @exception IllegalArgumentException      if <code>rowHeight</code> is
     *                                          less than 1
     */
    public void setRowHeight(int rowHeight) {
    	tableView.getComponent().setRowHeight(rowHeight);
    }
	
    /**
     * Remove header from table display
     */
    public void removeHeader() {
    	tableView.removeHeader();
    }
    
	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		return true;
	}

	//Delegated methods
	
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
	public void dispose() {
		helper.dispose();
	}

	@Override
	public void update() {
		helper.update();
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}

	/**
	 * Get the {@link ListAndSelectionAndValueReference} used by this view
	 * to reach data
	 * @return	Reference
	 */
	public ListAndSelectionAndValueReference<T> getReference() {
		return model;
	}

}
