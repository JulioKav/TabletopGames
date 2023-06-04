package games.resistance;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Deck;
import core.components.PartialObservableDeck;
import core.interfaces.IGamePhase;
import games.resistance.actions.*;
import games.resistance.components.ResPlayerCards;
import utilities.Utils;
import java.util.*;
import static games.resistance.ResGameState.ResGamePhase.*;


/**
 * <p>The forward model contains all the game rules and logic. It is mainly responsible for declaring rules for:</p>
 * <ol>
 *     <li>Game setup</li>
 *     <li>Actions available to players in a given game state</li>
 *     <li>Game events or rules applied after a player's action</li>
 *     <li>Game end</li>
 * </ol>
 */
public class ResForwardModel extends StandardForwardModel {
    public int counter = 0;


    /**
     * Initializes all variables in the given game state. Performs initial game setup according to game rules, e.g.:
     * <ul>
     *     <li>Sets up decks of cards and shuffles them</li>
     *     <li>Gives player cards</li>
     *     <li>Places tokens on boards</li>
     *     <li>...</li>
     * </ul>
     *
     * @param firstState - the state to be modified to the initial game state.
     */
    @Override
    protected void _setup(AbstractGameState firstState) {
        // TODO: perform initialization of variables and game setup
        Random rnd = new Random(firstState.getGameParameters().getRandomSeed());
        ResGameState resgs = (ResGameState)firstState;
        ResParameters resp = (ResParameters)firstState.getGameParameters();
        System.out.println(firstState.getNPlayers() +"fjrststate");
        resgs.votingChoice = new ArrayList<>(firstState.getNPlayers());
        resgs.missionVotingChoice = new ArrayList<>(firstState.getNPlayers());
        resgs.gameBoardValues = new ArrayList<>(5);
        resgs.failedVoteCounter = 0;
        resgs.playerHandCards = new ArrayList<>(firstState.getNPlayers());
        //could be wrong
        resgs.gameBoard = resp.getPlayerBoard(firstState.getNPlayers());
        if(resgs.gameBoard == null)
        {throw new AssertionError("GameBoard shouldn't be null");};
        resgs.factions = resp.getFactions(firstState.getNPlayers());


        //resgs.finalTeamChoice = new int[20];
        
        

        //List<PartialObservableDeck<ResPlayerCards>> playerHandCards = new ArrayList<>(firstState.getNPlayers());
        int spyCounter = 0;
        for (int i = 0; i < firstState.getNPlayers(); i++) {
            resgs.votingChoice.add(new ArrayList<>());
            resgs.missionVotingChoice.add(new ArrayList<>());
            boolean[] visible = new boolean[firstState.getNPlayers()];
            visible[i] = true;
            //might see sabotage cards
            PartialObservableDeck<ResPlayerCards> playerCards = new PartialObservableDeck<>("Player Cards", visible);
            resgs.playerHandCards.add(playerCards);

            // Add identity cards to hand
            if (rnd.nextInt(2) == 0 && spyCounter != resgs.factions[1]) {
                ResPlayerCards SPY = new ResPlayerCards(ResPlayerCards.CardType.SPY);
                SPY.setOwnerId(i);
                playerCards.add(SPY);
                spyCounter += 1;
            }
            else
            {
                ResPlayerCards resistor = new ResPlayerCards(ResPlayerCards.CardType.RESISTANCE);
                resistor.setOwnerId(i);
                playerCards.add(resistor);
            }
            //Add Voting Cards in random order, done so when getting hidden choice at index 0, will be random vote
            if(rnd.nextInt(2) == 0) {
                ResPlayerCards yes = new ResPlayerCards(ResPlayerCards.CardType.Yes);
                yes.setOwnerId(i);
                playerCards.add(yes);

                ResPlayerCards no = new ResPlayerCards(ResPlayerCards.CardType.No);
                no.setOwnerId(i);
                playerCards.add(no);
            }
            else{ResPlayerCards no = new ResPlayerCards(ResPlayerCards.CardType.No);

                no.setOwnerId(i);
                playerCards.add(no);

                ResPlayerCards yes = new ResPlayerCards(ResPlayerCards.CardType.Yes);
                yes.setOwnerId(i);
                playerCards.add(yes);
            }
        }

        //Adding leader card
        ResPlayerCards leader = new ResPlayerCards(ResPlayerCards.CardType.LEADER);
        int randomPlayerLeader = rnd.nextInt(resgs.getNPlayers());
        leader.setOwnerId(randomPlayerLeader);
        resgs.playerHandCards.get(randomPlayerLeader).add(leader);
        resgs.setGamePhase(LeaderSelectsTeam);
    }

