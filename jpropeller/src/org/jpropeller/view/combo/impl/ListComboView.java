package org.jpropeller.view.combo.impl;

import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.constrained.impl.SelectionFromCollectionProp;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.combo.ListComboBoxReference;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} displaying a {@link CList} as a {@link JComboBox},
 * using a {@link ListComboBoxModel}.
 *
 * @param <T> The type of element in the {@link CList}
 */
public class ListComboView<T> implements JView, ChangeListener {

	private final JComboBox combo;
	private final ListComboBoxReference<T> model;
	private final ListComboBoxModel<T> comboModel;
	private final Prop<Boolean> locked;
	private UpdateManager updateManager;

	/**
	 * Make a new {@link ListComboView}
	 * @param comboModel		The combo model to be displayed
	 */
	private ListComboView(ListComboBoxModel<T> comboModel) {
		this(comboModel, null);
	}
	
	/**
	 * Make a new {@link ListComboView}
	 * @param comboModel		The combo model to be displayed
	 * @param locked			If this is non-null and has value true, view will not allow editing
	 */
	private ListComboView(ListComboBoxModel<T> comboModel, Prop<Boolean> locked) {
		this.comboModel = comboModel;
		this.model = comboModel.getModel();
		combo = new JComboBox(comboModel);
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		this.locked = locked;
		if (locked != null) {
			locked.features().addListener(this);
		}

		//Start out up to date
		update();
	}
	
	/**
	 * Make a new {@link ListComboView}
	 * @param value				The prop containing the list to display
	 * @param valueClass 		The class of value in the list
	 * @param selectionMustBeInList		If true, selection is constrained to be in list.
	 * @param selectFirst		If true, then the selection will be set to the first element
	 * 							of the list when it must be constrained.
	 * 							If false, selection is set to null when constrained.
	 * 							{@link SelectionFromCollectionProp}
	 * @param <S>				The type of value in the list 
	 * @return 					New {@link ListComboView}
	 */
	public static <S extends Changeable> ListComboView<S> create(Prop<CList<S>> value, Class<S> valueClass, boolean selectionMustBeInList, boolean selectFirst) {
		ListComboBoxReference<S> comboRef = ListComboBoxReferenceDefault.create(value, valueClass, selectionMustBeInList, selectFirst);
		ListComboBoxModel<S> comboModel = new ListComboBoxModel<S>(comboRef, valueClass);
		return new ListComboView<S>(comboModel);
	}
	
	/**
	 * Make a new {@link ListComboView}
	 * @param valueClass 		The class of value in the list
	 * @param list				The prop containing the list to display
	 * @param selection			The prop containing the selection
	 * 
	 * @param <S>				The type of value in the list 
	 * @return 					New {@link ListComboView}
	 */
	public static <S> ListComboView<S> create(
			Class<S> valueClass, 
			Prop<CList<S>> list, 
			Prop<S> selection) {
		ListComboBoxReference<S> comboRef = ListComboBoxReferenceDefault.create(list, selection);
		ListComboBoxModel<S> comboModel = new ListComboBoxModel<S>(comboRef, valueClass);
		return new ListComboView<S>(comboModel);
	}
	
	/**
	 * Make a new {@link ListComboView}
	 * @param valueClass 		The class of value in the list
	 * @param list				The prop containing the list to display
	 * @param selection			The prop containing the selection
	 * @param locked			If this is non-null and has value true, view will not allow editing
	 * 
	 * @param <S>				The type of value in the list 
	 * @return 					New {@link ListComboView}
	 */
	public static <S> ListComboView<S> create(
			Class<S> valueClass, 
			Prop<CList<S>> list, 
			Prop<S> selection,
			Prop<Boolean> locked) {
		ListComboBoxReference<S> comboRef = ListComboBoxReferenceDefault.create(list, selection);
		ListComboBoxModel<S> comboModel = new ListComboBoxModel<S>(comboRef, valueClass, locked);
		return new ListComboView<S>(comboModel, locked);
	}
	
	/**
	 * Make a new {@link ListComboView}
	 * @param model				The model for the view
	 * @param valueClass 		The class of value in the list
	 * @param <S>				The type of value in the list 
	 * @return 					New {@link ListComboView}
	 */
	public static <S extends Changeable> ListComboView<S> create(ListComboBoxReference<S> model, Class<S> valueClass) {
		ListComboBoxModel<S> comboModel = new ListComboBoxModel<S>(model, valueClass);
		return new ListComboView<S>(comboModel);
	}

	@Override
	public JComboBox getComponent() {
		return combo;
	}

	@Override
	public boolean selfNaming() {
		//View just displays combo
		return false;
	}

	@Override
	public void cancel() {
		//Editing (of selection) is instant
	}

	@Override
	public void commit() throws CompletionException {
		//Editing (of selection) is instant
	}

	@Override
	public boolean isEditing() {
		//Editing (of selection) is instant
		return false;
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		Props.getPropSystem().getUpdateManager().updateRequiredBy(this);
	}

	@Override
	public void dispose() {
		//Dispose our model
		comboModel.dispose();
		if (locked != null) {
			locked.features().removeListener(this);
		}
		updateManager.deregisterUpdatable(this);
	}

	@Override
	public void update() {
		//JComboBox updates itself automatically based on combo model changes, without using
		//the UpdateManager - the combo model does use UpdateManager though, and this 
		//achieves the same goal
		//We just manage enable/disable state of the JComboBox
		combo.setEnabled(!Props.isTrue(locked));
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}

	/**
	 * Get the reference for this view
	 * @return	The reference used by this view to reach displayed data
	 */
	public ListComboBoxReference<T> getReference() {
		return model;
	}
	
}
