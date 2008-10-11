package org.jpropeller.info;

/**
 * Gives information about a property, in terms of its
 * editability and whether it is single or indexed, etc.
 * @author shingoki
 */
public enum PropInfo {

	/**
	 * Editable single prop
	 */
	EDITABLE(PropEditability.EDITABLE, PropAccessType.SINGLE),
	/**
	 * Default single prop
	 */
	DEFAULT(PropEditability.DEFAULT, PropAccessType.SINGLE),
	/**
	 * Editable list prop
	 */
	EDITABLE_LIST(PropEditability.EDITABLE, PropAccessType.LIST),
	/**
	 * Default list prop
	 */
	DEFAULT_LIST(PropEditability.DEFAULT, PropAccessType.LIST),
	/**
	 * Editable map prop 
	 */
	EDITABLE_MAP(PropEditability.EDITABLE, PropAccessType.MAP),
	/**
	 * Default map prop 
	 */
	DEFAULT_MAP(PropEditability.DEFAULT, PropAccessType.MAP);
	
	private PropEditability editability;
	private PropAccessType type;

	private PropInfo(PropEditability editability, PropAccessType type) {
		this.editability = editability;
		this.type = type;
	}
	
	/**
	 * Whether prop is editable or not
	 * @return
	 * 		editability
	 */
	public PropEditability getEditability() {
		return editability;
	}
	
	/**
	 * Access type of prop (single, list, map etc.)
	 * @return
	 * 		type
	 */
	public PropAccessType getAccessType() {
		return type;
	}
	
}
