package org.jpropeller.view.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jpropeller.view.JViewSource;
import org.jpropeller.view.ViewSystem;

/**
 * Default implementation of {@link ViewSystem}
 */
public class ViewSystemDefault implements ViewSystem {

	//Note that we ensure that we only ever add mappings from
	//classes to matching views, via registerViewFor
	@SuppressWarnings("unchecked")
	private Map<Class, JViewSource> viewMap = new HashMap<Class, JViewSource>();
	
	@Override
	public Color getErrorBackgroundColor() {
		return Color.PINK;
	}

	//See above - we ensure mappings are between corresponding types
	@SuppressWarnings("unchecked")
	@Override
	public <M> JViewSource<? super M> jviewSourceFor(Class<M> clazz) {
		return viewMap.get(clazz);
	}
	
	@Override
	public <M> void registerJViewSourceFor(Class<M> clazz, JViewSource<? super M> source) {
		viewMap.put(clazz, source);
	}

	@Override
	public String getStandard3ColumnDefinition() {
		//Old form where components grow
		//"right:pref, $lcgap, min:grow"
		return "right:pref:grow, $lcgap, 200px";
	}
}
