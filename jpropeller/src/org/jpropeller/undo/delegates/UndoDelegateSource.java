package org.jpropeller.undo.delegates;

import org.jpropeller.properties.change.Changeable;

/**
 * A source of {@link UndoDelegate}s for {@link Changeable}s
 */
public interface UndoDelegateSource {

	/**
	 * Get an {@link UndoDelegate} suitable for a given {@link Changeable}
	 * @param changeable
	 * 		The {@link Changeable} to be saved/restored
	 * @return
	 * 		A suitable {@link UndoDelegate} for the {@link Changeable}
	 * @throws UndoDelegateSourceException
	 * 		If no suitable {@link UndoDelegate} can be found for the {@link Changeable} 
	 */
	//We can't provide a typed UndoDelegate for an arbitrary Changeable, in any sensible way.
	//So we just return a raw UndoDelegate - users of the source must trust that the delegate
	//is suitable
	@SuppressWarnings("rawtypes")
	public UndoDelegate get(Changeable changeable) throws UndoDelegateSourceException;
	
}
