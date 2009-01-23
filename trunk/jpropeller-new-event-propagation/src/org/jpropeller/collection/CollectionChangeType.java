package org.jpropeller.collection;

/**
 * Types of changes to a list. Each type of change requires
 * further information from a ListChange to determine the
 * scope of the change.
 */
public enum CollectionChangeType {
	/**
	 * Type constant for a change which makes no guarantees of any
	 * similarity between list before change and list after change.
	 * Any users of the list should completely reassess it making 
	 * no assumptions.
	 */
	COMPLETE,
	
	/**
	 * Type constant for a change where where the list remains the same
	 * length, but consecutive elements may have been replaced.
	 * The firstChangedIndex and lastChangedIndex can be used to find the 
	 * set of elements which may have been replaced - some may still be the 
	 * same.
	 */	
	ALTERATION,
	
	/**
	 * Type constant for a change where one or more consecutive elements are 
	 * deleted from the list. The firstChangedIndex and changeSize can be used
	 * to identify the deleted elements
	 */
	DELETION,
	
	/**
	 * Type constant for a change where one or more consecutive elements are 
	 * inserted into the list. The firstChangedIndex and changeSize can be used
	 * to identify the inserted elements
	 */
	INSERTION,
	
	/**
	 * Type constant for a change where list is completely cleared. Properties
	 * and indices are set sensibly, but the CLEAR type alone gives all required 
	 * information
	 */
	CLEAR
}