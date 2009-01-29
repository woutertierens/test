/*
 *  $Id: org.eclipse.jdt.ui.prefs,v 1.1 2008/03/24 11:20:15 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package test.properties.list;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropeller.collection.impl.ObservableListCalculated;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.calculated.PropCalculation;
import org.jpropeller.properties.list.ListSelectionEditableValueReference;
import org.jpropeller.properties.list.impl.ListSelectionEditableValueReferenceDefault;
import org.jpropeller.reference.impl.EditableReferenceToChangeable;
import org.jpropeller.view.bean.impl.BeanPropListEditor;
import org.jpropeller.view.table.impl.ListTableView;
import org.jpropeller.view.table.impl.TableRowViewDirect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.example.LotsOfProps;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Test {@link ObservableListCalculated}
 */
public class TestObservableListCalculated {

	private LotsOfProps a;
	private LotsOfProps b;
	private MultiplesListCalc calc;
	private ObservableListCalculated<Integer> list;
	
	/**
	 * Set up before each test
	 * @throws java.lang.Exception
	 * 		On any error
	 */
	@Before
	public void setUp() throws Exception {
		a = new LotsOfProps();
		b = new LotsOfProps();
		calc = new MultiplesListCalc(a.intProp(), b.intProp());
		list = new ObservableListCalculated<Integer>(calc);
	}
	
	/**
	 * Test that lists are recalculated as appropriate
	 * @throws Exception
	 * 		On any error
	 */
	@Test
	public void testCalculation() throws Exception {
		
		assertList(1, 1);
		assertList();

		System.out.println();

		System.out.println("####Will set a = 2");
		a.intProp().set(2);
		System.out.println("####Set a = 2");
		assertList(2, 1);
		assertList();

		System.out.println();

		System.out.println("####Will set a = 1");
		a.intProp().set(1);
		System.out.println("####Set a = 1");
		System.out.println("####Will set b = 2");
		b.intProp().set(2);
		System.out.println("####Set b = 2");
		
		assertList(1, 2);
		assertList();

		System.out.println();

		//Thread.sleep(1000);
		
		System.out.println("#### - THIS FAILS Will set a = 2");
		a.intProp().set(2);
		System.out.println("####Set a = 2");
		assertList(2, 1);
		assertList();

		System.out.println();

		
		Random r = new Random(0);
		
		for (int i = 0; i < 100; i++) {
			int aVal = r.nextInt(100);
			int bVal = r.nextInt(100);
			a.intProp().set(aVal);
			b.intProp().set(bVal);
			System.out.println("####i = " + i + ", set a = " + aVal + ", b = " + bVal);
			assertList(aVal, bVal);
			assertList();
		}
	}

	private void assertList() {
		int length = a.intProp().get();
		int factor = b.intProp().get();
		if (length < 0) length = 0;

		System.out.println("Will check length (a) = " + length + ", factor (b) = " + factor);
		
		assertList(length, factor);
	}

	private void assertList(int length, int factor) {
		System.out.println("Expecting length = " + length + ", factor = " + factor);
		Assert.assertEquals(length, list.size());
		System.out.println("Checking contents");
		
		for (int i = 0; i < length; i++) {
			Assert.assertEquals(factor * i, list.get(i));
		}
	}

	private static class MultiplesListCalc implements PropCalculation<List<Integer>> {

		Set<GeneralProp<?>> sourceProps;
		GenericProp<Integer> a;
		GenericProp<Integer> b;
		
		public MultiplesListCalc(GenericProp<Integer> a, GenericProp<Integer> b) {
			this.a = a;
			this.b = b;
			HashSet<GeneralProp<?>> props = new HashSet<GeneralProp<?>>();
			props.add(a);
			props.add(b);
			sourceProps = Collections.unmodifiableSet(props);
		}

		@Override
		public Set<GeneralProp<?>> getSourceProps() {
			return sourceProps;
		}
	
		@Override
		public List<Integer> calculate() {
			int length = a.get();
			int factor = b.get();
			
			System.out.println("Calculating length = " + length + ", factor = " + factor);
			
			if (length < 0) length = 0;
			ArrayList<Integer> list = new ArrayList<Integer>(length);
			for (int i = 0; i < length; i++) {
				list.add(factor * i);
			}
			return list;
		}
	};
	
	/**
	 * Show a UI allowing editing/viewing of pair of beans controlling a list
	 * @param args
	 * 		Ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runDemoApp();
			}
		});
		
	}
	
	private static void runDemoApp() {
		LotsOfProps a = new LotsOfProps();
		LotsOfProps b = new LotsOfProps();
		MultiplesListCalc calc = new MultiplesListCalc(a.intProp(), b.intProp());
		ObservableListCalculated<Integer> list = new ObservableListCalculated<Integer>(calc);
		
		final ListSelectionEditableValueReference<Integer> listRef = new ListSelectionEditableValueReferenceDefault<Integer>(list, Integer.class);
		ListTableView<Integer> listView = new ListTableView<Integer>(listRef, new TableRowViewDirect<Integer>(Integer.class, "Values"));

		System.out.println(listView.getComponent().getModel().getColumnClass(0));
		
		BeanPropListEditor<LotsOfProps> aEditor = BeanPropListEditor.create(EditableReferenceToChangeable.create(LotsOfProps.class, a));
		BeanPropListEditor<LotsOfProps> bEditor = BeanPropListEditor.create(EditableReferenceToChangeable.create(LotsOfProps.class, b));
		
		//Layout the editors in a frame
		FormLayout layout = new FormLayout(
				"fill:200dlu:grow, 5dlu, fill:200dlu:grow", 
				"fill:200dlu:grow, 5dlu, fill:200dlu:grow");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		builder.append(aEditor.getComponent());
		builder.append(bEditor.getComponent());
		builder.nextRow();
		builder.append(listView.getComponent());
		
		JFrame frame = new JFrame("Test Calculated List");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(builder.getPanel());
		frame.pack();
		frame.setVisible(true);		
	}
}
