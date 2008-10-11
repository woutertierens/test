package org.jpropeller.view.table.impl;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.table.TableRowView;

/**
 * A {@link TableRowView} of {@link Bean} instances
 * @author bwebster
 */
public class BeanRowView implements TableRowView<Bean> {

	List<Prop<?>> props;

	/**
	 * Create a {@link BeanRowView} showing all
	 * {@link Prop}s of the specified {@link Bean}
	 * @param b
	 * 		The bean to use as a "template"
	 */
	public BeanRowView(Bean b) {
		this(PropUtils.buildPropsList(b));
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
	 * @param props
	 * 		The properties to display, in order
	 * 		Note that the names of these props
	 * are used to look up the actual props to display
	 * for each bean - in essence these are "example"
	 * props, not the actual props to display
	 */
	public BeanRowView(List<Prop<?>> props) {
		super();
		this.props = props;
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
		PropName<? extends Prop<?>, ?> name = props.get(column).getName();
		Prop<?> beanProp = row.props().get(name);
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
		return PropUtils.localisedName(props.get(column));
	}

	@Override
	public boolean isEditable(Bean row, int column) {
		Prop<?> beanProp = findBeanProp(row, column);
		
		//Uneditable where prop does not exist in current bean
		if (beanProp == null) return false;

		//We can edit if the prop is editable
		return (beanProp instanceof EditableProp<?>);
	}

	//See below for explanation of suppression
	@SuppressWarnings("unchecked")
	@Override
	public void setColumn(Bean row, int column, Object value) {
		Prop<?> beanProp = findBeanProp(row, column);
		
		//Can't edit where prop does not exist in current bean
		if (beanProp == null) return;

		//We can only set an editable prop
		if (beanProp instanceof EditableProp) {
			//Cast to raw type, since we are about to check that
			//the value is off a suitable class directly
			EditableProp ed = (EditableProp)beanProp;
			
			//The prop class of ed's name gives us the accepted class,
			//so we can safely set any value where this class is
			//assignable from the value
			if (ed.getName().getPropClass().isAssignableFrom(value.getClass())) {
				ed.set(value);
			}
		}
	}

}
