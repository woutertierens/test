package org.jpropeller.view.table.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.set.impl.SetPropDefault;
import org.jpropeller.reference.Reference;
import org.jpropeller.ui.impl.JTabButton;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableView;

/**
 * A {@link JView} displaying an {@link CList} as a table,
 * using a {@link TableRowView} to convert each element of the list
 * to a row of the table
 * Displays as a spreadsheet, {@link #getComponent()} will return a
 * {@link JScrollPane} with the editor in, so there is no need for
 * consumers of this view to use another {@link JScrollPane}.
 *
 * @param <T>
 * 		The type of element in the {@link CList}
 */
public class SpreadsheetTableView<T> implements TableView {

	JTable table;
	JScrollPane sheet;
	Reference<? extends CList<T>> model;
	FiringTableModel tableModel;
	
	//private static final Cursor CURSOR;
	private static final Color selectionBackground = new Color(155, 155, 200, 100);
	
	/**
	 * Make a new {@link SpreadsheetTableView}
	 * @param tableModel		The table model to be displayed
	 * @param selection 		The current selection.
	 */
	public SpreadsheetTableView(final FiringTableModel tableModel, final Prop<? extends CCollection<Integer>> selection) {
		this.tableModel = tableModel;
		table = new JTableImproved(tableModel);
		
		sheet = new JScrollPane(table);
		sheet.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(4, 4, 0, 0)));
		SelectionSetFilter filter = new SelectionSetFilter() {
			@Override
			public boolean canSet() {
				return !tableModel.isFiring();
			}
		};
		TableRowSorter<FiringTableModel> sorter = new TableRowSorter<FiringTableModel>(tableModel);
		
		
		IntegersListSelectionModel listSelectionModel = new IntegersListSelectionModel(selection, filter, table);
		table.setSelectionModel(listSelectionModel);
		
		table.setRowSorter(sorter);
		 
		
		
		//table.setCursor(CURSOR);

		TableCellRenderer rowHeaderRenderer = new TableCellRenderer() {
			JTabButton lbl;
			{
				 lbl = new JTabButton();
				 
			}
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				
				if(isSelected || hasFocus) {
					lbl.setBackground(UIManager.getColor("itis.background.selected"));						
				} else {
					lbl.setBackground(UIManager.getColor("itis.background"));
				}
				
				lbl.setText(value.toString());
				return lbl;
			}
		};
		
		TableCellRenderer columnHeaderRenderer = new TableCellRenderer() {
			JTabButton lbl;

			{
				 lbl = new JTabButton();
				 lbl.setBorders(JTabButton.LEFT_BORDER | JTabButton.RIGHT_BORDER | JTabButton.TOP_BORDER);
			}
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				
				
				int min = table.getColumnModel().getSelectionModel().getMinSelectionIndex();
				int max = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();
								
				if(min != -1 && max != -1 && column >= min && column <= max) {
					lbl.setBackground(UIManager.getColor("itis.background.selected"));						
				} else {
					lbl.setBackground(UIManager.getColor("itis.background"));
				}
				
				lbl.setText(value.toString());
				return lbl;
			}
		};
		
		for(Enumeration<TableColumn> colEnum = table.getColumnModel().getColumns(); colEnum.hasMoreElements(); ) {
			TableColumn col = colEnum.nextElement();
			col.setHeaderRenderer(columnHeaderRenderer);					
			
		}
			
		
		final JTable rowHeaders = new JTable();
		rowHeaders.setModel(new AbstractTableModel() {
		
			int oldSize = tableModel.getRowCount();
			
			{
				
				tableModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						if(tableModel.getRowCount() != oldSize) {
							oldSize = tableModel.getRowCount();
							fireTableStructureChanged();
						}
					}
				});
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return new Integer(rowIndex+1);				
			}
		
			@Override
			public int getRowCount() {
				return tableModel.getRowCount();
			}
		
			@Override
			public int getColumnCount() {
				return 1;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Integer.class;
			}
		});
		
		rowHeaders.getModel().addTableModelListener(new TableModelListener() {
		
			@Override
			public void tableChanged(TableModelEvent e) {
				int size = rowHeaders.getModel().getRowCount();
				int width;
				if(size < 100) {
					width = 48;
				} else if (size < 1000) {
					width = 53;
				} else if (size < 1000) {
					width = 58;
				} else {
					width = 64;
				}
				rowHeaders.getColumnModel().getColumn(0).setWidth(width);
				rowHeaders.getColumnModel().getColumn(0).setPreferredWidth(width);
				rowHeaders.setPreferredScrollableViewportSize(new Dimension(width, 100000));
			}
		});
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int min = table.getSelectionModel().getMinSelectionIndex();
				int max = table.getSelectionModel().getMaxSelectionIndex();
				if(min == -1 || max == -1) return;	
				rowHeaders.setRowSelectionInterval(min, max);
			}
		});
		
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
		
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
				table.getTableHeader().repaint();
			}
			@Override public void columnRemoved(TableColumnModelEvent e) {}
			@Override public void columnMoved(TableColumnModelEvent e) {}
			@Override public void columnMarginChanged(ChangeEvent e) {}
			@Override public void columnAdded(TableColumnModelEvent e) {}
		});
		
		table.setGridColor(Color.lightGray);
		table.setShowGrid(true);
		table.setIntercellSpacing(new Dimension(2, 2));

		rowHeaders.setBackground(Color.lightGray);
		rowHeaders.setForeground(Color.black);
		rowHeaders.setEnabled(false);
		rowHeaders.setDefaultRenderer(Integer.class, rowHeaderRenderer);
		rowHeaders.getColumnModel().getColumn(0).setPreferredWidth(48);
		rowHeaders.setPreferredScrollableViewportSize(new Dimension(48, 100000));
		rowHeaders.getColumnModel().getColumn(0).setWidth(48);
		table.setSelectionBackground(selectionBackground);
		
		rowHeaders.getColumnModel().getColumn(0).setMinWidth(48);
		rowHeaders.getColumnModel().getColumn(0).setMaxWidth(100);
		
		rowHeaders.setRowHeight(table.getRowHeight());
		
		table.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		table.setCellSelectionEnabled(true);
		
		rowHeaders.setTableHeader(null);
		
		sheet.setRowHeaderView(rowHeaders);
		SpreadSheetAdapter.install(table);
	}
	
	/**
	 * Creates a {@link SpreadsheetTableView} that doesn't care about selection.
	 * @param model		The model.
	 */
	public SpreadsheetTableView(final FiringTableModel model) {
		this(model, SetPropDefault.create("sel", Integer.class));
	}

	@Override
	public TableCellEditor getDefaultEditor(Class<?> columnClass) {
		return table.getDefaultEditor(columnClass);
	}

	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return table.getDefaultRenderer(columnClass);
	}

	@Override
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		table.setDefaultEditor(columnClass, editor);
	}

	@Override
	public void setDefaultRenderer(Class<?> columnClass,
			TableCellRenderer renderer) {
		table.setDefaultRenderer(columnClass, renderer);
	}
	
	@Override
    public void setRowHeight(int rowHeight) {
    	table.setRowHeight(rowHeight);
    }
	
	@Override
    public void removeHeader() {
    	table.setTableHeader(null);
    }

	@Override
	public JScrollPane getComponent() {
		return sheet;
	}

	@Override
	public boolean selfNaming() {
		//Just displays list contents
		return false;
	}

	@Override
	public void cancel() {
		//TODO find way to cancel
		//Can't currently cancel
	}

	@Override
	public void commit() throws CompletionException {
		//TODO find way to commit
		//Can't currently commit
	}

	@Override
	public boolean isEditing() {
		// TODO find way to tell if we are editing
		//Currently never editing (or rather - not editing in a way we can cancel or commit)
		return false;
	}

	@Override
	public void dispose() {
		//Dispose our model
		tableModel.dispose();
	}

	@Override
	public void update() {
		//JTable updates itself automatically based on table model changes, without using
		//the UpdateManager - the table model does use UpdateManager though, and this 
		//achieves the same goal
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}
	
}
