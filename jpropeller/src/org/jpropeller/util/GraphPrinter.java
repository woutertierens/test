package org.jpropeller.util;

import java.util.IdentityHashMap;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.list.ListProp;

/**
 * Prints graphs of data based on {@link Bean}s
 * @author bwebster
 */
public class GraphPrinter {

	Map<Object, Long> indices = new IdentityHashMap<Object, Long>(200);
	long nextIndex = 0;
	int tabLevel = 0;
	StringBuilder sb = new StringBuilder(200);

	/**
	 * Print all beans in the graph starting from a root,
	 * as a tree
	 * @param root
	 * 		The root bean - all data reachable from here
	 * will be printed 
	 */
	public synchronized void printTree(Bean root) {
		clearIndices();
		print(root);
	}

	private void print(Bean bean) {
		Long index = getIndex(bean);
		if (index > -1) {
			print("Reference to " + index);
		} else {
			index = assignIndex(bean);
			print("Bean, index " + index + ", " + bean.getClass().getName());
			tabIn();
			for (GeneralProp<?> prop : bean.features().getList()) {
				
				//If we have single access, just print the single value
				if (prop.getInfo().getAccessType() == PropAccessType.SINGLE) {
					GenericProp<?> genericProp = (GenericProp<?>)prop;
					print(prop.getName().getString() + " = ");
					tabIn();
					printValue(genericProp.get());
					tabOut();
				//For list access, print all values in list
				} else if (prop.getInfo().getAccessType() == PropAccessType.LIST) {
					ListProp<?> listProp = (ListProp<?>)prop;
					print(prop.getName().getString() + " = list of:");
					for (Object value : listProp) {
						tabIn();
						printValue(value);
						tabOut();
					}
					
				//TODO handle map access
					
				//This should not happen
				} else {
					print("Unexpected - unknown access type in General prop - " + prop.toString());
				}
			}
			tabOut();
		}
	}
	
	private void printValue(Object value) {
		if (value instanceof Bean) {
			print ((Bean)value);
		} else {
			print(value.toString());
		}
	}
	
	private void print(String s) {
		sb.setLength(0);
		for (int i = 0; i < tabLevel; i++) {
			sb.append("-");
		}
		sb.append(s);
		System.out.println(sb);
	}
	
	private void tabIn() {
		tabLevel++;
	}
	
	private void tabOut() {
		tabLevel--;
	}
	
	private void clearIndices() {
		indices.clear();
	}
	
	private Long getIndex(Object o) {
		Long index = indices.get(o);
		if (index == null) {
			return -1L;
		} else {
			return index;
		}
	}
	
	private Long assignIndex(Object o) {
		Long currentIndex = getIndex(o);
		if (currentIndex !=-1) throw new IllegalArgumentException("Object " + o + " already has an index, " + currentIndex);
		
		Long index = nextIndex;
		nextIndex++;
		
		indices.put(o, index);
		
		return index;
	}
	
}
