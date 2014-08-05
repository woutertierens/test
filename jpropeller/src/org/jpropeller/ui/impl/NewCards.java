package org.jpropeller.ui.impl;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} that doesn't actually view any data - just
 * manages the selection of a card from a card layout via a name
 * in a String Prop
 */
public class NewCards implements JView, ChangeListener {

	private final JPanel panel;
	private final CardLayout layout;
	private final UpdateManager updateManager;
	private String currentlyShowing = "";
	private boolean cardsAddedSinceUpdate = true;
	
	private final Prop<String> selectedCardName;

	/**
	 * Create a {@link NewCards} instance
	 */
	public NewCards(Prop<String> selectedCard) {
		
		//Listen to the selection
		this.selectedCardName = selectedCard;
		this.selectedCardName.features().addListener(this);
		
		layout = new CardLayout();
		panel = new JPanel(layout);
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		//Start out updated
		updateManager.updateRequiredBy(this);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		selectedCardName.features().removeListener(this);
	}

	@Override
	public void update() {
		String card = selectedCardName.get();

		//Synchronize so that cardsAddedSinceUpdate is valid
		synchronized (layout) {
			//If we have new cards, or currently selected card does not 
			//match prop, update selected card (if possible)
			if (cardsAddedSinceUpdate || !currentlyShowing.equals(card)) {
				layout.show(panel, card);
				currentlyShowing = card;
				cardsAddedSinceUpdate = false;
			}
		}
	}
	
	/**
	 * Add a component to a named card.
	 * @param cardName		The name of the card.
	 * @param component		The component for that card.
	 */
	public void addComponent(String cardName, Component component) {
		
		//Synchronize so that cardsAddedSinceUpdate is valid
		synchronized (layout) {
			panel.add(component, cardName);
			cardsAddedSinceUpdate = true;
		}
		
		//Update in case the component for the model's current selectedCard
		//was just added/changed.
		//Since we have added a card, we know the update will definitely show it
		//if required
		updateManager.updateRequiredBy(this);
	}
	
	//Boring implementation
	@Override
	public JPanel getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		//No name is required - so might as well declare as self naming
		return true;
	}

	@Override
	public void cancel() {
		//No editing, so no need to cancel
	}

	@Override
	public void commit() throws CompletionException {
		//No editing, so no need to commit
	}

	@Override
	public boolean isEditing() {
		//No editing
		return false;
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}
}
