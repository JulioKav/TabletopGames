package games.resistance.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.resistance.ResGameState;
import utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ResTeamBuilding extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    private final int[] team;

    public ResTeamBuilding(int playerId, int[] team) {
        this.playerId = playerId;
        this.team = team;
    }

//    public ResTeamBuilding getHiddenChoice() {
//        return new ResTeamBuilding(playerId, -1);
//    }

    public int[] getTeam() {return team.clone();}
    @Override
    public boolean execute(AbstractGameState gs) {
        ((ResGameState)gs).addTeamChoice(this);;
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        ResGameState resgs = (ResGameState) state;

        List<AbstractAction> actions = new ArrayList<>();

        int[] players = new int[resgs.getNPlayers()];
        for (int i = 0; i < resgs.getNPlayers(); i++) {
            players[i] = i;
        }

          ArrayList<int[]> chosenMarket = Utils.generateCombinations(players, resgs.gameBoard.getMissionSuccessValues()[resgs.getRoundCounter()]);
            for(int[] teams : chosenMarket) {
                actions.add(new ResTeamBuilding(playerId, teams));
            }
        //System.out.println(actions);
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
    public ResTeamBuilding copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResTeamBuilding)) return false;
        ResTeamBuilding that = (ResTeamBuilding) o;
        return playerId == that.playerId && Arrays.equals(team, that.team);
    }

    @Override
    public int hashCode() {
        return playerId + Arrays.hashCode(team);
    }

    @Override
    public String getString(AbstractGameState gameState) {

        return "Action Chosen Team :  " + Arrays.toString(team) + "|  PlayerID : " + playerId;
    }



}
