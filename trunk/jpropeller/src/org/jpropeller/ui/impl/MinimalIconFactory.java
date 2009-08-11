package org.jpropeller.ui.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jpropeller.ui.IconFactory;

/**
 * Minimal implementation of {@link IconFactory} returns a blank
 * 16x16 image for all images, and an {@link ImageIcon} showing
 * this image for all icons.
 */
public class MinimalIconFactory implements IconFactory {

	private final static Image NO_IMAGE = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private final static ImageIcon NO_ICON = new ImageIcon(NO_IMAGE);
	
	@Override
	public Icon getIcon(IconSize size, String category, String name) {
		return NO_ICON;
	}

	@Override
	public Image getImage(IconSize size, String category, String name) {
		return NO_IMAGE;
	}

}
