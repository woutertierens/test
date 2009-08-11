package org.jpropeller.util;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.ui.impl.OKCancelDialog;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.impl.SingleSelectionListTableView;

import com.jgoodies.forms.factories.Borders;

/**
 * A {@link Source} that allows the user to select
 * an item from a {@link ListProp} when a new
 * instance is requested.
 * 
 * NOTE: The selected element is returned directly,
 * not cloned.
 *
 * @param <T> The type of instance provided
 */
public class ListSelectionSource<T> implements Source<T> {

	private OKCancelDialog dialog;
	private T selection = null;
	private boolean selected = false;
	private ListAndSelectionAndValueReference<T> listRef;
	private SingleSelectionListTableView<T> view;
	private JPanel panel;
	private Frame owner;
	private String title;
	
	
	/**
	 * Create a {@link ListSelectionSource}
	 * 
	 * @param owner				The owner of this dialog, or null 
	 * @param title				The title of the dialog
	 * @param listProp 			The property containing the
	 * 							list of elements to choose from
	 * @param clazz 			The class of instance provided
	 * @param tableRowView		The view for the table display		
	 * @param showTableHeader 	True to display the table row
	 * 							header in dialog, false to hide it
	 */
	public ListSelectionSource(
			Frame owner, String title, 
			Prop<CList<T>> listProp, Class<T> clazz, 
			TableRowView<? super T> tableRowView,
			boolean showTableHeader)
	{
		this(owner, title, listProp, clazz, tableRowView, showTableHeader, null);
	}
	/**
	 * Create a {@link ListSelectionSource}
	 * 
	 * @param owner				The owner of this dialog, or null 
	 * @param title				The title of the dialog
	 * @param listProp 			The property containing the
	 * 							list of elements to choose from
	 * @param clazz 			The class of instance provided
	 * @param tableRowView		The view for the table display		
	 * @param showTableHeader 	True to display the table row
	 * 							header in dialog, false to hide it
	 * @param choiceDescription	Description of the choice to be made,
	 * 							to be displayed as the text of a {@link JLabel}
	 * 							above the selection list. Will accept {@link JLabel}
	 * 							compatible html.
	 */
	public ListSelectionSource(
			Frame owner, String title, 
			Prop<CList<T>> listProp, Class<T> clazz, 
			TableRowView<? super T> tableRowView,
			boolean showTableHeader,
			String choiceDescription) {
		super();
		
		this.owner = owner;
		this.title = title;
		
		listRef = new ListAndSelectionAndValueReferenceDefault<T>(
				listProp, 
				clazz);

		view = new SingleSelectionListTableView<T>(listRef, tableRowView);
		JScrollPane scroll = new JScrollPane(view.getComponent());
        if (!showTableHeader) {
        	view.getComponent().setTableHeader(null);
        }
        
        panel = new JPanel(new BorderLayout());
        panel.setBorder(Borders.DIALOG_BORDER);
        panel.add(scroll);
        
        if (choiceDescription != null) {
        	panel.add(new JLabel(choiceDescription), BorderLayout.NORTH);
        }
		
        //Remove the mapping for Enter and Shift-Enter, which normally
        //move the selection. We want the keys to be ignored so that the root pane
        //will receive the event, and a selection will be made
		JTable table = view.getComponent();
        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke("ENTER"), "none");
        im.put(KeyStroke.getKeyStroke("shift ENTER"), "none");

        //On double-left-click in the table, select the item
        table.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
        			//"Click ok" in the current dialog, if there is one 
        			if (dialog != null) {
        				dialog.ok();
        			}
        		}
        	}
        });
	}

	private void ok() {
		//Note this will be null if no selection is made
		if (listRef.selection().get() < 0) {
			selection = null;
			selected = false;
		} else {
			selection = listRef.selectedValue().get();
			selected = true;
		}
	}
	
	private void cancel() {
		selection = null;
		selected = false;
	}
	
	@Override
	public T get() {
		selection = null;
		selected = false;

		//Make a dialog to display the view with OK/Cancel
		dialog = new OKCancelDialog(panel, owner, title, "Select", "Cancel");
		dialog.displayDialog();
	
		//This will run when the dialog is hidden again
		if (dialog.lastClickWasOK()) {
			ok();
		} else {
			cancel();
		}
		
		//Make absolutely sure the dialog is got rid of
		dialog = null;
		
		if (!selected) throw new NoInstanceAvailableException("User made no selection.");
		
		return selection;
	}

	/**
	 * Gets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultEditor(Class)}
	 * @param columnClass The edited class
	 * @return The default editor
	 */
	public TableCellEditor getDefaultEditor(Class<?> columnClass) {
		return view.getDefaultEditor(columnClass);
	}
	
	/**
	 * Gets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultRenderer(Class)}
	 * @param columnClass The rendered class
	 * @return The default renderer
	 */
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return view.getDefaultRenderer(columnClass);
	}

	/**
	 * Sets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultEditor(Class, TableCellEditor)}
	 * @param columnClass The edited class
	 * @param editor The default editor
	 */
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		view.setDefaultEditor(columnClass, editor);
	}

	/**
	 * Sets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultRenderer(Class, TableCellRenderer)}
	 * @param columnClass The rendered class
	 * @param renderer The default renderer
	 */
	public void setDefaultRenderer(Class<?> columnClass,
			TableCellRenderer renderer) {
		view.setDefaultRenderer(columnClass, renderer);
	}

	/**
	 * Sets height of row used to display each element of list
	 * @param rowHeight		Row height
	 */
	public void setRowHeight(int rowHeight) {
		view.setRowHeight(rowHeight);
	}

}
