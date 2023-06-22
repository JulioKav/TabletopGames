package test.games.resistance;

import core.AbstractPlayer;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import core.interfaces.IGamePhase;
import games.GameType;
import games.dicemonastery.DiceMonasteryConstants;
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
import utilities.Utils;

import java.util.*;

import static games.dicemonastery.DiceMonasteryConstants.Phase.USE_MONKS;
import static org.junit.Assert.*;


public class TestResistance {

    Game resistance;
    List<AbstractPlayer> players;
    ResForwardModel fm = new ResForwardModel();
    RandomPlayer rnd = new RandomPlayer();


    private void progressGame(ResGameState state, ResGameState.ResGamePhase requiredGamePhase, int playerTurn) {
            while (state.getGamePhase() != requiredGamePhase && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
            {
                fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            }
            while (state.getCurrentPlayer() != playerTurn && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
            {
                fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            }
    }

    private void progressGameOneRound(ResGameState state) {
        while (state.getGamePhase() != ResGameState.ResGamePhase.MissionVote && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
        while (state.getCurrentPlayer() != state.getNPlayers()-1 && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
    }
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

    @Test
    public void checkingActionsForFirstPhaseTest() {
        ResGameState state = (ResGameState) resistance.getGameState();
        if(state.getGamePhase() == ResGameState.ResGamePhase.LeaderSelectsTeam){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            if(state.getLeaderID() != state.getCurrentPlayer())
            {
                assertEquals(actions.size(),1);
                assertEquals(actions.get(0).getClass(), ResWait.class);
            }

            else{
                int[] players = new int[state.getNPlayers()];
                for (int i = 0; i < state.getNPlayers(); i++) {
                    players[i] = i;
                }
                ArrayList<int[]> choiceOfTeams = Utils.generateCombinations(players, state.gameBoard.getMissionSuccessValues()[state.getRoundCounter()]);
                assertEquals(choiceOfTeams.size(), actions.size());
                for (int i = 0; i < actions.size(); i++) {
                    assertEquals(actions.get(i).getClass(), ResTeamBuilding.class);
                }
            }
        }
    }

    @Test
    public void checkingActionsForSecondPhaseTest() {
        ResGameState state = (ResGameState) resistance.getGameState();
        if(state.getGamePhase() == ResGameState.ResGamePhase.TeamSelectionVote )
        {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            assertEquals(actions.size(),1);
            assertEquals(actions.get(0).getClass(), ResVoting.class);
        }
    }

    @Test
    public void checkingActionsForThirdPhaseTest() {
        ResGameState state = (ResGameState) resistance.getGameState();
        if(state.getGamePhase() == ResGameState.ResGamePhase.MissionVote){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            if(state.getFinalTeam().contains(state.getCurrentPlayer()))
            {
                assertEquals(actions.size(),1);
                assertEquals(actions.get(0).getClass(), ResMissionVoting.class);
            }
            else
            {
                assertEquals(actions.size(),1);
                assertEquals(actions.get(0).getClass(), ResWait.class);
            }
        }
    }
    @Test
    public void checkingPhaseTransitionLeaderToVote()
    {
        ResGameState state = (ResGameState) resistance.getGameState();

        progressGame(state,ResGameState.ResGamePhase.LeaderSelectsTeam, state.getNPlayers()-1);
        IGamePhase previousGamePhase = state.getGamePhase();

        assertEquals(state.getGamePhase(),ResGameState.ResGamePhase.LeaderSelectsTeam);
        assertEquals(state.getNPlayers()-1,state.getCurrentPlayer() );
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        assertNotEquals(previousGamePhase,state.getGamePhase());
        assertEquals(state.getGamePhase(), ResGameState.ResGamePhase.TeamSelectionVote);

    }
@Test
    public void checkingPhaseTransitionVoteToMissionVote()
    {
        ResGameState state = (ResGameState) resistance.getGameState();

        progressGame(state,ResGameState.ResGamePhase.TeamSelectionVote, state.getNPlayers()-1);
        IGamePhase previousGamePhase = state.getGamePhase();

        assertEquals(state.getGamePhase(),ResGameState.ResGamePhase.TeamSelectionVote);
        assertEquals(state.getNPlayers()-1,state.getCurrentPlayer() );
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        assertNotEquals(previousGamePhase,state.getGamePhase());
        if(state.getGamePhase() == ResGameState.ResGamePhase.MissionVote || state.getGamePhase() == ResGameState.ResGamePhase.LeaderSelectsTeam )
        {
            return;
        }
        else
        {
            {throw new AssertionError("Neither condition is met.");}
        }
    }

    @Test
    public void checkingPhaseTransitionMissionVoteToLeader()
    {
        ResGameState state = (ResGameState) resistance.getGameState();


            progressGame(state,ResGameState.ResGamePhase.MissionVote, state.getNPlayers()-1);
            IGamePhase previousGamePhase = state.getGamePhase();

            if(CoreConstants.GameResult.GAME_END != state.getGameStatus()) {
                assertEquals(state.getGamePhase(), ResGameState.ResGamePhase.MissionVote);
                assertEquals(state.getNPlayers() - 1, state.getCurrentPlayer());
                fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

                assertNotEquals(previousGamePhase, state.getGamePhase());
                assertEquals(state.getGamePhase(), ResGameState.ResGamePhase.LeaderSelectsTeam);
            }


    }

    @Test
    public void gameOverWithMissionsCriteriaCheck() {

        ResGameState state = (ResGameState) resistance.getGameState();
        progressGameOneRound(state);
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 1);
            progressGameOneRound(state);
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 2);
            progressGameOneRound(state);
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 3);
        }

        if ( Collections.frequency(state.getGameBoardValues(), true) == 3 || Collections.frequency(state.getGameBoardValues(), false) == 3)
        {
            assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ){progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 4);
            if ( Collections.frequency(state.getGameBoardValues(), true) == 3 || Collections.frequency(state.getGameBoardValues(), false) == 3) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 5);
            if (Collections.frequency(state.getGameBoardValues(), true) == 3 || Collections.frequency(state.getGameBoardValues(), false) == 3) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }
    }
    @Test
    public void gameOverWithFailedVotesCriteriaCheck() {

        ResGameState state = (ResGameState) resistance.getGameState();
        while ( state.getFailedVoteCounter() != 5 && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
        //fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        if(state.getFailedVoteCounter() == 5){

            assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
        }
    }

    @Test
    public void handInitialisationCheck() {

        ResGameState state = (ResGameState) resistance.getGameState();
        for (int i = 0; i < state.getNPlayers(); i++) {
            assertEquals(state.getPlayerHandCards().get(i).getSize(), 3);
            if (state.getPlayerHandCards().get(i).get(2).cardType != ResPlayerCards.CardType.RESISTANCE && state.getPlayerHandCards().get(i).get(2).cardType != ResPlayerCards.CardType.SPY)
            {throw new AssertionError("last card isn't SPY or RESISTANCE");}
            if (state.getPlayerHandCards().get(i).get(0).cardType != ResPlayerCards.CardType.No && state.getPlayerHandCards().get(i).get(0).cardType != ResPlayerCards.CardType.Yes)
            {throw new AssertionError("first card isn't yes or no");}
            if (state.getPlayerHandCards().get(i).get(1).cardType != ResPlayerCards.CardType.Yes && state.getPlayerHandCards().get(i).get(1).cardType != ResPlayerCards.CardType.No)
            {throw new AssertionError("second card isn't yes or no");}
        }

    }
    @Test
    public void checkingCorrectSpyToResistanceRatio()
    {
        ResGameState state = (ResGameState) resistance.getGameState();
        int spyCount = 0;
        int resistanceCount = 0;
        for (int i = 0; i < state.getNPlayers(); i++) {
            assertEquals(state.getPlayerHandCards().get(i).getSize(), 3);
            if (state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.RESISTANCE)
            {resistanceCount += 1;}
            if (state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.SPY)
            {spyCount += 1;}
        }
        assertEquals(resistanceCount, state.factions[0]);
        assertEquals(spyCount, state.factions[1]);
    }

    @Test
    public void checkingWinnersAreCorrect()
    {
        ResGameState state = (ResGameState) resistance.getGameState();
        while (CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            progressGameOneRound(state);
        }

        for (int i = 0; i < state.getNPlayers()-1; i++) {
            if(state.getWinners() == 0)
            {
                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.RESISTANCE)
                {assertEquals( CoreConstants.GameResult.WIN,state.getPlayerResults()[i]);}
                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.SPY)
                {assertEquals(CoreConstants.GameResult.LOSE,state.getPlayerResults()[i] );}
            }

            if(state.getWinners() == 1)
            {
                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.RESISTANCE)
                {assertEquals(CoreConstants.GameResult.LOSE,state.getPlayerResults()[i] );}
                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.SPY)
                {assertEquals( CoreConstants.GameResult.WIN,state.getPlayerResults()[i]);}
            }
        }
    }

    @Test
    public void checkingLeaderMovesAfterFailedTeamVote() {
        ResGameState state = (ResGameState) resistance.getGameState();
        progressGame(state, ResGameState.ResGamePhase.TeamSelectionVote, state.getNPlayers() -1);
        int previousLeader = state.getLeaderID();

        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        if(state.getVoteSuccess() == false) {assertNotEquals( previousLeader,state.getLeaderID());}
        else {assertEquals( previousLeader,state.getLeaderID());}
    }

    @Test
    public void checkingLeaderMovesAfterRoundEnds() {
        ResGameState state = (ResGameState) resistance.getGameState();
        progressGame(state, ResGameState.ResGamePhase.MissionVote, state.getNPlayers() -1);
        int previousLeader = state.getLeaderID();
        int previousRound = state.getRoundCounter();

        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        if(previousRound != state.getRoundCounter()) {assertNotEquals( previousLeader,state.getLeaderID());}
    }

    @Test
    public void checkingSpiesKnowEveryonesCards() {
        ResGameState state = (ResGameState) resistance.getGameState();
        List<ResPlayerCards.CardType> listOfIdentityCards =  new ArrayList<>();
        for (int i = 0; i < state.getNPlayers()-1; i++) {
            listOfIdentityCards.add(state.getPlayerHandCards().get(i).get(2).cardType);
        }

        //Checking Player 0
        checkingSpiesKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 1
        checkingSpiesKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 2
        checkingSpiesKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 3
        checkingSpiesKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 4
        checkingSpiesKnowEveryonesCardsMethod(state,listOfIdentityCards);
    }

    @Test
    public void checkingResistanceDontKnowEveryonesCards() {
        ResGameState state = (ResGameState) resistance.getGameState();
        List<ResPlayerCards.CardType> listOfIdentityCards =  new ArrayList<>();
        for (int i = 0; i < state.getNPlayers()-1; i++) {
            listOfIdentityCards.add(state.getPlayerHandCards().get(i).get(2).cardType);
        }

        //Checking Player 0
        checkingResistanceDontKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 1
        checkingResistanceDontKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 2
        checkingResistanceDontKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 3
        checkingResistanceDontKnowEveryonesCardsMethod(state,listOfIdentityCards);
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        //Checking Player 4
        checkingResistanceDontKnowEveryonesCardsMethod(state,listOfIdentityCards);
    }