    /**
     * Calculates the list of currently available actions, possibly depending on the game phase.
     * @return - List of AbstractAction objects.
     */
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {

        ResGameState resgs = (ResGameState) gameState;

        List<AbstractAction> actions = new ArrayList<>();
        int currentPlayer = resgs.getCurrentPlayer();

        Deck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(currentPlayer);

        if (resgs.getGamePhase() == LeaderSelectsTeam) {
            //Leader Creates Team
            if (currentPlayerHand.get(0).cardType == ResPlayerCards.CardType.LEADER) {
                int[] players = new int[resgs.getNPlayers()];
                for (int i = 0; i < resgs.getNPlayers(); i++) {
                    players[i] = i;
                }
                ArrayList<int[]> choiceOfTeams = Utils.generateCombinations(players, resgs.gameBoard.getMissionSuccessValues()[resgs.getRoundCounter()]);
                for(int[] team : choiceOfTeams) {
                    actions.add(new ResTeamBuilding(currentPlayer, team));
                }
            }
            //Every Other Player Waits
            else{
                actions.add(new ResWait(currentPlayer));
            }
        }


            if(resgs.getGamePhase()== TeamSelectionVote) {
                // All players can do is choose a yes or no card in hand to play.
                actions.add(new ResVoting(currentPlayer, currentPlayerHand.getSize() - 3));
                //System.out.println("ADDING VOTE ACTION : " + currentPlayerHand.get(currentPlayerHand.getSize() - 3));
                actions.add(new ResVoting(currentPlayer, currentPlayerHand.getSize() - 2));
                //System.out.println("ADDING VOTE ACTION : " + currentPlayerHand.get(currentPlayerHand.getSize() - 2));
            }

        if(resgs.getGamePhase()== MissionVote) {
            if(resgs.finalTeamChoice.contains(currentPlayer)){

                actions.add(new ResMissionVoting(currentPlayer, currentPlayerHand.getSize() - 3));
                actions.add(new ResMissionVoting(currentPlayer, currentPlayerHand.getSize() - 2));
            }
            else {
                actions.add(new ResWait(currentPlayer));
            }
        }

            //System.out.println(actions.get(0).toString());
            return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        ResGameState resgs = (ResGameState) currentState;
        System.out.println("Current Round : " + resgs.getRoundCounter());

        //Leader Selects Team
        if (resgs.getGamePhase() == LeaderSelectsTeam) {
            int turn = resgs.getTurnCounter();
            if ((turn + 1) % (resgs.getNPlayers()) == 0) {
                revealCards(resgs);
                resgs.previousGamePhase = resgs.getGamePhase();
                resgs.setGamePhase(TeamSelectionVote);

            }
        }

        if (resgs.getGamePhase() == TeamSelectionVote) {
            int turn = resgs.getTurnCounter();
            ///// POTENTIAL ISSUE WITH TURN COUNTER IF VOTE REJECTED
            if ((turn+1) % resgs.getNPlayers() == 0 && resgs.previousGamePhase == resgs.getGamePhase()) {
            revealCards(resgs);
            if(resgs.failedVoteCounter == 5){
                for (int i = 0; i < resgs.getNPlayers(); i++) {
                    PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                    if(hand.get(0).cardType == ResPlayerCards.CardType.SPY || hand.get(1).cardType == ResPlayerCards.CardType.SPY) {
                        resgs.setPlayerResult(CoreConstants.GameResult.WIN,i);}
                    else{resgs.setPlayerResult(CoreConstants.GameResult.LOSE,i);}}
                endGame(resgs);
                System.out.println("GAME ENDED BY FAILED TEAMVOTE IN AFTERACTION");
            }

            if(resgs.voteSuccess == true){resgs.setGamePhase(MissionVote);}
            else{
                resgs.clearCardChoices();
                resgs.clearTeamChoices();
                // CHANGE LEADER
                changeLeader(resgs);
                resgs.setGamePhase(LeaderSelectsTeam);
            }}

            else{resgs.previousGamePhase = resgs.getGamePhase();}
        }

        if (resgs.getGamePhase() == MissionVote){
            int turn = resgs.getTurnCounter();

            if ((turn + 1) % resgs.getNPlayers() == 0 && resgs.previousGamePhase == resgs.getGamePhase()) {

                revealCards(resgs);


//                // Check if the round is over
//                if (isRoundOver(resgs)) {
                // It is! Process end of round rules.
                changeLeader(resgs);
                endRound(resgs);
                _endRound(resgs);

                // Clear card choices from this turn, ready for the next simultaneous choice.
                resgs.clearCardChoices();
                resgs.clearMissionChoices();
                resgs.clearTeamChoices();


                // Check if the game is over
                resgs.occurrenceCountTrue = Collections.frequency(resgs.gameBoardValues, true);
                resgs.occurrenceCountFalse = Collections.frequency(resgs.gameBoardValues, false);
                System.out.println("Occurrence True : " + resgs.occurrenceCountTrue);
                System.out.println("Occurrence False : " + resgs.occurrenceCountFalse);
                if (resgs.occurrenceCountTrue == 3) {
                    // Decide winner
                    for (int i = 0; i < resgs.getNPlayers(); i++) {
                        PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                        if (hand.get(0).cardType == ResPlayerCards.CardType.RESISTANCE || hand.get(1).cardType == ResPlayerCards.CardType.RESISTANCE) {
                            resgs.setPlayerResult(CoreConstants.GameResult.WIN, i);
                        } else {
                            resgs.setPlayerResult(CoreConstants.GameResult.LOSE, i);
                        }
                    }
                    endGame(resgs);
                    if(resgs.occurrenceCountTrue == 3){ System.out.println("GAME ENDED BY SUCCESSFUL MISSIONS");}
                    ////MAYBE GET RID OF RETURNS
                    return;
                }

                if (resgs.occurrenceCountFalse == 3 || resgs.failedVoteCounter == 5) {
                    // Decide winner
                    for (int i = 0; i < resgs.getNPlayers(); i++) {
                        PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                        if (hand.get(0).cardType == ResPlayerCards.CardType.SPY || hand.get(1).cardType == ResPlayerCards.CardType.SPY) {
                            resgs.setPlayerResult(CoreConstants.GameResult.WIN, i);
                        } else {
                            resgs.setPlayerResult(CoreConstants.GameResult.LOSE, i);
                        }
                    }
                    endGame(resgs);
                    if(resgs.failedVoteCounter == 5){ System.out.println("GAME ENDED BY FAILED VOTE");}
                    if(resgs.occurrenceCountFalse == 3){ System.out.println("GAME ENDED BY FAILED MISSIONS");}
                    return;

                }
                resgs.failedVoteCounter = 0;
                resgs.setGamePhase(LeaderSelectsTeam);
                _startRound(resgs);


            //}
//                else {
                    // Clear card choices
                    resgs.clearCardChoices();
                    resgs.clearMissionChoices();
                    resgs.clearTeamChoices();
                //}
            }
            resgs.previousGamePhase = resgs.getGamePhase();}
        //End player turn
        if (resgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
            endPlayerTurn(resgs);
        }
    }

