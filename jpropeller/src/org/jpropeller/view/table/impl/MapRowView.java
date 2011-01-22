package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * {@link TableRowView} of rows R that each contain a {@link Map}
 * from K to V.
 * 
 * Columns are allocated according to a {@link CList} of K, with each
 * column showing the mapped values for that key, in the respective row.
 * 
 * A {@link Transformer} is used to go from rows of type R to the {@link Map}
 * for that row.
 * @param <R>		The type or row 
 * @param <K> 		The type of key in the maps
 * @param <V> 		The type of value in the maps
 */
public class MapRowView<R, K, V> implements TableRowView<R>, ChangeListener {

	private final static Logger logger = GeneralUtils.logger(MapRowView.class);
	
	private final Prop<? extends CList<? extends K>> keys;
	private final Transformer<? super R, ? extends Map<K, V>> transformer;
	private final Class<V> vClass;
	
	private final Listeners<TableRowViewListener> listeners = new Listeners<TableRowViewListener>();
	
	private final String overridingName;

	/**
	 * Make a {@link MapRowView}
	 * @param vClass	The {@link Class} of value in the maps
	 * @param keys		The keys for which to display values, this determines
	 * 					the column list
	 * @param transformer	The {@link Transformer} from a Row of type R to a {@link Map} from
	 * 							key (type K) to value (type V)
	 */
	public MapRowView(Class<V> vClass, Prop<? extends CList<? extends K>> keys, Transformer<? super R, ? extends Map<K, V>> transformer) {
		this(vClass, keys, transformer, null);
	}
	
	/**
	 * Make a {@link MapRowView}
	 * @param vClass	The {@link Class} of value in the maps
	 * @param keys		The keys for which to display values, this determines
	 * 					the column list
	 * @param transformer	The {@link Transformer} from a Row of type R to a {@link Map} from
	 * 							key (type K) to value (type V)
	 * @param overridingName	If null, this name is used for the column, regardless of key name
	 */
	public MapRowView(Class<V> vClass, Prop<? extends CList<? extends K>> keys, Transformer<? super R, ? extends Map<K, V>> transformer, String overridingName) {
		this.vClass = vClass;
		this.keys = keys;
		keys.features().addListener(this);
		this.transformer = transformer;
		this.overridingName = overridingName;
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
		Map<K, V> map = transformer.transform(row);
		if (map == null) return null;
		
		CList<? extends K> keyList = keys.get();
		if (keyList == null) return null;
		
		if (column < 0 || column > keyList.size()) {
			return null;
		}
		
		K key = keyList.get(column);

		return map.get(key);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return vClass;
	}

	@Override
	public int getColumnCount() {
		CList<? extends K> keyList = keys.get();
		if (keyList == null) {
			return 0;
		} else {
			return keyList.size();
		}
	}

	@Override
	public String getColumnName(int column) {
		CList<? extends K> keyList = keys.get();
		if (keyList == null) return null;
		
		if (column < 0 || column > keyList.size()) {
			return null;
		}
		
		if (overridingName == null) {
			return keyList.get(column).toString();
		} else {
			return overridingName;
		}
	}

	@Override
	public boolean isEditable(R row, int column) {
		return false;
	}

	@Override
	public void setColumn(R row, int column, Object value) {
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
		keys.features().removeListener(this);
	}
}
