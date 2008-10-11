/*
 * $Id: ListBeanEditor.java,v 1.27 2008-06-30 09:05:34 b.webster Exp $
 */
package org.jpropeller.view.list.impl;

//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import javax.swing.InputMap;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.KeyStroke;
//import javax.swing.ListSelectionModel;
//import javax.swing.SwingConstants;
//import javax.swing.border.EmptyBorder;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//
//import org.jpropeller.collection.ObservableList;
//import org.jpropeller.ui.external.SimpleInternalFrame;
//import org.jpropeller.view.CompletionException;
//import org.jpropeller.view.JView;
//import org.jpropeller.view.table.impl.ListRowsTableModel;
//
//import com.jgoodies.forms.builder.PanelBuilder;
//import com.jgoodies.forms.layout.CellConstraints;
//import com.jgoodies.forms.layout.FormLayout;

/**
 * A PanelEditor for ListBeans, displays the listbean contents as a JTable,
 * and the PanelEditor or Customizer for the selected element of the list,
 * where that element is an editable bean
 * This is also a View for ListBeans.
 * using setObject and editObject to edit a ListBean will also set the model
 * of the view to that ListBean, and fire events on view change
 * 
 * This editor respects the Freezable interface, and will not allow addition, deletion
 * or moving of elements in the list while it is frozen. Note that editors will still
 * however be shown for list elements - it is expected that if necessary, the 
 * elements will be frozen, and their editors will respect this, to render the
 * ListBean "deep" frozen.
 * @param <E> 
 * 		The type of element in the list
 */
