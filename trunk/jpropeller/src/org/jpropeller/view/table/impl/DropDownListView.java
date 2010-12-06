package org.jpropeller.view.table.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.ui.impl.JDropDownButton;
import org.jpropeller.ui.impl.NimbusDropDownButton;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.CompositeViewHelper;
import org.jpropeller.view.impl.LabelView;
import org.jpropeller.view.table.TableRowView;

/**
 * A {@link DropDownListView} is a {@link JView} made up from
 * a {@link SingleSelectionListTableView} of a {@link Prop}
 * containing a {@link CList}, and a {@link LabelView} of a
 * selection from that list.
 * 
 * The label view is contained in a {@link JDropDownButton}, which
 * is the normally visible portion of the view, and the list view 
 * is popped up when the button is pressed.
 * 
 * This behaviour is very similar in effect to a non-editable 
 * {@link JComboBox}, but has numerous advantages:
 * 
 * JComboBox can only display a ListModel, and this has several
 * design flaws that make implementations suboptimal - here we
 * use a JTable as the popup component, using a standard JPropeller
 * TableModel approach.
 * 
 * JComboBox retains references to old items/selections, even if 
 * the referenced instances are no longer even in the model. This is
 * potentially a major memory leak.
 * 
 * JComboBox selection seems to suffer from a lag when using the
 * mouse to select.
 * 
 * There are other minor quirks/niggles with JComboBox, and in any
 * case this code is much simpler since it only reuses existing
 * label and list views. 
 * 
 * @param <T>	The type of element in the list model 
 * 
 */
public class DropDownListView<T> implements JView {
	
	private final CompositeViewHelper helper;
	private final JToggleButton button;
	private ListAndSelectionAndValueReference<T> reference;
	private final JTable table;

