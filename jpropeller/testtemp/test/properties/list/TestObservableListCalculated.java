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


import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.impl.CListCalculated;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.reference.impl.ReferenceToChangeable;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.bean.impl.BeanEditor;
import org.jpropeller.view.table.impl.ListTableView;
import org.jpropeller.view.table.impl.TableRowViewDirect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.example.LotsOfProps;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Test {@link CListCalculated}
 */
public class TestObservableListCalculated {

	private final static Logger logger = GeneralUtils.logger(TestObservableListCalculated.class);
	
	private LotsOfProps a;
	private LotsOfProps b;
	private MultiplesListCalc calc;
	private CListCalculated<Integer> list;
	
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
		list = new CListCalculated<Integer>(calc);
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

		logger.finest("...");

		logger.finest("####Will set a = 2");
		a.intProp().set(2);
		logger.finest("####Set a = 2");
		assertList(2, 1);
		assertList();

		logger.finest("...");

		logger.finest("####Will set a = 1");
		a.intProp().set(1);
		logger.finest("####Set a = 1");
		logger.finest("####Will set b = 2");
		b.intProp().set(2);
		logger.finest("####Set b = 2");
		
		assertList(1, 2);
		assertList();

		logger.finest("...");

		//Thread.sleep(1000);
		
		logger.finest("#### - THIS FAILS Will set a = 2");
		a.intProp().set(2);
		logger.finest("####Set a = 2");
		assertList(2, 2);
		assertList();

		logger.finest("...");
		
		Random r = new Random(0);
		
		long startTime;
		
		
		for (int repeat = 0; repeat < 20; repeat++) {
			startTime = System.currentTimeMillis();
			
			for (int i = 0; i < 1000; i++) {
				int aVal = r.nextInt(100);
				int bVal = r.nextInt(100);
				a.intProp().set(aVal);
				b.intProp().set(bVal);
				logger.finest("####i = " + i + ", set a = " + aVal + ", b = " + bVal);
				assertList(aVal, bVal);
				assertList();
			}
			
			long runTime = (System.currentTimeMillis() - startTime);
			Assert.assertTrue("Testing time for 100 changes < 1000ms", runTime < 200);
			logger.fine("1000 changes took " + runTime + "ms");
		}
	}

	private void assertList() {
		int length = a.intProp().get();
		int factor = b.intProp().get();
		if (length < 0) length = 0;

		logger.finest("Will check length (a) = " + length + ", factor (b) = " + factor);
		
		assertList(length, factor);
	}

	private void assertList(int length, int factor) {
		logger.finest("Expecting length = " + length + ", factor = " + factor);
		Assert.assertEquals(length, list.size());
		logger.finest("Checking contents");
		
		for (int i = 0; i < length; i++) {
			Assert.assertEquals((int)factor * i, (int)list.get(i));
		}
	}

	private static class MultiplesListCalc implements Calculation<List<Integer>> {

		Set<Prop<?>> sourceProps;
		Prop<Integer> a;
		Prop<Integer> b;
		
		public MultiplesListCalc(Prop<Integer> a, Prop<Integer> b) {
			this.a = a;
			this.b = b;
			IdentityHashSet<Prop<?>> props = new IdentityHashSet<Prop<?>>();
			props.add(a);
			props.add(b);
			sourceProps = Collections.unmodifiableSet(props);
		}

		@Override
		public Set<Prop<?>> getSources() {
			return sourceProps;
		}
	
		@Override
		public List<Integer> calculate() {
			int length = a.get();
			int factor = b.get();
			
			logger.finest("Calculating length = " + length + ", factor = " + factor);
			
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
		
		GeneralUtils.enableNimbus();
		
		LotsOfProps a = new LotsOfProps();
		LotsOfProps b = new LotsOfProps();
		MultiplesListCalc calc = new MultiplesListCalc(a.intProp(), b.intProp());
		CListCalculated<Integer> list = new CListCalculated<Integer>(calc);
		
		final ListAndSelectionAndValueReference<Integer> listRef = new ListAndSelectionAndValueReferenceDefault<Integer>(list, Integer.class);
		ListTableView<Integer> listView = new ListTableView<Integer>(listRef, new TableRowViewDirect<Integer>(Integer.class, "Values"));
		JScrollPane listScroll = new JScrollPane(listView.getComponent());
		listScroll.setMinimumSize(new Dimension(100, 100));
		listScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		BeanEditor<LotsOfProps> aEditor = BeanEditor.create(ReferenceToChangeable.create(LotsOfProps.class, a));
		BeanEditor<LotsOfProps> bEditor = BeanEditor.create(ReferenceToChangeable.create(LotsOfProps.class, b));
		
		//Layout the editors in a frame
		FormLayout layout = new FormLayout(
				"fill:200dlu:grow, 5dlu, fill:200dlu:grow, 5dlu, fill:200dlu:grow", 
				"fill:default:grow");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		builder.append(aEditor.getComponent());
		builder.append(bEditor.getComponent());
		builder.append(listScroll);
		
		JFrame frame = new JFrame("Test Calculated List");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(builder.getPanel());
		frame.pack();
		frame.setVisible(true);		
	}
}