public class ListBeanEditor<E> {//implements JView<ObservableList<E>>, ListSelectionListener{
//
//	//Create logger
//	private static Logger logger = Logger.getLogger(ListBeanEditor.class.getCanonicalName());    
//	
//	/**
//	 * The View in use to edit an item, or null if there is no view in use 
//	 */
//	JView<? super E> currentItemView;
//		
//	/**
//	 * The model used to show the list as a table
//	 */
//	ListRowsTableModel<E> tableModel;
//
//	/**
//	 * Name for the list pane, if null, list.toString() will be used
//	 */
//	String listName = null;
//
//	/**
//	 * Name for the edit frame pane, if null, "Edit List Entry" will be used
//	 */
//	String editName = null;
//
//	/**
//	 * The JTable we use to display the list
//	 */
//	protected JTable table;
//	
//	/**
//	 * The internal frame we use to show the list
//	 */
//	SimpleInternalFrame listIF;
//	
//	/**
//	 * The panel for the item view
//	 */
//	JPanel viewHolderPanel;
//	
//	/**
//	 * Label to display when selected element is not editable
//	 */
//	JLabel uneditableLabel = new JLabel("Entry Cannot Be Edited", SwingConstants.CENTER);
//
//	/**
//	 * Label to display when there is no selected element
//	 */
//	JLabel noSelectionLabel = new JLabel("");
//
//	/**
//	 * Button to add element to list
//	 */
//	JButton addButton;
//
//	/**
//	 * Button to remove selected element from list
//	 */
//	JButton removeButton;
//	
//	/**
//	 * Button to move element up list
//	 */
//	JButton upButton;
//	
//	/**
//	 * Button to move element down list
//	 */
//	JButton downButton;
//	
//	JScrollPane editorScroll;
//	
//	Action removeElementAction;
//	
//	Action addElementAction;
//
//	Action moveUpAction;
//	
//	Action moveDownAction;
//	
//	List<ListBeanEditorSelectionListener<E>> selectionListeners = 
//		new LinkedList<ListBeanEditorSelectionListener<E>>();
//	
//	JView<? super E> overridingView = null;
//	
//	List<ObservableListAddFilter<E>> addFilters = new ArrayList<ObservableListAddFilter<E>>();
//	
//	/**
//	 * Store the add factory used to get new instances to 
//	 * insert
//	 */
//	protected ListBeanAddFactory<? extends E> addFactory = null;
//
//	private SimpleInternalFrame editIF;
//	
//	/**
//	 * Create ListBeanEditor with default sizes, list on left, editing on right,
//	 * no add/remove/move controls
//	 */
//	public ListBeanEditor() {
//		super();
//	}
//	
//	/**
//	 * @return A Component which is the UI for the editor
//	 */
//	public Component buildUI() {
//		
//		//Make actions
//		removeElementAction = 	new RemoveElementAction();
//		addElementAction = 		new AddElementAction();
//		moveUpAction = 			new MoveUpAction();
//		moveDownAction = 		new MoveDownAction();
//		
//        //Make form layout for components
//        FormLayout layout = new FormLayout(
//            "220dlu:grow, 5dlu, 400", // columns
//        	"fill:0:grow");      // rows
//        
//        //Make builder to construct panel
//        PanelBuilder builder = new PanelBuilder(layout);
//     
//        //Obtain a reusable constraints object to place components in the grid.
//        CellConstraints cc = new CellConstraints();
//        
//        //Fill the grid with components
//        builder.add(		buildListPanel(),   	cc.xy(1, 1));
//        builder.add(		buildEditorPanel(),   	cc.xy(3, 1));
//
//        return builder.getPanel();
//		
//	}
//
//	
//	private Component buildListEditPanel() {
//		
//		addButton = new JButton(addElementAction);
//		
//		removeButton = new JButton(removeElementAction);
//
//		upButton= new JButton(moveUpAction);
//
//		downButton= new JButton(moveDownAction);
//
//		updateButtons();
//		
//        FormLayout layout = new FormLayout(
//        		"0dlu:grow, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 0dlu:grow",
//        		"3dlu, pref" 
//            );
//        
//        layout.setColumnGroups(new int[][]{{2, 4, 6, 8}});
//        
//        //Make builder to construct panel
//        PanelBuilder builder = new PanelBuilder(layout);
//        //builder.setDefaultDialogBorder();
//     
//        //Obtain a reusable constraints object to place components in the grid.
//        CellConstraints cc = new CellConstraints();
//        
//        //Fill the grid with components
//        builder.add(		addButton,   	cc.xy(2, 2));
//        builder.add(		removeButton,   cc.xy(4, 2));
//        builder.add(		upButton,   	cc.xy(6, 2));
//        builder.add(		downButton,   	cc.xy(8, 2));
//        
//        return builder.getPanel();
//	}
//	
//	/**
//	 * @return A Component which contains the list display of the bean
//	 */
//	public Component buildListPanel() {
//		
//		table = new JTable();
//		table.setTableHeader(null);
//		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		
//		table.getSelectionModel().addListSelectionListener(this);
//		
//        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        im.put(KeyStroke.getKeyStroke("DELETE"), "removeElement");
//        table.getActionMap().put("removeElement", removeElementAction);
//		
//		//Simple internal frame to hold table
//        listIF = new SimpleInternalFrame(listFrameName());
//
//        JScrollPane scroll = new JScrollPane(table);
//        JPanel surround = new JPanel(new BorderLayout());
//        surround.add(scroll);
//        surround.add(buildListEditPanel(), BorderLayout.SOUTH);
//        surround.setBorder(new EmptyBorder(8,8,8,8));
//        
//        //Put the table in a scrollpane in the internal frame
//        listIF.add(surround);
//        
//        //return the internal frame
//        return listIF;                		
//	}
//	
//	/**
//	 * @return A Component which contains the editor buttons (editor for the
//	 * selected list item, and add/move/delete buttons if enabled
//	 */
//	public Component buildEditorPanel() {
//		
//		editIF = new SimpleInternalFrame("Edit List Entry");
//        editIF.setMinimumSize(new Dimension(300, 300));
//        editIF.setPreferredSize(new Dimension(32768, 32768));
//        editIF.setMaximumSize(new Dimension(32768, 32768));
//        
//        //Put the list entry editor holder panel into the sif
//		viewHolderPanel = new JPanel(new BorderLayout());
//
//        //Add a scroll pane containing the editor pane to the SIF
//		editorScroll = new JScrollPane(viewHolderPanel);
//		editorScroll.setBorder(new EmptyBorder(2,2,2,2));
//		editorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		editorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		editIF.add(editorScroll, BorderLayout.CENTER);
//        
//        //return the internal frame
//        return editIF;                		
//	}
//	
//	/**
//	 * Respond to change in selection on table
//	 */
//	@SuppressWarnings("unchecked")
//	public void valueChanged(ListSelectionEvent e) {
//		logger.info("ListBean list selection " + e.getFirstIndex());
//		logger.info("Model " + getModel());
//		
//		updateButtons();
//		
//		if (!table.getSelectionModel().isSelectionEmpty()) {
//
//			int selectedRow = table.getSelectionModel().getMinSelectionIndex();
//			
//			//FIXME deal with unchecked conversion. This results from difficulty
//			//in checking whether elements are editable as the same type as the
//			//elements themselves. Logically this should always be the case.
//			//This could be solved by making the editor only accept lists of a type
//			//that is editable as its own type, but this is restrictive.
//			//Should perhaps catch class cast exception - but due to erasure 
//			//I don't think there is one?
//			//If we have a selected, non null object, then try to display editor
//			Object o = table.getValueAt(selectedRow, 0);
//			
//			//If we have an overriding editor, use it
//			if (overridingView != null) {
//				
//				//Try to commit any current editing in the overriding editor
//				try {
//					overridingView.commit();
//				} catch (CompletionException ce) {
//					logger.log(Level.WARNING, "Completion exception editing " + getModel(), ce);
//				}
//
//				//Set the model of the overriding editor to the new selection
//				//Cast is safe, since we know that we are editing a ListBean<E>, so
//				//even thought the table model doesn't know this, we can still be
//				//confident that the object is of type E
//				overridingView.setModel((E)o);
//				
//				//Use the overriding editor
//				useNewItemView(overridingView);
//			
//			//Otherwise look for a panel editor
//			} else if (o instanceof JPanelEditable) {
//				
//				JPanelEditor pEd = ((JPanelEditable)o).getPanelEditor(); 
//				
//				//Set new editor into interface
//				useNewItemView(pEd);
//				
//			//Otherwise display notice that element is uneditable
//			} else {
//				//TODO get bean customizer and use instead of editor,
//				//TODO if no customizer, display uneditable message
//				//Remember to clear currentPanelEditor
//				logger.info("uneditable");
//				useNewEditorComponent(uneditableLabel);
//			}
//			
//        //No selection
//		} else {
//			//Show no selection 
//			useNewEditorComponent(noSelectionLabel);
//			logger.info("no selection");
//		}
//		
//		//Fire selection change
//		fireSelectionChange(getSelected());
//		
//	}
//	
//	/**
//	 * Set the index selected in the table.
//	 * Does nothing if the index is outside the index range
//	 * of the table
//	 * @param index	
//	 * 		The selection index
//	 */
//	public void setSelectedIndex(int index) {
//		if ((index >= 0) && (index < table.getRowCount()))	{
//			table.getSelectionModel().setSelectionInterval(index, index);
//		}
//	}
//	
//	/**
//	 * Put the new editor component into the interface, and revalidate/repaint ui
//	 * @param newItemView The new editor component 
//	 */
//	void useNewItemView(JView<? super E> newItemView) {
//
//		//If we have a view in use, commit it
//		//If the new editor is the same as the last one, then it does not need to be committed
//		if (currentItemView != null && (newItemView != currentItemView)) {
//			try {
//				currentItemView.commit();
//			} catch (CompletionException e) {
//				logger.log(Level.WARNING, "Completion exception editing " + getModel(), e);
//			}
//		}
//
//		//remember new current panel editor
//		currentItemView = newItemView;
//		
//		editorScroll.setViewportView(newItemView.getComponent());
//	}
//	
//	/**
//	 * Put the new editor component into the interface, and revalidate/repaint ui
//	 * @param newEditorComponent The new editor component 
//	 */
//	void useNewEditorComponent(Component newEditorComponent) {
//
//		//If we have a panel editor in use, commit it
//		if (currentItemView != null) {
//			try {
//				currentItemView.commit();
//			} catch (CompletionException e) {
//				logger.log(Level.WARNING, "Completion exception editing " + getModel(), e);
//			}
//		}
//
//		//Now have no current panel editor
//		currentItemView = null;
//		
//		editorScroll.setViewportView(newEditorComponent);
//	}
//	
//	/* (non-Javadoc)
//	 * @see com.itis.bean.View#setModel(null)
//	 */
//	public void setModel(ListBean<E> model) {
//
//		//Do nothing if model has not changed
//		if (getModel() == model) return;
//
//		super.setModel(model);
//		
//		//Set the table model here, to avoid resetting on every change, 
//		//since the table model already updates itself as necessary
//		table.setModel(new ListBeanTableModel(model));
//		
//		//Notify of selection change
//		fireSelectionChange(getSelected());
//	}
//	
//	/**
//	 * @return The name currently to be used for list internal frame name
//	 */
//	String listFrameName() {
//		//If no listName, then use list.toString()
//		if (listName == null) {
//			//If null list, then use "List (Empty)"
//			if (getModel() == null) {
//				return "List (Empty)";
//			//Use list.toString
//			} else {
//				return getModel().toString();
//			}
//		//Use specified name 
//		} else {
//			return listName;
//		}
//	}	
//	
//	/**
//	 * @return The name currently to be used for list internal frame name
//	 */
//	String editFrameName() {
//		//If no listName, then use list.toString()
//		if (editName == null) {
//			return "Edit List Entry";
//		//Use specified name 
//		} else {
//			return editName;
//		}
//	}	
//	
//	/* (non-Javadoc)
//	 * @see java.beans.Customizer#setObject(java.lang.Object)
//	 */
//	@SuppressWarnings("unchecked")
//	public void setObject(Object bean) {
//		//FIXME any way to get safe cast on this?
//		if (bean instanceof ListBean) {
//			setModel((ListBean)bean);
//		} else {
//			throw new IllegalArgumentException(
//					"ListBeanEditor will accept ListBeans only.");
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see com.itis.edit.ObjectEditor#isEditing()
//	 */
//	public boolean isEditing() {
//		//We only have a pending edit if we have a panel editor in use, and it is editing
//		if (currentItemView == null) {
//			return false;
//		} else {
//			return currentItemView.isEditing();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see com.itis.edit.ObjectEditor#commit()
//	 */
//	public void commit() throws CompletionException {
//		//If we have a panel editor in use, commit it, otherwise nothing to do
//		if (currentItemView != null) {
//			currentItemView.commit();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see com.itis.edit.ObjectEditor#cancel()
//	 */
//	public void cancel() {
//		//If we have a panel editor in use, cancel it, otherwise nothing to do
//		if (currentItemView != null) {
//			currentItemView.cancel();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see com.itis.bean.ui.AbstractPanelEditor#modelToInterface()
//	 */
//	protected void modelToInterface() {
//		
//		//NOTE we do not update the table model here, since it already
//		//updates itself exactly when necessary already
//		
//		//Update our frame name
//		listIF.setTitle(listFrameName());
//		
//		//Update our action buttons to enable/disable according to
//		//model frozen state and presence/absence of an addFactory
//		updateButtons();
//		
//	}
//	
//	private void updateButtons() {
//		boolean frozen = false;
//		if (getModel() instanceof Freezable) {
//			frozen = ((Freezable) getModel()).getFrozen();			
//		}
//		
//		//If we have a selection
//		if (!table.getSelectionModel().isSelectionEmpty()) {
//
//			int selectedRow = table.getSelectionModel().getMinSelectionIndex();
//			
//			//Enable remove button if not frozen
//			removeElementAction.setEnabled(!frozen);
//			
//			//Can move up if not frozen, unless first element selected
//			moveUpAction.setEnabled(!frozen && (selectedRow > 0));
//			
//			//Can move down if not frozen, unless last element is selected
//			moveDownAction.setEnabled(!frozen && (selectedRow < table.getRowCount() - 1));
//			
//		//If we have no selection, can't move or remove
//		} else {
//			moveUpAction.setEnabled(false);
//			moveDownAction.setEnabled(false);
//			removeElementAction.setEnabled(false);			
//		}
//		
//		//Can add if we are not frozen and have an add factory
//		addElementAction.setEnabled(!frozen && (addFactory!=null));
//
//	}
//	
//	/**
//	 * @return Returns the listName
//	 */
//	public String getListName() {
//		return listName;
//	}	
//
//	/**
//	 * @return
//	 * 		The currently selected list item, or null if no item is selected
//	 */
//	public E getSelected() {
//		int selectedRow = table.getSelectionModel().getMinSelectionIndex();
//		if (selectedRow < 0) return null;
//		
//		return getModel().get(selectedRow);
//	}
//	
//	/**
//	 * @param listName The listName to set, set to null to
//	 * use the toString() value of the displayed/edited list bean
//	 */
//	public void setListName(String listName) {
//		Object oldValue = this.listName;
//		this.listName = listName;
//
//		//Update name in frame
//		listIF.setTitle(listFrameName());
//
//		support().setProperty("listName", oldValue, listName);
//	}
//	
//	
//	/**
//	 * Get the string displayed in the editing frame title
//	 * @return
//	 * 		editName
//	 */
//	public String getEditName() {
//		return editName;
//	}
//
//	/**
//	 * Set the string displayed in the editing frame title
//	 * @param editName
//	 */
//	public void setEditName(String editName) {
//		//Store old value of property
//		Object oldValue = this.editName;
//	
//		this.editName = editName;
//	
//		//Update name in frame
//		editIF.setTitle(editFrameName());
//		
//		//Set new property
//		support().setProperty("editName", oldValue, editName);
//	}
//
//	/**
//	 * Get the editor used instead of an individual element's
//	 * editor. If this is null, each element is asked to provide
//	 * its own editor, if it is {@link JPanelEditable}
//	 * @return
//	 * 		overridingEditor
//	 */
//	public JPanelEditor<? super E> getOverridingEditor() {
//		return overridingView;
//	}
//
//	/**
//	 * Set the editor used instead of an individual element's
//	 * editor. If this is null, each element is asked to provide
//	 * its own editor, if it is {@link JPanelEditable}
//	 * @param overridingEditor
//	 */
//	public void setOverridingEditor(JPanelEditor<? super E> overridingEditor) {
//		//Store old value of property
//		Object oldValue = this.overridingView;
//	
//		this.overridingView = overridingEditor;
//	
//		//Set new property
//		support().setProperty("overridingEditor", oldValue, overridingEditor);
//	}
//
//	/**
//	 * @return The factory from which new instances are requested, when the
//	 * user chooses to add to the list
//	 */
//	public ListBeanAddFactory<? extends E> getAddFactory() {
//		return addFactory;
//	}
//	
//	/**
//	 *	Set the add factory 
//	 * @param addFactory The factory from which new instances are requested, when the
//	 * user chooses to add to the list
//	 */
//	public void setAddFactory(ListBeanAddFactory<? extends E> addFactory) {
//		Object oldValue = this.addFactory;
//		this.addFactory = addFactory;
//		updateButtons();
//		support().setProperty("addFactory", oldValue, addFactory);
//	}
//	
//	/**
//	 *	Action removes an element from the list, if possible 
//	 */
//	class MoveUpAction extends AbstractAction {
//		private static final long serialVersionUID = -9115284467263327996L;
//
//		MoveUpAction() {
//			super("Up", Icons.icon(Icons.IconName.UP));
//			putValue(SHORT_DESCRIPTION, "Move an item up the list");	
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			if (!table.getSelectionModel().isSelectionEmpty()) {
//				int index = table.getSelectionModel().getMinSelectionIndex();
//				if (index > 0) {
//					boolean wasSet = false;
//					if (getModel() instanceof PropertyChangeGrouping) {
//						logger.info("Starting group for move up");
//						wasSet = support().startGroupedChange(
//								(PropertyChangeGrouping)getModel());
//					}
//					
//					E removed = getModel().remove(index);
//					getModel().add(index - 1, removed);
//
//					if (getModel() instanceof PropertyChangeGrouping) {
//						logger.info("Stopping group for move up");
//						support().stopGroupedChange(
//								(PropertyChangeGrouping)getModel(), 
//								wasSet);
//					}
//					table.getSelectionModel().setSelectionInterval(index-1, index-1);
//				}
//			}
//		}
//
//	}
//
//	/**
//	 *	Action moves an item in the list, if possible 
//	 */
//	class MoveDownAction extends AbstractAction {
//		private static final long serialVersionUID = -3032553485299396425L;
//
//		MoveDownAction() {
//			super("Down", Icons.icon(Icons.IconName.DOWN));
//			putValue(SHORT_DESCRIPTION, "Move an item down the list");	
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			if (!table.getSelectionModel().isSelectionEmpty()) {
//				int index = table.getSelectionModel().getMinSelectionIndex();
//				if (index < getModel().size()-1) {
//					
//					boolean wasSet = false;
//					if (getModel() instanceof PropertyChangeGrouping) {
//						logger.info("Starting group for move down");
//						wasSet = support().startGroupedChange(
//								(PropertyChangeGrouping)getModel());
//					}
//					
//					E removed = getModel().remove(index);
//					getModel().add(index + 1, removed);
//
//					if (getModel() instanceof PropertyChangeGrouping) {
//						logger.info("Stopping group for move down");
//						support().stopGroupedChange(
//								(PropertyChangeGrouping)getModel(), 
//								wasSet);
//					}
//					table.getSelectionModel().setSelectionInterval(index+1, index+1);
//				}
//			}
//		}
//
//	}
//	
//	/**
//	 *	Action removes an element from the list, if possible 
//	 */
//	class RemoveElementAction extends AbstractAction {
//		private static final long serialVersionUID = 767341962299850437L;
//
//		RemoveElementAction() {
//			super("Remove", Icons.icon(Icons.IconName.REMOVE));
//			putValue(SHORT_DESCRIPTION, "Remove an item from the list");	
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			if (!table.getSelectionModel().isSelectionEmpty()) {
//				getModel().remove(table.getSelectionModel().getMinSelectionIndex());
//			}
//		}
//
//	}
//	
//	/**
//	 *	Action adds an element to the list, if possible 
//	 */
//	class AddElementAction extends AbstractAction {
//		private static final long serialVersionUID = 1434033170443310462L;
//
//		AddElementAction() {
//			super("Add", Icons.icon(Icons.IconName.ADD));
//			putValue(SHORT_DESCRIPTION, "Add an item to the list");	
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			if (addFactory != null) {
//				
//				E instance = addFactory.createInstanceToAdd(ListBeanEditor.this);
//
//				//If factory returned item to insert, then insert it
//				if (instance != null) {
//					
//					//Find insert index - 
//					//Insert after selection index, or at end of list
//					int insertIndex = getModel().size();
//					if (!table.getSelectionModel().isSelectionEmpty()) {
//						insertIndex = table.getSelectionModel().getMinSelectionIndex()+1;
//					}
//					
//					//First notify filters - they may
//					//modify the instance, and if any reject it, it will
//					//not be added
//					try {
//						applyAddFilters(instance, insertIndex);
//						
//					//If we get an objection, notify user and cancel addition
//					} catch (ListBeanAddException e1) {
//						JOptionPane.showMessageDialog(ListBeanEditor.this, 
//								"Cannot add the new item:\n" +
//								e1.getObjection(),
//								"Addition Failed",
//								JOptionPane.ERROR_MESSAGE);
//						return;
//					}
//					
//					//Actually perform addition
//					getModel().add(insertIndex, instance);
//					
//					//Select the instance we just inserted
//					table.getSelectionModel().setSelectionInterval(insertIndex, insertIndex);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Allow each filter in turn to modify instance and
//	 * then raise any objections to adding it.
//	 * @param instance
//	 * 		The instance proposed for addition
//	 * @param index
//	 * 		The position at which the element will be added
//
//	 * @throws ListBeanAddException
//	 * 		If any filter objects to the addition - this will
//	 * be the objection of the first filter to object, other filters are
//	 * not applied
//	 */
//	private void applyAddFilters(E instance, int index) throws ListBeanAddException {
//		for (ObservableListAddFilter<E> filter : addFilters) {
//			filter.filterElement(instance, getModel(), index);
//		}
//	}
//	
//	/**
//	 * Add a filter to the list to be applied to new elements
//	 * added to the list.
//	 * Filters are applied in the order they are added to the editor.
//	 * @param filter
//	 * 		Filter to apply to all new additions to the list via the editor
//	 */
//	public void addAddFilter(ObservableListAddFilter<E> filter) {
//		addFilters.add(filter);
//	}
//
//	/**
//	 * Remove a ListBeanAddFilter - will no longer be applied to
//	 * new additions to the list
//	 * 
//	 * @param filter
//	 * 		Filter to remove
//	 */
//	public void removeAddFilter(ObservableListAddFilter<E> filter) {
//		addFilters.remove(filter);
//	}
//
//	/**
//	 * @param listener
//	 * 		A listener to be notified of selection changes, please see
//	 * 		{@link ListBeanEditorSelectionListener} and {@link ListBeanEditorSelectionEvent}
//	 * 		for MUCH more detail on when events are fired.
//	 */
//	public void addSelectionListener(ListBeanEditorSelectionListener<E> listener) {
//		selectionListeners.add(listener);
//	}
//
//	/**
//	 * @param listener
//	 * 		A listener no longer to be notified of selection changes, please see
//	 * 		{@link ListBeanEditorSelectionListener} and {@link ListBeanEditorSelectionEvent}
//	 * 		for MUCH more detail on when events are fired.
//	 */
//	public void removeSelectionListener(ListBeanEditorSelectionListener<E> listener) {
//		selectionListeners.remove(listener);
//	}
//
//	/**
//	 * Notify all listeners of a selection change
//	 * @param newSelection
//	 * 		The newly selected element
//	 */
//	private void fireSelectionChange(E newSelection) {
//		ListBeanEditorSelectionEvent<E> event = new ListBeanEditorSelectionEvent<E>(this, newSelection);
//		for (ListBeanEditorSelectionListener<E> selectionListener : selectionListeners) {
//			selectionListener.selectionChanged(event);
//		}
//	}
//	
}
