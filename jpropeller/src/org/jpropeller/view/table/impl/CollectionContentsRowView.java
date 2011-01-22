package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Listeners;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;

/**
 * {@link TableRowView} where each row object "contains"
 * a {@link Prop} of {@link CCollection}, in the sense
 * that a transformer can reach such a {@link Prop} from the
 * row Object.
 * 
 * The {@link CollectionContentsRowView} will create a column
 * for each element of a {@link CList} of values in another prop.
 * 
 * On each row, each column contains a boolean value, true if the
 * {@link CCollection} for that row contains the item displayed in that
 * column.
 * 
 * @param <R>		The type of row
 * @param <C>		The type of element in the row's collections
 */
public class CollectionContentsRowView<R, C> implements TableRowView<R>, ChangeListener {

	private final static Logger logger = GeneralUtils.logger(CollectionContentsRowView.class);
	
	private final Prop<? extends CList<? extends C>> values;
	private final Transformer<R, Prop<? extends CCollection<C>>> transformer;
	
	private final Listeners<TableRowViewListener> listeners = new Listeners<TableRowViewListener>();

	private final boolean editable;
	
	/**
	 * Make a {@link CollectionContentsRowView}
	 * @param values		The list of values that are looked for in 
	 * 						rows' {@link CCollection}s
	 * @param transformer	The {@link Transformer} from rows to
	 * 						the {@link CCollection}s they contain
	 * 						Note that this can be any {@link Transformer}
	 * 						with the correct types, but in order for a 
	 * 						{@link JTable} using this {@link TableRowView}
	 * 						to update properly, it must notice when the
	 * 						value reached by applying the {@link Transformer}
	 * 						changes. So for example, if R is a {@link Bean}
	 * 						type, and the {@link Transformer} reaches a
	 * 						{@link Prop} by navigating from the {@link Bean}
	 * 						via {@link Prop}s, everything will work fine. This
	 * 						is because a suitable {@link TableModel} like
	 * 						{@link ListTableModel} will notice when the
	 * 						{@link Bean}s change, and so redraw.  
	 * @param editable		Whether values are editable
	 */
	public CollectionContentsRowView(
			Prop<? extends CList<? extends C>> values, 
					Transformer<R, Prop<? extends CCollection<C>>> transformer,
					boolean editable) {
		this.values = values;
		this.transformer = transformer;
		values.features().addListener(this);
		this.editable = editable;
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		fireChange();
	}
	
	private void fireChange() {
		for (TableRowViewListener listener : listeners) {
			listener.tableRowViewChanged(this, true);
		}
	}
	
	@Override
	public Object getColumn(R row, int column) {
		Prop<? extends CCollection<C>> cProp = transformer.transform(row);
		if (cProp == null) return null;
		
		CCollection<C> c = cProp.get();
		if (c == null) return null;
		
		CList<? extends C> valueList = values.get();
		if (valueList == null) return null;
		
		if (column < 0 || column > valueList.size()) {
			return null;
		}
		
		C item = valueList.get(column);
		
		return c.contains(item);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return Boolean.class;
	}

	@Override
	public int getColumnCount() {
		CList<? extends C> valueList = values.get();
		if (valueList == null) {
			return 0;
		} else {
			return valueList.size();
		}
	}

	@Override
	public String getColumnName(int column) {
		
		CList<? extends C> valueList = values.get();
		if (valueList == null) return null;
		
		if (column < 0 || column > valueList.size()) {
			return null;
		}

		return valueList.get(column).toString();
	}

	@Override
	public boolean isEditable(R row, int column) {
		return editable;
	}

	@Override
	public void setColumn(R row, int column, Object value) {
		if (!editable) return;
		
		boolean newState;
		if (value instanceof Boolean) {
			newState = (Boolean) value;
		} else {
			return;
		}
		
		Prop<? extends CCollection<C>> cProp = transformer.transform(row);
		if (cProp == null) return;
		
		CCollection<C> c = cProp.get();
		if (c == null) return;
		
		CList<? extends C> valueList = values.get();
		if (valueList == null) return;
		
		if (column < 0 || column > valueList.size()) {
			return;
		}
		
		C item = valueList.get(column);
		
		//Current boolean state
		boolean currentState = c.contains(item);
		
		//If needed, toggle state
		if (newState != currentState) {
			if (currentState) {
				c.remove(item);
			} else {
				c.add(item);
			}
		}
	}

	@Override
	public void addListener(TableRowViewListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(TableRowViewListener listener) {
		if (!listeners.remove(listener)) {
			logger.log(Level.FINE, "Removed TableRowViewListener which was not registered.", new Exception("Stack Trace"));
		}
	}
	
	@Override
	public void dispose() {
		values.features().removeListener(this);
	}
}
