package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;
import utilities.Utils;

import java.util.ArrayList;
import java.util.List;


public class SHInvestigateIdentity extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final int investigatingID;

    public SHInvestigateIdentity(int playerId, int investigatingID) {
        this.playerId = playerId;
        this.investigatingID = investigatingID;
    }

    public int getinvestigatingID() {return investigatingID;}
    @Override
    public boolean execute(AbstractGameState gs) {
        ((SHGameState)gs).addInvestigatingChoice(this,(SHGameState) gs);
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
                actions.add(new SHInvestigateIdentity(playerId, investigatingID));
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
    public SHInvestigateIdentity copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHInvestigateIdentity)) return false;
        SHInvestigateIdentity that = (SHInvestigateIdentity) o;
        return playerId == that.playerId && investigatingID == that.investigatingID;
    }

    @Override
    public int hashCode() {
        return playerId + investigatingID;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Leader Has Chosen To Investigate :  " + investigatingID;
    }



}
