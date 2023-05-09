package games.resistance.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.PartialObservableDeck;
import core.interfaces.IExtendedSequence;
import games.resistance.ResGameState;
import games.resistance.components.ResPlayerCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResVoting extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final int cardIdx;

    public ResVoting(int playerId, int cardIdx) {
        this.playerId = playerId;
        this.cardIdx = cardIdx;
    }

    public ResVoting getHiddenChoice() {
        return new ResVoting(playerId, -1);
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        ((ResGameState)gs).addCardChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
        // Chopsticks allowing to pick second card, different from that already selected
        ResGameState resgs = (ResGameState) state;
        int idxSelected = resgs.getvotingChoice().get(playerId).get(0).cardIdx;
        List<AbstractAction> actions = new ArrayList<>();

        PartialObservableDeck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(playerId);
        for (int i = 0; i < currentPlayerHand.getSize(); i++) {
            // All players can do is choose a card in hand to play. Cannot chain chopsticks, only 1 per turn can be used.
            // So all of these actions can only be 'useChopsticks = false'
            if (idxSelected != i) {
                actions.add(new ResVoting(playerId, i));
            }
        }
        return actions;
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return playerId;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {

    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return false;
    }

    @Override
    public ResVoting copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResVoting)) return false;
        ResVoting that = (ResVoting) o;
        return playerId == that.playerId && cardIdx == that.cardIdx;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, cardIdx);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Choose card " + cardIdx;
    }
}
