package org.jpropeller.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.system.Props;
import org.jpropeller.ui.impl.OKCancelDialog;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.impl.SingleSelectionListTableView;

import com.jgoodies.forms.factories.Borders;

/**
 * Similar to ListSelectionSource. The ListClassifier is used to divide the list into several groups, which are displayed in tabs
 * (as opposed to the single list display of ListSelectionSource).
 * 
 * NOTE: The selected element is returned directly,
 * not cloned.
 *
 * @param <T> The type of instance provided
 */
public class TabbedListSelectionSource<T> implements Source<T> {

	private OKCancelDialog dialog;
	private T selection = null;
	private boolean selected = false;
	private ArrayList<ListAndSelectionAndValueReference<T>> listRef;
	private ArrayList<SingleSelectionListTableView<T>> view;
	private JPanel panel;
	private Frame owner;
	private String title;
	private ArrayList<JScrollPane> scroll;
	private JTabbedPane jtb;
	
	private ListClassifier<T> classifier;
	
	
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
	public TabbedListSelectionSource(
			Frame owner, String title, 
			Prop<CList<T>> listProp, Class<T> clazz, 
			TableRowView<? super T> tableRowView,
			boolean showTableHeader , ListClassifier<T> classifier)
	{
		this(owner, title, listProp, clazz, tableRowView, showTableHeader, null, null, classifier);
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
	 * 							compatible html. Null to omit.
	 */
	public TabbedListSelectionSource(
			Frame owner, String title, 
			Prop<CList<T>> listProp, Class<T> clazz, 
			TableRowView<? super T> tableRowView,
			boolean showTableHeader,
			String choiceDescription , ListClassifier<T> classifier)
	{
		this(owner, title, listProp, clazz, tableRowView, showTableHeader, choiceDescription, null, classifier);
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
	 * 							compatible html. Null to omit.
	 * @param additionalComponent	Additional component to display below the selection list, null to omit.
	 * @param classifier		The classifier used to distribute the contents of the list on different tabs. Null to completely mimick the behaviour of ListSelectionSource.
	 */
	public TabbedListSelectionSource(
			Frame owner, String title, 
			final Prop<CList<T>> listProp, Class<T> clazz, 
			TableRowView<? super T> tableRowView,
			boolean showTableHeader,
			String choiceDescription,
			JComponent additionalComponent , final ListClassifier<T> classifier) {
		super();
		
		this.owner = owner;
		this.title = title;
		
		this.classifier=classifier;
		boolean makeTabs=true;
		if(classifier==null)
		{
			makeTabs=false;
			this.classifier=new ListClassifier<T>(){
				@Override
				public int classifier(T t) {
					return 0;
				}
				@Override
				public int nClassifiers() {
					return 1;
				}
				@Override
				public String classifierName(int classifier) {
					return "Whole list";
				}
				@Override
				public Icon classifierIcon(int classifier) {
					return null;
				}};
		}
		
		listRef=new ArrayList<ListAndSelectionAndValueReference<T>>(classifier.nClassifiers());
		for(int i=0;i<classifier.nClassifiers();i++)
		{
			final int index=i;
			Prop<CList<T>> partialList = Props.calculatedListOn(clazz, "", listProp).returning(new Source<List<T>>(){
				@Override
				public List<T> get()
						throws NoInstanceAvailableException {
					if(listProp==null || listProp.get()==null)
					{
						throw new NoInstanceAvailableException();
					}
					else
					{
						CList<T> ret=new CListDefault<T>(listProp.get().size());
						for(T t : listProp.get())
						{
							if( classifier.classifier(t)==index )
							{
								ret.add(t);
							}
						}
						return ret;
					}
				}});
			
			listRef.add(new ListAndSelectionAndValueReferenceDefault<T>(
					clazz, 
					partialList));
		}

		view = new ArrayList<SingleSelectionListTableView<T>>(classifier.nClassifiers());
		scroll = new ArrayList<JScrollPane>(classifier.nClassifiers());
		for(int i=0;i<classifier.nClassifiers();i++)
		{
			view.add(new SingleSelectionListTableView<T>(listRef.get(i), tableRowView));
			scroll.add(new JScrollPane(view.get(i).getComponent()));
	        if (!showTableHeader) {
	        	view.get(i).getComponent().setTableHeader(null);
	        }
		}
        
        panel = new JPanel(new BorderLayout());
        panel.setBorder(Borders.DIALOG_BORDER);

        this.jtb=new JTabbedPane();
		for(int i=0;i<classifier.nClassifiers();i++)
		{
			jtb.addTab(classifier.classifierName(i), classifier.classifierIcon(i), scroll.get(i));
		}
		jtb.setSelectedIndex(0);
		
		
        if(makeTabs)
        {
			panel.add(jtb);
        }
        else
        {
			panel.add(scroll.get(0));
        }
        
        if (choiceDescription != null) {
        	panel.add(new JLabel(choiceDescription), BorderLayout.NORTH);
        }
        
        if (additionalComponent != null) {
        	panel.add(additionalComponent, BorderLayout.SOUTH);
        }
        
        
        //Remove the mapping for Enter and Shift-Enter, which normally
        //move the selection. We want the keys to be ignored so that the root pane
        //will receive the event, and a selection will be made
        for(int i=0;i<classifier.nClassifiers();i++)
        {
			JTable table = view.get(i).getComponent();
	        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	        im.put(KeyStroke.getKeyStroke("ENTER"), "none");
	        im.put(KeyStroke.getKeyStroke("shift ENTER"), "none");
	        
	        if(makeTabs)
	        {
	        	//allow left/right key to move through the tabs when the table has the focus
	        	table.addKeyListener(new KeyAdapter(){
	        	public void keyReleased(KeyEvent e)
	        	{
	        		switch(e.getKeyCode())
	        		{
	        		case KeyEvent.VK_LEFT:
	        		{
	        			int nextTab=jtb.getSelectedIndex()+1;
	        			if(nextTab>=jtb.getTabCount()) nextTab=0;
	        			jtb.setSelectedIndex(nextTab);
	        		}
	        		break;
	        		case KeyEvent.VK_RIGHT:
	        		{
	        			int prevTab=jtb.getSelectedIndex()-1;
	        			if(prevTab<0) prevTab=jtb.getTabCount()-1;
	        			jtb.setSelectedIndex(prevTab);
	        		}
	        		break;
	        		}
	        	}
	        });
	        }
	
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
	}

	private void ok() {
		//Note this will be null if no selection is made
		if (listRef.get(jtb.getSelectedIndex()).selection().get() < 0) {
			selection = null;
			selected = false;
		} else {
			selection = listRef.get(jtb.getSelectedIndex()).selectedValue().get();
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
		return view.get(0).getDefaultEditor(columnClass);
	}
	
	/**
	 * Gets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultRenderer(Class)}
	 * @param columnClass The rendered class
	 * @return The default renderer
	 */
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return view.get(0).getDefaultRenderer(columnClass);
	}

	/**
	 * Sets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultEditor(Class, TableCellEditor)}
	 * @param columnClass The edited class
	 * @param editor The default editor
	 */
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		for(SingleSelectionListTableView<T>  v : view)
		{
			v.setDefaultEditor(columnClass, editor);
		}
	}

	/**
	 * Sets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultRenderer(Class, TableCellRenderer)}
	 * @param columnClass The rendered class
	 * @param renderer The default renderer
	 */
	public void setDefaultRenderer(Class<?> columnClass,
			TableCellRenderer renderer) {
		for(SingleSelectionListTableView<T>  v : view)
		{
			v.setDefaultRenderer(columnClass, renderer);
		}
	}

	/**
	 * Sets height of row used to display each element of list
	 * @param rowHeight		Row height
	 */
	public void setRowHeight(int rowHeight) {
		for(SingleSelectionListTableView<T>  v : view)
		{
			v.setRowHeight(rowHeight);
		}
	}
	
	/**
	 * Set preferred size of scroll pane in dialog
	 * @param d	Size
	 */
	public void setPreferredSize(Dimension d) {
		for(JScrollPane s : scroll)
		{
			s.setPreferredSize(d);
		}
	}

}
