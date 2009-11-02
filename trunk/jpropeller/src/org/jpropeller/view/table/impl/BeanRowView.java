package org.jpropeller.view.table.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;

/**
 * A {@link TableRowView} of {@link Bean} instances
 * @param <R>		The type of {@link Bean} displayed
 */
public class BeanRowView<R extends Bean> implements TableRowView<R> {

	private final static Logger logger = GeneralUtils.logger(BeanRowView.class);
	
	private List<Prop<?>> props;
	private R bean;
	private final boolean editable;
	private final Map<Class<?>, BeanRowValueProcessor<R, ?>> filters = new HashMap<Class<?>, BeanRowValueProcessor<R, ?>>();

	/**
	 * Create a {@link BeanRowView} showing all
	 * non-generic {@link Prop}s of the specified {@link Bean}
	 * 
	 * @param bean		The bean to use as a "template"
	 */
	public BeanRowView(R bean) {
		this(bean, buildFilteredPropsList(bean), true);
	}

	/**
	 * Create a {@link BeanRowView} showing all
	 * non-generic {@link Prop}s of the specified {@link Bean}
	 * 
	 * @param bean		The bean to use as a "template"
	 * @param editable	True to enable editing of editable props, false to disable all editing
	 */
	public BeanRowView(R bean, boolean editable) {
		this(bean, buildFilteredPropsList(bean), editable);
	}
	
	//FIXME it would be nicer to use a list of propnames
	//instead of props, but lists of PropName<? extends Prop<?>,?> are
	//much harder to handle than List<Prop<?>>. There is no technical
	//disadvantage to using Props instead, except that sometimes
	//a user might wish to create a PropName from scratch, and it is
	//harder to create a whole Prop just to act as a source for the
	//PropName
	/**
	 * Create a {@link BeanRowView}
	 * @param bean		The bean to use as a "template"
	 * 
 	 * @param props		The properties to display, in order
	 * 					Note that the names of these props
	 * 					are used to look up the actual props 
	 * 					to display for each bean - in essence 
	 * 					these are "example" props, not the actual 
	 * 					props to display
	 * @param editable	True to enable editing of editable props, false to disable all editing
	 */
	public BeanRowView(R bean, List<Prop<?>> props, boolean editable) {
		super();
		this.bean = bean;
		this.props = props;
		this.editable = editable;
	}
	
	/**
	 * Put a {@link BeanRowValueProcessor} for a given type.
	 * This will be asked to {@link BeanRowValueProcessor#process(Object, Object)}
	 * any new values columns having the specified class.
	 * This allows for value filtering that will only be applied
	 * in the context of a given row view, rather than at the {@link Prop}
	 * level.
	 * @param <T>			The type
	 * @param clazz			The {@link Class} of the type
	 * @param filter		The {@link BeanRowValueProcessor}
	 */
	public <T> void putFilter(Class<T> clazz, BeanRowValueProcessor<R, T> filter) {
		filters.put(clazz, filter);
	}
	
	/**
	 * Remove a {@link BeanRowValueProcessor} registered for the
	 * specified class using {@link #putFilter(Class, BeanRowValueProcessor)} 
	 * @param <T>			The type
	 * @param clazz			The {@link Class} of the type
	 */
	public <T> void removeFilter(Class<T> clazz) {
		filters.remove(clazz);
	}
	
	private final static List<Prop<?>> buildFilteredPropsList(Bean bean) {
		List<Prop<?>> list = PropUtils.buildNonGenericPropsList(bean);
		List<Prop<?>> filtered = new ArrayList<Prop<?>>(list.size());
		for (Prop<?> prop : list) {
			if (!prop.features().hasMetadata(OMIT_FROM_TABLE_ROW_VIEW)) {
				filtered.add(prop);
			}
		}
		return filtered;
	}

	/**
	 * Find the {@link Prop} for the specified row bean
	 * @param row
	 * 		The bean from which to get prop
	 * @param column
	 * 		The index of the property to get
	 * @return
	 * 		The {@link Prop}, or null if there is no {@link Prop}
	 * in the specified {@link Bean} for the required {@link PropName}
	 * at the specified column
	 */
	private Prop<?> findBeanProp(R row, int column) {
		//Get name from the prop for the column, then use this
		//to look up the value of the corresponding prop in the bean
		PropName<?> name = props.get(column).getName();
		Prop<?> beanProp = row.features().get(name);
		return beanProp;
	}
	
	@Override
	public Object getColumn(R row, int column) {
		Prop<?> beanProp = findBeanProp(row, column);
		
		//Null value where prop does not exist in current bean
		if (beanProp == null) return null;
		
		return beanProp.get();
	}

	@Override
	public Class<?> getColumnClass(int column) {
		//Class of column is data class of corresponding prop
		return props.get(column).getName().getPropClass();
	}

	@Override
	public int getColumnCount() {
		//One column per prop
		return props.size();
	}

	@Override
	public String getColumnName(int column) {
		return PropUtils.localisedName(bean.getClass(), props.get(column));
	}

	@Override
	public boolean isEditable(R row, int column) {
		//Not editable if all editing disables
		if (!editable) return false;
		
		Prop<?> beanProp = findBeanProp(row, column);
		
		//Uneditable where prop does not exist in current bean
		if (beanProp == null) return false;

		//We can edit if the prop is editable
		return (beanProp.getEditability() == PropEditability.EDITABLE);
	}

	//See below for explanation of suppression
	@SuppressWarnings("unchecked")
	@Override
	public void setColumn(R row, int column, Object value) {
		Prop<?> beanProp = findBeanProp(row, column);
		
		//Can't edit where prop does not exist in current bean
		if (beanProp == null) return;

		//We can only set an editable prop
		if (beanProp instanceof Prop) {
			//Cast to raw type, since we are about to check that
			//the value is off a suitable class directly
			Prop ed = (Prop)beanProp;
			
			Class<?> propClass = ed.getName().getPropClass();
			
			//The prop class of ed's name gives us the accepted class,
			//so we can safely set any value where this class is
			//assignable from the value
			if (value == null || propClass.isAssignableFrom(value.getClass())) {
				try {
					//Filter is placed in map only for matching class,
					//so we know it will accept the value, which is
					//an instance of the class as checked just above
					BeanRowValueProcessor filter = filters.get(propClass);
					if (filter != null) {
						try {
							ed.set(filter.process(row, value));
						} catch (IllegalArgumentException iae) {
							//If edit is rejected, do nothing
						}
					} else {
						ed.set(value);
					}
				} catch (ReadOnlyException e) {
					logger.log(Level.SEVERE, "Read-only exception setting new value - should not occur since row view marks read-only props as uneditable");
				} catch (InvalidValueException e) {
					logger.log(Level.FINEST, "Can't set invalid new value in " + beanProp, e);
				}
			} else {
				logger.warning("BeanRowView got incorrect value, expecting " + ed.getName().getPropClass() + ", but got value '" + value + "' of " + value.getClass());
			}
		}
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

}
