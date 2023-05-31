package test.games.resistance;

import core.AbstractPlayer;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import games.GameType;
import games.resistance.components.ResPlayerCards;
import games.resistance.components.ResGameBoard;
import games.resistance.ResGameState;
import games.resistance.ResForwardModel;
import games.resistance.ResParameters;
import games.resistance.actions.ResMissionVoting;
import games.resistance.actions.ResVoting;
import games.resistance.actions.ResAction;
import games.resistance.actions.ResTeamBuilding;
import games.resistance.actions.ResWait;
import org.junit.Before;
import org.junit.Test;
import players.simple.RandomPlayer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestResistance {

    Game resistance;
    List<AbstractPlayer> players;
    ResForwardModel fm = new ResForwardModel();

    @Before
    public void setup() {
        players = Arrays.asList(new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer());
        resistance = GameType.Resistance.createGameInstance(5, 34, new ResParameters(-274));
        resistance.reset(players);
    }

//    @Test
//    public void testPassMovesToNextPlayerDirectly() {
//        assertEquals(CantStopGamePhase.Decision, cantStop.getGameState().getGamePhase());
//        assertEquals(0, cantStop.getGameState().getCurrentPlayer());
//        List<AbstractAction> actions = fm.computeAvailableActions(cantStop.getGameState());
//        assertEquals(2, actions.size());
//        assertEquals(new Pass(false), actions.get(0));
//        assertEquals(new RollDice(), actions.get(1));
//
//        fm.next(cantStop.getGameState(), new Pass(false));
//        assertEquals(CantStopGamePhase.Decision, cantStop.getGameState().getGamePhase());
//        assertEquals(1, cantStop.getGameState().getCurrentPlayer());
//    }
//
//    @Test
//    public void testTeamVoteNumber() {
//        ResGameState state = (ResGameState) resistance.getGameState();
//        fm.next(state, new RollDice());
//        do {
//            fm.next(state, fm.computeAvailableActions(state).get(0));
//            fm.next(state, new RollDice());
//            // we keep rolling dice until we go bust
//        } while (!fm.computeAvailableActions(state).get(0).equals(new Pass(true)));
//        fm.next(state, new Pass(true));
//        assertEquals(CantStopGamePhase.Decision, state.getGamePhase());
//    }


}
