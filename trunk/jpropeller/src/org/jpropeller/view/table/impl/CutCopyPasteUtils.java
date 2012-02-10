package org.jpropeller.view.table.impl;

import java.awt.event.KeyEvent;

/**
 * Utilities for working with keyevents to detect cut/copy/paste commands
 */
public class CutCopyPasteUtils {

	/**
	 * Return true if a key has the OS-specific modifier
	 * expected for Cut/Copy/Paste shortcut.
	 * @param event	The {@link KeyEvent}
	 * @return	True if key press is modified appropriately
	 */
	public static boolean isCutCopyPasteModified(KeyEvent event) {
        String vers = System.getProperty("os.name").toLowerCase();
        boolean m = event.isControlDown();
        if (vers.indexOf("mac") != -1) {
        	m = event.isMetaDown();
        }
        return m;
	}
	
	/**
	 * Return true if a key has the OS-specific modifier
	 * expected for Cut/Copy/Paste shortcut, and a specified key code.
	 * @param event	The {@link KeyEvent}
	 * @param vkCode The key code expected
	 * @return	True if key press is modified appropriately, and has
	 * 			specified key code.
	 */
	public static boolean isCutCopyPasteModifiedKey(KeyEvent event, int vkCode) {
		return isCutCopyPasteModified(event) && event.getKeyCode()==vkCode;
	}
	
	/**
	 * Is key event a copy command?
	 * @param event	The {@link KeyEvent}
	 * @return	True if event is a copy
	 */
    public static boolean isCopy(KeyEvent event) {
    	return isCutCopyPasteModifiedKey(event, KeyEvent.VK_C);
    } 

	/**
	 * Is key event a copy command?
	 * @param event	The {@link KeyEvent}
	 * @return	True if event is a cut
	 */
    public static boolean isCut(KeyEvent event) {
    	return isCutCopyPasteModifiedKey(event, KeyEvent.VK_X);
    } 

	/**
	 * Is key event a paste command?
	 * @param event	The {@link KeyEvent}
	 * @return	True if event is a paste
	 */
    public static boolean isPaste(KeyEvent event) {
    	return isCutCopyPasteModifiedKey(event, KeyEvent.VK_V);
    } 

}
