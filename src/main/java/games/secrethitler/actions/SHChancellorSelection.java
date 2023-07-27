package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;
import utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SHChancellorSelection extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final int chancellorID;

    public SHChancellorSelection(int playerId, int chancellorID) {
        this.playerId = playerId;
        this.chancellorID = chancellorID;
    }

    public int getChancellorID() {return chancellorID;}
    @Override
    public boolean execute(AbstractGameState gs) {
        ((SHGameState)gs).addChancellorChoice(this);
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        SHGameState shgs = (SHGameState) state;

        List<AbstractAction> actions = new ArrayList<>();
        if (state.getCurrentPlayer() == shgs.getLeaderID()) {
            for (int i = 0; i < shgs.getNPlayers(); i++) {
                if (i != shgs.getPreviousLeader() && i != shgs.getPreviousChancellor() && i != shgs.getLeaderID() && !shgs.getDeceasedFellas().contains(i)) {
                    actions.add(new SHChancellorSelection(state.getCurrentPlayer(), i));
                }
            }
            if(actions.size() == 0)
            {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    if ( i != shgs.getLeaderID() && !shgs.getDeceasedFellas().contains(i)) {
                        actions.add(new SHChancellorSelection(state.getCurrentPlayer(), i));
                    }
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
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {}

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return false;
    }

    @Override
    public SHChancellorSelection copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHChancellorSelection)) return false;
        SHChancellorSelection that = (SHChancellorSelection) o;
        return playerId == that.playerId && chancellorID == that.chancellorID;
    }

    @Override
    public int hashCode() {
        return playerId + chancellorID;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Leader Has Suggested Chancellor :  " + chancellorID;
    }



}
