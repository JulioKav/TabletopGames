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


    /////////// MIGHT BE DUMB RANDOMLY CHOOSING WITH HARDCODEd HAND
    public ResVoting getHiddenChoice(ResGameState resgs, int i) {
        if (resgs.getPlayerHandCards().get(i).getSize() > 3)
        {

            return new ResVoting(i, 1);}
        else{return new ResVoting(i, 0);}
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        ((ResGameState)gs).addCardChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
        List<AbstractAction> actions = new ArrayList<>();
        ResGameState resgs = (ResGameState) state;
        int idxSelected = resgs.getvotingChoice().get(playerId).get(0).cardIdx;


        PartialObservableDeck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(playerId);
        for (int i = 0; i < currentPlayerHand.getSize(); i++) {
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


