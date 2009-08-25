package org.jpropeller.view.bean.impl;

import java.util.HashMap;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.PathReferenceBuilder;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;
import org.jpropeller.view.table.impl.SingleSelectionListTableView;

/**
 * {@link PropViewFactory} for very simple lists.
 */
public class ListViewFactory implements PropViewFactory {
	private static final Map<String, Class<?>> genericViews = new HashMap<String, Class<?>>() {{
		put("<java.lang.String>", String.class);
		put("<java.lang.Integer>", Integer.class);
		put("<java.lang.Boolean>", Boolean.class);
		put("<java.lang.Long>", Long.class);
		put("<java.lang.Integer>", Integer.class);
		put("<java.lang.Float>", Float.class);
		put("<java.lang.Double>", Double.class);
	}};
	
	@Override
	public boolean providesFor(PropName<?> name) {
		return 
				genericViews.containsKey(name.getPropParametricTypes())
			&&	CList.class.isAssignableFrom(name.getGenericPropClass());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <M> JView viewFor(final Reference<? extends Bean> model,
			final PropName<M> displayedName) {
		Class<?> genericType = genericViews.get(displayedName.getPropParametricTypes());
		Transformer<Bean, Prop<CList<?>>> transformer = new Transformer<Bean, Prop<CList<?>>>() {
			@Override
			public Prop<CList<?>> transform(Bean s) {
				return (Prop<CList<?>>) s.features().getUnsafe(displayedName);
			}
		}; 
		Prop<CList<?>> pathProp = PathReferenceBuilder.listFromRef(model, (Class)genericType).to(transformer).value();
		final ListAndSelectionAndValueReference<?> ref = 
			new ListAndSelectionAndValueReferenceDefault(pathProp, genericType);
		
		//FIXME Is this required, or can it be a normal row view
		TableRowView<?> rowView = new TableRowView<?>(){
		
			@Override
			public void setColumn(Object row, int column, Object value) {
				//((ListProp)model.value().get().features().getUnsafe(displayedName)).set(ref.selection().get().intValue(), value);
			}
		
			@Override
			public boolean isEditable(Object row, int column) {
				return false;
			}
		
			@Override
			public String getColumnName(int column) {
				return "Value";
			}
		
			@Override
			public int getColumnCount() {
				return 1;
			}
		
			@Override
			public Class getColumnClass(int column) {
				return genericViews.get(displayedName.getPropParametricTypes());
			}
		
			@Override
			public Object getColumn(Object row, int column) {
				return row;
			}
			@Override
			public void addListener(TableRowViewListener listener) {
				//View is immutable, no changes
			}

			@Override
			public void removeListener(TableRowViewListener listener) {
				//View is immutable, no changes
			}
			@Override
			public void dispose() {
			}

		};
		

		SingleSelectionListTableView<?> tableView = 
			new SingleSelectionListTableView(ref, rowView);
		
		
		return tableView;
	}
	
}
