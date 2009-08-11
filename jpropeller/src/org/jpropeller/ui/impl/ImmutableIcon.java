package org.jpropeller.ui.impl;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Immutable;

/**
 * An {@link Icon} that is {@link Immutable}, and hence can be used
 * in a {@link Prop}
 */
public class ImmutableIcon implements Icon, Immutable {

	private final Icon delegate;

	/**
	 * Make an {@link ImmutableIcon} from a copy of an image
	 * @param image
	 * 		The image to use
	 * @return
	 * 		The {@link ImmutableIcon}
	 */
	public static ImmutableIcon fromImage(Image image) {
		return new ImmutableIcon(new ImageIcon(image.getScaledInstance(-1, -1, Image.SCALE_FAST)));
	}
	
	private ImmutableIcon(Icon delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int getIconHeight() {
		return delegate.getIconHeight();
	}

	@Override
	public int getIconWidth() {
		return delegate.getIconWidth();
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		delegate.paintIcon(c, g, x, y);
	}

}
