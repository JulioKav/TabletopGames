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

public class ResMissionVoting extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final int cardIdx;

    public ResMissionVoting(int playerId, int cardIdx) {
        this.playerId = playerId;
        this.cardIdx = cardIdx;
    }

    public ResMissionVoting getHiddenChoice(ResGameState resgs) {
        if (resgs.getPlayerHandCards().get(playerId).getSize() > 3)
        {return new ResMissionVoting(playerId, 1);}
        else{return new ResMissionVoting(playerId, 0);}
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        ((ResGameState)gs).addMissionChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        ResGameState resgs = (ResGameState) state;
        int idxSelected = resgs.getvotingChoice().get(playerId).get(0).cardIdx;
        List<AbstractAction> actions = new ArrayList<>();

        PartialObservableDeck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(playerId);
        for (int i = 0; i < currentPlayerHand.getSize(); i++) {
            if (idxSelected != i) {
                actions.add(new ResMissionVoting(playerId, i));
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
    public ResMissionVoting copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResMissionVoting)) return false;
        ResMissionVoting that = (ResMissionVoting) o;
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
