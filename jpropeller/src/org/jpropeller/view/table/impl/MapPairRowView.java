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
import org.jpropeller.transformer.PairTransformer;
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
 * column showing a transformation of the mapped values for that key,
 * in the respective row.
 * 
 * A {@link Transformer} is used to go from rows of type R to the {@link Map}
 * for that row.
 * @param <R>		The type of row 
 * @param <K> 		The type of key in the maps
 * @param <V> 		The type of value in the maps
 * @param <O>		The output type of the pair transformer
 */
public class MapPairRowView<R, K, V, O> implements TableRowView<R>, ChangeListener {

	private final static Logger logger = GeneralUtils.logger(MapPairRowView.class);
	
	private final Prop<? extends CList<? extends K>> keys;
	private final Transformer<? super R, ? extends Map<K, V>> transformer;
	private final PairTransformer<? super K, ? super V, ? extends O> pairTransformer;
	private final Class<O> oClass;
	
	private final Listeners<TableRowViewListener> listeners = new Listeners<TableRowViewListener>();
	
	/**
	 * Make a {@link MapPairRowView}
	 * @param oClass	The {@link Class} of output from the pairTransformer
	 * @param keys		The keys for which to display values, this determines
	 * 					the column list
	 * @param transformer		The {@link Transformer} from a Row of type R to a {@link Map} from
	 * 							key (type K) to value (type V)
	 * @param pairTransformer	The {@link PairTransformer} from a key (type K) and value (type V)
	 * 							to an output.
	 */
	public MapPairRowView(
			Class<O> oClass,
			Prop<? extends CList<? extends K>> keys, 
			Transformer<? super R, ? extends Map<K, V>> transformer,
			PairTransformer<? super K, ? super V, ? extends O> pairTransformer) {
		this.oClass = oClass;
		this.keys = keys;
		keys.features().addListener(this);
		this.transformer = transformer;
		this.pairTransformer = pairTransformer;
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		fireChange();
	}
	
	private void fireChange() {
		for (TableRowViewListener listener : listeners) {
			listener.tableRowViewChanged(this);
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
		V value = map.get(key);

		return pairTransformer.transform(key, value);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return oClass;
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
		
		return keyList.get(column).toString();
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
