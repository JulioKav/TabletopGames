package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Deck;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;
import games.secrethitler.components.SHPolicyCards;
import org.sparkproject.guava.hash.HashCode;
import utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SHLeaderPeeks extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final List<SHPolicyCards> cardsPeeked;

    public SHLeaderPeeks(int playerId, List<SHPolicyCards> cardsPeeked) {
        this.playerId = playerId;
        this.cardsPeeked = cardsPeeked;
    }

    public List<SHPolicyCards> getcardsPeeked() {return cardsPeeked;}
    @Override
    public boolean execute(AbstractGameState gs) {
        ((SHGameState)gs).addPeekedCards(this);
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        SHGameState shgs = (SHGameState) state;

        List<AbstractAction> actions = new ArrayList<>();
        if (shgs.getCurrentPlayer() == shgs.getLeaderID()) {
            for (int i = 0; i < shgs.getNPlayers(); i++) {
                actions.add(new SHLeaderPeeks(shgs.getCurrentPlayer(), shgs.getDrawPile().peek(0,3)));
            }
        }

        return actions;
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return playerId;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {}

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return false;
    }

    @Override
    public SHLeaderPeeks copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHLeaderPeeks)) return false;
        SHLeaderPeeks that = (SHLeaderPeeks) o;
        return playerId == that.playerId && cardsPeeked == that.cardsPeeked;
    }

    @Override
    public int hashCode() {
        return playerId + Objects.hash(cardsPeeked);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Leader Has Suggested Chancellor :  " + cardsPeeked;
    }



}
