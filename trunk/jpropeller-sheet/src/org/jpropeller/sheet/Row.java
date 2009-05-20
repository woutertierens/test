package org.jpropeller.sheet;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;

public class Row implements Changeable {

	private ChangeableFeaturesDefault features;

	public Row() {
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			//@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				//We just contain primitives, so there are no deep changes
				return null;
			}
		}, this);
	}
	
	public ChangeableFeatures features() {
		return features;
	}

}