    void revealCards(ResGameState resgs) {
        if (resgs.getGamePhase() == TeamSelectionVote){
            ArrayList<ResPlayerCards.CardType> allVotes = new ArrayList<>();
        for (int i = 0; i < resgs.getNPlayers(); i++) {
            PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
            for (ResVoting cc: resgs.votingChoice.get(i)) {
                ResPlayerCards cardToReveal = hand.get(cc.cardIdx);
                allVotes.add(cardToReveal.cardType);
            }
            //System.out.println("player :"+ i + "  |card");
        }
        // returns if the vote successful
            int occurrenceCount = Collections.frequency(allVotes, ResPlayerCards.CardType.Yes);
            System.out.println("Team Vote :  " + allVotes);
            System.out.println("Team Yes Occurrence Count : " + occurrenceCount  );
            if (occurrenceCount > allVotes.size()/2){resgs.voteSuccess = true;}
            /// MOVED failedVoteCounter to RESGS COULD BE WRONG
            else{resgs.voteSuccess = false; resgs.failedVoteCounter += 1;}
        }

        if (resgs.getGamePhase() == LeaderSelectsTeam){
            for (int i = 0; i < resgs.getNPlayers(); i++) {

                PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                    if(hand.get(0).cardType == ResPlayerCards.CardType.LEADER) {
                        for (int[] cc : resgs.teamChoice) {
                            //System.out.println("team choice check: " + resgs.teamChoice);
//                            ArrayList<ResTeamBuilding> intList = new ArrayList<ResTeamBuilding>(cc.team.length);
//                            intList.add(cc);
//                            System.out.println(intList);
                            resgs.finalTeamChoice = new ArrayList<>();
                            for (int member : cc){resgs.finalTeamChoice.add(member);}


                            //System.out.println(cc.team.length + " cc team size");
                            //System.out.println(resgs.finalTeamChoice.length + " final team size");
                        }
                        //System.out.println(allActions.get(0));
                        System.out.println("Final Team :  " + resgs.finalTeamChoice);
                    }

            }



        }
        //System.out.println(allVotes);

        //////////////////////////////////////////

        /////////////////////////////////////////

        // MISSION CARDS ARE NOT HIDDEN
        /////////////////////////////

        if (resgs.getGamePhase() == MissionVote){
            ArrayList<ResPlayerCards.CardType> allVotes = new ArrayList<>();
            for (int i = 0; i < resgs.getNPlayers(); i++) {
                    PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);

                    for (ResMissionVoting cc : resgs.missionVotingChoice.get(i)) {
                        ResPlayerCards cardToReveal = hand.get(cc.cardIdx);
                        allVotes.add(cardToReveal.cardType);
                    }

            }


            System.out.println("Mission Vote :  " + allVotes);
            //System.out.println(allVotes.get(0) == ResPlayerCards.CardType.No);
            int occurrenceCount = Collections.frequency(allVotes, ResPlayerCards.CardType.No);
            System.out.println("Mission No Occurrence Count : " + occurrenceCount  );
            if (occurrenceCount > 0){resgs.gameBoardValues.add(false);}
            else{resgs.gameBoardValues.add(true);}

        }

    }

    public void _startRound(ResGameState resgs) {

    }

    boolean isRoundOver(ResGameState resgs) {
        //some logic

        return true;
    }
    public void _endRound(ResGameState resgs) {
        // Apply card end of round rules
//        for (ResPlayerCards.CardType type: ResPlayerCards.CardType.values()) {
//            type.onRoundEnd(resgs);
//        }
        resgs.setGamePhase(LeaderSelectsTeam);
        //resgs.teamChoice.clear();


    }

    public void changeLeader(ResGameState resgs) {
        for (int i = 0; i < resgs.getNPlayers(); i++){

            Deck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(i);
            if (currentPlayerHand.get(0).cardType == ResPlayerCards.CardType.LEADER)
            {

                if(i + 1 == resgs.getNPlayers()) {
                    currentPlayerHand.remove(0);
                    ResPlayerCards leader = new ResPlayerCards(ResPlayerCards.CardType.LEADER);
                    leader.setOwnerId(0);
                    resgs.getPlayerHandCards().get(0).add(leader);

                    break;
                }
                else{
                    currentPlayerHand.remove(0);
                    ResPlayerCards leader = new ResPlayerCards(ResPlayerCards.CardType.LEADER);
                    leader.setOwnerId(i+1);
                    resgs.getPlayerHandCards().get(i+1).add(leader);
                    break;
                }
            }

        }
    }
}
