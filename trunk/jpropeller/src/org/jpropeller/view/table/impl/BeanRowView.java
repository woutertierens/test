package org.jpropeller.view.table.impl;

import java.util.ArrayList;
import java.util.List;
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
 */
public class BeanRowView implements TableRowView<Bean> {

	private final static Logger logger = GeneralUtils.logger(BeanRowView.class);
	
	private List<Prop<?>> props;
	private Bean bean;
	private final boolean editable;

	/**
	 * Create a {@link BeanRowView} showing all
	 * non-generic {@link Prop}s of the specified {@link Bean}
	 * 
	 * @param bean		The bean to use as a "template"
	 */
	public BeanRowView(Bean bean) {
		this(bean, buildFilteredPropsList(bean), true);
	}

	/**
	 * Create a {@link BeanRowView} showing all
	 * non-generic {@link Prop}s of the specified {@link Bean}
	 * 
	 * @param bean		The bean to use as a "template"
	 * @param editable	True to enable editing of editable props, false to disable all editing
	 */
	public BeanRowView(Bean bean, boolean editable) {
		this(bean, buildFilteredPropsList(bean), editable);
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
	public BeanRowView(Bean bean, List<Prop<?>> props, boolean editable) {
		super();
		this.bean = bean;
		this.props = props;
		this.editable = editable;
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
	private Prop<?> findBeanProp(Bean row, int column) {
		//Get name from the prop for the column, then use this
		//to look up the value of the corresponding prop in the bean
		PropName<?> name = props.get(column).getName();
		Prop<?> beanProp = row.features().get(name);
		return beanProp;
	}
	
	@Override
	public Object getColumn(Bean row, int column) {
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
	public boolean isEditable(Bean row, int column) {
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
	public void setColumn(Bean row, int column, Object value) {
		Prop<?> beanProp = findBeanProp(row, column);
		
		//Can't edit where prop does not exist in current bean
		if (beanProp == null) return;

		//We can only set an editable prop
		if (beanProp instanceof Prop) {
			//Cast to raw type, since we are about to check that
			//the value is off a suitable class directly
			Prop ed = (Prop)beanProp;
			
			//The prop class of ed's name gives us the accepted class,
			//so we can safely set any value where this class is
			//assignable from the value
			if (ed.getName().getPropClass().isAssignableFrom(value.getClass())) {
				try {
					ed.set(value);
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
