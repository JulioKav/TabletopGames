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

        SHGameState resgs = (SHGameState) state;

        List<AbstractAction> actions = new ArrayList<>();


            int[] players = new int[resgs.getNPlayers()];
            for (int i = 0; i < resgs.getNPlayers(); i++) {
                players[i] = i;
            }
            ArrayList<int[]> choiceOfTeams = Utils.generateCombinations(players, resgs.gameBoard.getMissionSuccessValues()[resgs.getRoundCounter()]);
            for(int[] team : choiceOfTeams) {
                actions.add(new SHChancellorSelection(playerId, chancellorID));
                if (team.length == 0){throw new AssertionError("Team Size Zero");}
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
