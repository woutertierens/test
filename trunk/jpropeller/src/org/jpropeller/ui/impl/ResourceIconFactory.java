package org.jpropeller.ui.impl;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jpropeller.ui.IconFactory;

/**
 * An {@link IconFactory} loading images as png's stored
 * in a standard directory structure and retrieved as resources
 * relative to a given class
 */
public class ResourceIconFactory implements IconFactory {

	private final Map<IconSize, String> sizeStrings;
	private final Class<?> resourceClass;
	
	/**
	 * Create a {@link ResourceIconFactory} with default size names
	 * "small", "medium", "large"
	 * @param resourceClass		The class used to load resources for images
	 */
	public ResourceIconFactory(Class<?> resourceClass) {
		this(resourceClass, null);
	}
	
	/**
	 * Create a {@link ResourceIconFactory} with custom size names. 
	 * @param resourceClass		The class used to load resources for images
	 * @param sizeNames			The names for each icon size. Any mapping here
	 * 							replaces teh preexisting default mapping
	 */
	public ResourceIconFactory(Class<?> resourceClass, Map<IconSize, String> sizeNames) {
		this.resourceClass = resourceClass;
		sizeStrings = new EnumMap<IconSize, String>(IconSize.class);
		sizeStrings.put(IconSize.SMALL, "small");
		sizeStrings.put(IconSize.MEDIUM, "medium");
		sizeStrings.put(IconSize.LARGE, "large");
		if (sizeNames != null) {
			sizeStrings.putAll(sizeNames);
		}
	}
	
	@Override
	public Icon getIcon(IconSize size, String category, String name) {
		return new ImageIcon(getImage(size, category, name));
	}

	@Override
	public Image getImage(IconSize size, String category, String name) {
		String sizeString = sizeStrings.get(size);
		
		if (sizeString == null) {
			sizeString = "small";
		}
		
		String location = sizeString + "/" + category + "/" + name + ".png";
		
		URL resource = resourceClass.getResource(location);
		if (resource != null) {
			try {
				return ImageIO.read(resource);
			} catch (IOException e) {
				//TODO should log this
			}
		}
		
		try {
			URL fallBackResource = ResourceIconFactory.class.getResource(size.toString() + ".png");
			if (fallBackResource == null) {
				//TODO should log this
				return null;
			}
			return ImageIO.read(fallBackResource);	
		} catch (IOException e2) {
			//TODO should log this
			return null;
		}
	}
	
}
