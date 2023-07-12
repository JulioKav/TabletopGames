package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;
import utilities.Utils;

import java.util.ArrayList;
import java.util.List;


public class SHLeaderSelectsLeader extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final int chosenLeaderID;

    public SHLeaderSelectsLeader(int playerId, int chosenLeaderID) {
        this.playerId = playerId;
        this.chosenLeaderID = chosenLeaderID;
    }

    public int getchosenLeaderID() {return chosenLeaderID;}
    @Override
    public boolean execute(AbstractGameState gs) {
        ((SHGameState)gs).addChosenLeaderChoice(this);
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
                actions.add(new SHLeaderSelectsLeader(playerId, chosenLeaderID));
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
    public SHLeaderSelectsLeader copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHLeaderSelectsLeader)) return false;
        SHLeaderSelectsLeader that = (SHLeaderSelectsLeader) o;
        return playerId == that.playerId && chosenLeaderID == that.chosenLeaderID;
    }

    @Override
    public int hashCode() {
        return playerId + chosenLeaderID;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Leader Has Suggested Chancellor :  " + chosenLeaderID;
    }



}