	/**
	 * Create a {@link DropDownListView}
	 * 
	 * @param modelClass	The {@link Class} of value in the list 
	 * @param model			The list to view
	 * @param rowView		The {@link TableRowView} to convert elements of the model list
	 * 						to rows of the table displayed for editing. Note that the
	 * 						rowView should probably NOT be editable, since the table
	 * 						is intended for selection only.
	 * @param minWidth		The minimum width of the table popup used for selection. If
	 * 						the button provided by {@link #getComponent()} is wider than this,
	 * 						then the table popup will expand to the same width.
	 * @param maxHeight		The maximum height of the popup. If this is insufficient to display all
	 * 						list elements, then the popup will contain a scrollbar.
	 * @param displayHeader	True to display table header in popup, false otherwise.
	 */
	public DropDownListView(Class<T> modelClass, Prop<CList<T>> model, TableRowView<? super T> rowView, int minWidth, final int maxHeight, boolean displayHeader) {
		List<View> views = new ArrayList<View>();
		
		reference = new ListAndSelectionAndValueReferenceDefault<T>(modelClass, model);
		final SingleSelectionListTableView<T> listView = new SingleSelectionListTableView<T>(reference, rowView);
		views.add(listView);
		table = listView.getComponent();
		
		//TODO would be nice to specify a renderer in constructor
		//Make a view of selected item from list as a label
		LabelView labelView = new LabelView(reference.selectedValue());
		views.add(labelView);

		//TODO neater (automatic in button?) choosing between styles.
		if (NimbusDropDownButton.isNimbusAvailable()) {
			button = new NimbusDropDownButton();
		} else {
			button = new JDropDownButton();			
		}
		
		button.add(labelView.getComponent());

		final JScrollPane listScroll = new JScrollPane(listView.getComponent());
		listScroll.setBorder(null);

		Runnable preparePopup = new Runnable() {
			@Override
			public void run() {
				Dimension size = listView.getComponent().getPreferredSize();
				int preferredHeight = size.height;
				if (preferredHeight > maxHeight) {
					preferredHeight = maxHeight;
				}
				//Widths will be replaced by the popup handler
				listScroll.setPreferredSize(new Dimension(10, preferredHeight));
				listScroll.setMaximumSize(new Dimension(10, preferredHeight));
				listScroll.setMinimumSize(new Dimension(10, preferredHeight));
			}
		};

		final PopupHandler handler = new PopupHandler(listScroll, listView.getComponent(), button, minWidth, preparePopup);

		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (button.isSelected()) {
					handler.show();
				}
			}
		});
		
		JTable table = listView.getComponent();
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						handler.hide();
					}
				});
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							handler.hide();	
						}
					});
				}
			}
		});
		
		//Do nothing on pressing enter - otherwise it moves selection down one row
		Action jpropellerNullAction = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {}
		};
		table.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "jpropellerNullAction");
		table.getInputMap(JInternalFrame.WHEN_FOCUSED)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "jpropellerNullAction");
		table.getActionMap().put("jpropellerNullAction", jpropellerNullAction);

		if (!displayHeader) {
			listView.removeHeader();
		}
		
		helper = new CompositeViewHelper(views);
	}

	@Override
	public JComponent getComponent() {
		return button;
	}

	@Override
	public boolean selfNaming() {
		return false;
	}
	
	/**
	 * The {@link ListAndSelectionAndValueReference} used by this view - this can
	 * be used to monitor the selected value
	 * @return	{@link ListAndSelectionAndValueReference}
	 */
	public ListAndSelectionAndValueReference<T> reference() {
		return reference;
	}

	static class PopupHandler implements PopupMenuListener {
		
		private final JPopupMenu popup;
		private final Component focusComponent;
		private final Component popupComponent;
		private final Component invoker;
		private final int minWidth;
		private final int gap = 4;
		private final Runnable preparePopup;

		public PopupHandler(Component popupComponent, Component focusComponent, Component invoker, int minWidth, Runnable preparePopup) {
			
			this.invoker = invoker;
			this.focusComponent = focusComponent;
			this.popupComponent = popupComponent;
			this.minWidth = minWidth;
			this.preparePopup = preparePopup;
			
		    popup = new JPopupMenu();
		    popup.removeAll();
		    popup.setBorder(new MatteBorder(1, 1, 1, 1, Color.darkGray));
		    popup.setLayout(new BorderLayout());
		    popup.add(popupComponent, BorderLayout.CENTER);

		    popup.addPopupMenuListener(this);
		    popup.pack();
		}
		
		public void hide() {
			popup.setVisible(false);
		}

		public void show() {
		    	int width = invoker.getWidth();
		    	if (width < minWidth) {
		    		width = minWidth;
		    	}
		    	preparePopup.run();
			    popupComponent.setPreferredSize(new Dimension(width, popupComponent.getPreferredSize().height));
			    popupComponent.setMinimumSize(new Dimension(width, popupComponent.getMinimumSize().height));
			    popupComponent.setMaximumSize(new Dimension(width, popupComponent.getMaximumSize().height));
			    popup.pack();
				
				//Find position relative to invoker - if we would appear (partially) off screen bottom, display above
				//instead of below
				int x = 0;
				int y = invoker.getHeight() + gap;
				if (invoker.getLocationOnScreen().getY() + y + popup.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().height) {
					y = - popup.getHeight() - gap;
				}
				
				popup.show(invoker, x, y);
				
				//Start with correct component focused
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (focusComponent != null) {
							focusComponent.requestFocus();
						}
					}
				});
		}

		@Override 
	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

	    @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	    	if (invoker instanceof JToggleButton) {
				JToggleButton button = (JToggleButton) invoker;
				button.setSelected(false);
			}
	    }
	    @Override public void popupMenuCanceled(PopupMenuEvent e) {}		
	}
	
	//Delegates to popup table

	/**
	 * Gets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultEditor(Class)}
	 * @param columnClass The edited class
	 * @return The default editor
	 */
	public TableCellEditor getDefaultEditor(Class<?> columnClass) {
		return table.getDefaultEditor(columnClass);
	}

	/**
	 * Gets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultRenderer(Class)}
	 * @param columnClass The rendered class
	 * @return The default renderer
	 */
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return table.getDefaultRenderer(columnClass);
	}

	/**
	 * Sets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultEditor(Class, TableCellEditor)}
	 * @param columnClass The edited class
	 * @param editor The default editor
	 */
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		table.setDefaultEditor(columnClass, editor);
	}

	/**
	 * Sets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultRenderer(Class, TableCellRenderer)}
	 * @param columnClass The rendered class
	 * @param renderer The default renderer
	 */
	public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
		table.setDefaultRenderer(columnClass, renderer);
	}

	/**
	 * Sets the row height of the contained {@link JTable}
	 * @param rowHeight		The row height
	 */
	public void setRowHeight(int rowHeight) {
		table.setRowHeight(rowHeight);
	}

    /**
     * Remove header from table display
     */
    public void removeHeader() {
    	table.setTableHeader(null);
    }
	
	//JView methods delegated to helper
	@Override
	public void cancel() {
		helper.cancel();
	}

	@Override
	public void commit() throws CompletionException {
		helper.commit();
	}

	@Override
	public void dispose() {
		helper.dispose();
	}

	@Override
	public boolean isEditing() {
		return helper.isEditing();
	}

	@Override
	public void update() {
		helper.update();
	}
	
	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}
}
