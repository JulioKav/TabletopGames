package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SHDeceased extends AbstractAction implements IExtendedSequence {
    public final int playerId;


    public SHDeceased(int playerId ) {
        this.playerId = playerId;

    }

//    public SHChancellorSelection getHiddenChoice() {
//        return new SHChancellorSelection(playerId, -1);
//    }

    @Override
    public boolean execute(AbstractGameState gs) {

        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        SHGameState resgs = (SHGameState) state;
        List<AbstractAction> actions = new ArrayList<>();
        actions.add(new SHDeceased(playerId));
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
    public SHDeceased copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHDeceased)) return false;
        SHDeceased that = (SHDeceased) o;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Deceased.";
    }


}
