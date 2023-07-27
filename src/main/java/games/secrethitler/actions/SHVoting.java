package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;
import games.secrethitler.components.SHPlayerCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SHVoting extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final SHPlayerCards.CardType cardType;

    public SHVoting(int playerId, SHPlayerCards.CardType cardType) {
        this.playerId = playerId;
        this.cardType = cardType;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        ((SHGameState)gs).addCardChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
        SHGameState state1 = (SHGameState) state;
        List<AbstractAction> actions = new ArrayList<>();
        if(!state1.getDeceasedFellas().contains(state.getCurrentPlayer())) {
            actions.add(new SHVoting(state.getCurrentPlayer(), SHPlayerCards.CardType.Yes));
            actions.add(new SHVoting(state.getCurrentPlayer(), SHPlayerCards.CardType.No));
        }
        else{
            actions.add(new SHDeceased(state.getCurrentPlayer()));
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
    public SHVoting copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHVoting)) return false;
        SHVoting that = (SHVoting) o;
        return playerId == that.playerId && cardType == that.cardType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, cardType);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return cardType + ".";
    }
}


