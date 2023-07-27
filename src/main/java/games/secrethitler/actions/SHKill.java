package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SHKill extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final int victim;


    public SHKill(int playerId, int victim ) {
        this.playerId = playerId;
        this.victim = victim;

    }


    @Override
    public boolean execute(AbstractGameState gs) {
        ((SHGameState)gs).addKillChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        SHGameState shgs = (SHGameState) state;

        List<AbstractAction> actions = new ArrayList<>();
        if (shgs.getCurrentPlayer() == shgs.getLeaderID()) {
            for (int i = 0; i < shgs.getNPlayers(); i++) {
                if (i != shgs.getPreviousLeader() && i != shgs.getPreviousChancellor() && i != shgs.getLeaderID() && !shgs.getDeceasedFellas().contains(i)) {
                    actions.add(new SHKill(shgs.getCurrentPlayer(), i));
                }
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
    public SHKill copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHKill)) return false;
        SHKill that = (SHKill) o;
        return playerId == that.playerId && victim == that.victim;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId,victim);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "kills " + victim;
    }


}
