package demo.list;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Test operation of Iterator
 * @author shingoki
 *
 */
public class TestIterator {

	/**
	 * Test operation of Iterator
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		
		Iterator<String> it = list.iterator();
		it.next();
		
		list.add("e");
		
		it.next();
	}

}