private void checkingSpiesKnowEveryonesCardsMethod(ResGameState state, List<ResPlayerCards.CardType> listOfIdentityCards)
{
    if(state.getPlayerHandCards().get(state.getCurrentPlayer()).get(2).cardType == ResPlayerCards.CardType.SPY)
    {
        List<ResPlayerCards.CardType> listOfSpyKnownIdentityCards =  new ArrayList<>();
        for (int j = 0; j < state.getNPlayers()-1; j++) {
            listOfSpyKnownIdentityCards.add(state.getPlayerHandCards().get(j).get(2).cardType);
        }
        assertEquals(listOfSpyKnownIdentityCards,listOfIdentityCards);
    }}

    private void checkingResistanceDontKnowEveryonesCardsMethod(ResGameState state, List<ResPlayerCards.CardType> listOfIdentityCards)
    {
        if (state.getPlayerHandCards().get(state.getCurrentPlayer()).get(2).cardType == ResPlayerCards.CardType.RESISTANCE) {
            List<ResPlayerCards.CardType> listOfSpyKnownIdentityCards = new ArrayList<>();
            for (int j = 0; j < state.getNPlayers() - 1; j++) {
                listOfSpyKnownIdentityCards.add(state.getPlayerHandCards().get(j).get(2).cardType);
            }
            assertNotEquals(listOfSpyKnownIdentityCards, listOfIdentityCards);
        }
    }

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
