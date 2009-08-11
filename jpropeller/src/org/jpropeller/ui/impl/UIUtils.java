package org.jpropeller.ui.impl;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * Simple util methods for UI
 */
public class UIUtils {

	/**
	 * Clear the selected file in a {@link JFileChooser} without changing
	 * directory
	 * @param chooser		The {@link JFileChooser}
	 */
	public static void clearSelection(JFileChooser chooser) {
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		
		File directory = file.getParentFile();
		if (directory == null) {
			return;
		}
		
		chooser.setSelectedFile(new File(""));
		chooser.setSelectedFiles(new File[0]);
		chooser.setCurrentDirectory(directory);
	}
	
}
