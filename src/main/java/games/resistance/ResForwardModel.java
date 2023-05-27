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
import games.sushigo.SGGameState;
import games.sushigo.SGParameters;
import games.sushigo.actions.ChooseCard;
import games.sushigo.cards.SGCard;
import utilities.Utils;

import java.util.*;

import static core.CoreConstants.ComponentType.CARD;
import static games.resistance.ResGameState.ResGamePhase.*;
import static games.sushigo.cards.SGCard.SGCardType.values;

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
    public int[] factions;
    IGamePhase previousGamePhase = null;
    int failedVoteCounter = 0;
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
        // DOUBLE CHECK LAST PLAYER CAN GET LEADER

        //System.out.println(resgs.playerHandCards.get(0));
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


        //System.out.println(resgs.getGamePhase());
        int currentPlayer = resgs.getCurrentPlayer();

        Deck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(currentPlayer);
        //System.out.println(currentPlayerHand);
        //LEADER TEAM SELECTION
        //System.out.println(currentPlayerHand.getComponents().get(0).cardType);
        if (resgs.getGamePhase() == LeaderSelectsTeam) {
            //System.out.println(currentPlayerHand);
            if (currentPlayerHand.get(0).cardType == ResPlayerCards.CardType.LEADER) {
                //ALSO REPEATED IN RESTEAMBUILDING SO MIGHT BE MISTAKE
                int[] players = new int[resgs.getNPlayers()];
                for (int i = 0; i < resgs.getNPlayers(); i++) {
                    players[i] = i;

                }

                //System.out.println(resgs.gameBoard.getMissionSuccessValues()[resgs.getRoundCounter()]);
                ArrayList<int[]> choiceOfTeams = Utils.generateCombinations(players, resgs.gameBoard.getMissionSuccessValues()[resgs.getRoundCounter()]);

                for(int[] team : choiceOfTeams) {
                    //System.out.println(Arrays.toString(teams));

                    actions.add(new ResTeamBuilding(currentPlayer, team));


                }

            }
            else{

                actions.add(new ResWait(currentPlayer));
            }
        }

            if(resgs.getGamePhase()== TeamSelectionVote) {
                // All players can do is choose a yes or no card in hand to play.
                //System.out.println(currentPlayer + "currentplayer");
                //System.out.println( "consdsdsdd not met");
                actions.add(new ResVoting(currentPlayer, currentPlayerHand.getSize() - 3));
                actions.add(new ResVoting(currentPlayer, currentPlayerHand.getSize() - 2));
                //System.out.println(actions.size());
            }

        if(resgs.getGamePhase()== MissionVote) {

            //System.out.println("in here");
            // All players can do is choose a yes or no card in hand to play.

            ArrayList<Integer> teamList = new ArrayList<>();
            for (int value : resgs.finalTeamChoice) {
                teamList.add(value);

                //System.out.println(value + "value");
                //System.out.println(teamList );
            }
           // System.out.println(currentPlayer);
            if(teamList.contains(currentPlayer)){
                //System.out.println(teamList + "cond met");
                actions.add(new ResMissionVoting(currentPlayer, currentPlayerHand.getSize() - 3));
                actions.add(new ResMissionVoting(currentPlayer, currentPlayerHand.getSize() - 2));
            }
            else {
                //System.out.println(teamList + "cond not met");
                actions.add(new ResWait(currentPlayer));
            }


            //System.out.println(actions.size());
        }
            //System.out.println(actions.get(0));

            System.out.println(actions.get(0).toString());

            return actions;


    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        if (currentState.isActionInProgress())
            return; // we only want to trigger this processing if an extended action sequence has been terminated
        ResGameState resgs = (ResGameState) currentState;


        //NEED TO CHOOSE TEAM
        if (resgs.getGamePhase() == LeaderSelectsTeam) {

            int turn = resgs.getTurnCounter();


            if ((turn + 1) % (resgs.getNPlayers()) == 0) {

                revealCards(resgs);
                previousGamePhase = resgs.getGamePhase();
                resgs.setGamePhase(TeamSelectionVote);

            }


        }


        if (resgs.getGamePhase() == TeamSelectionVote) {
            // Check if all players made their choice

            int turn = resgs.getTurnCounter();



            ///// POTENTIAL ISSUE WITH TURN COUNTER IF VOTE REJECTED
            if ((turn+1) % resgs.getNPlayers() == 0 && previousGamePhase == resgs.getGamePhase()) {
           // They did! Reveal all cards at once. Process card reveal rules.
                // System.out.println("player" + turn);

            revealCards(resgs);


 //           resgs.setGamePhase(MissionVote);
            if(resgs.voteSuccess){resgs.setGamePhase(MissionVote);}
            else{
                resgs.clearCardChoices();
                resgs.clearTeamChoices();
                // CHANGE LEADER
                changeLeader(resgs);
                resgs.setGamePhase(LeaderSelectsTeam);
            }


            }
            else{previousGamePhase = resgs.getGamePhase();}


//            else {
//                // Clear card choices from this turn, ready for the next simultaneous choice.
//                resgs.clearCardChoices();
//                // Round is not over, keep going. Rotate hands for next player turns.
//                //rotatePlayerHands(resgs);
//
//
//            }


        // End player turn
//        if (resgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
//            endPlayerTurn(resgs);
//        }
        }



        if (resgs.getGamePhase() == MissionVote){
            // Check if all players made their choice
            int turn = resgs.getTurnCounter();
            //System.out.println("Previous GamePhase :" + previousGamePhase + "  Current GamePhase : " + resgs.getGamePhase() );
            if ((turn + 1) % resgs.getNPlayers() == 0 && previousGamePhase == resgs.getGamePhase()) {
                // They did! Reveal all cards at once. Process card reveal rules.

                revealCards(resgs);




                // Check if the round is over
                if (isRoundOver(resgs)) {
                    // It is! Process end of round rules.
                    changeLeader(resgs);
                    endRound(resgs);
                    _endRound(resgs);

                    // Clear card choices from this turn, ready for the next simultaneous choice.
                    resgs.clearCardChoices();
                    resgs.clearMissionChoices();
                    resgs.clearTeamChoices();


                    // Check if the game is over
                    int occurrenceCountTrue = Collections.frequency(resgs.gameBoardValues, true);
                    int occurrenceCountFalse = Collections.frequency(resgs.gameBoardValues, false);
                    if (occurrenceCountTrue == 3) {
                        // Decide winner
                        for (int i = 0; i < resgs.getNPlayers(); i++) {
                            PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                            if(hand.get(0).cardType == ResPlayerCards.CardType.RESISTANCE || hand.get(1).cardType == ResPlayerCards.CardType.RESISTANCE) {
                            resgs.setPlayerResult(CoreConstants.GameResult.WIN,i);}
                            else{resgs.setPlayerResult(CoreConstants.GameResult.LOSE,i);}}
                        endGame(resgs);
                        return;
                    }

                    if ( occurrenceCountFalse == 3 || failedVoteCounter == 5) {
                        // Decide winner
                        for (int i = 0; i < resgs.getNPlayers(); i++) {
                            PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                            if(hand.get(0).cardType == ResPlayerCards.CardType.SPY || hand.get(1).cardType == ResPlayerCards.CardType.SPY) {
                                resgs.setPlayerResult(CoreConstants.GameResult.WIN,i);}
                            else{resgs.setPlayerResult(CoreConstants.GameResult.LOSE,i);}}
                        endGame(resgs);
                        System.out.println("GAME ENDED");
                        return;
                    }
                    failedVoteCounter = 0;
                    _startRound(resgs);
                    resgs.setGamePhase(LeaderSelectsTeam);
                    return;
                }
                else {
                    // Clear card choices
                    resgs.clearCardChoices();
                    resgs.clearMissionChoices();
                    resgs.clearTeamChoices();
                }
            }
            previousGamePhase = resgs.getGamePhase();}
        //End player turn
        if (resgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
            endPlayerTurn(resgs);
        }
    }

    void revealCards(ResGameState resgs) {
        if (resgs.getGamePhase() == TeamSelectionVote){ArrayList<ResPlayerCards.CardType> allVotes = new ArrayList<>();

        for (int i = 0; i < resgs.getNPlayers(); i++) {
            PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
            for (ResVoting cc: resgs.votingChoice.get(i)) {
//                System.out.println(hand.get(0) + "hand MCTS");
//                System.out.println(hand.get(1) + "hand MCTS");
//                System.out.println(hand.get(2) + "hand MCTS");
//                System.out.println(cc.cardIdx + "CC");


                ResPlayerCards cardToReveal = hand.get(cc.cardIdx);
                allVotes.add(cardToReveal.cardType);
                //dont need to remove cards?
                //hand.remove(cardToReveal);
                // MIGHT NEED FOR HEURISTIC/INFO
                //resgs.playedCards.get(i).add(cardToReveal);
//                resgs.playedCardTypes[i].get(cardToReveal.type).increment(cardToReveal.count);
//                resgs.playedCardTypesAllGame[i].get(cardToReveal.type).increment(cardToReveal.count);
//                //Add points to player
//                cardToReveal.type.onReveal(resgs, i);
            }
            //System.out.println("player :"+ i + "  |card");
        }
        // returns if the vote successful
            int occurrenceCount = Collections.frequency(allVotes, ResPlayerCards.CardType.Yes);
            System.out.println("Team Vote :  " + allVotes);
            System.out.println("Team Yes Occurrence Count : " + occurrenceCount  );
            if (occurrenceCount > allVotes.size()/2){resgs.voteSuccess = true;}
            else{resgs.voteSuccess = false; failedVoteCounter += 1;}
        }

        if (resgs.getGamePhase() == LeaderSelectsTeam){
            ArrayList<int[]> allActions = new ArrayList<>();
            for (int i = 0; i < resgs.getNPlayers(); i++) {

                PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                    if(hand.get(0).cardType == ResPlayerCards.CardType.LEADER) {

                        for (ResTeamBuilding cc : resgs.teamChoice) {

                            //System.out.println(cc.team + "team");
                            allActions.add(cc.team);
                            resgs.finalTeamChoice = cc.team;
                        }
                        //System.out.println(allActions.get(0));
                    }
                    ///////////////////////////
                ///////////////////////////
/////////////////////Could be voting on wrong teamchoice for players B4 leader

                    System.out.println("Final Team :  " + resgs.finalTeamChoice.toString());
                    //if(allVotes.size() == 5){System.out.println(allVotes);}

                    //dont need to remove cards?

                    //hand.remove(cardToReveal);

                    // MIGHT NEED FOR HEURISTIC/INFO
                    //resgs.playedCards.get(i).add(cardToReveal);



//                resgs.playedCardTypes[i].get(cardToReveal.type).increment(cardToReveal.count);
//                resgs.playedCardTypesAllGame[i].get(cardToReveal.type).increment(cardToReveal.count);
//
//                //Add points to player
//                cardToReveal.type.onReveal(resgs, i);

            }



        }
        //System.out.println(allVotes);

        //////////////////////////////////////////

        /////////////////////////////////////////

        // MISSION CARDS ARE NOT HIDDEN
        /////////////////////////////

        if (resgs.getGamePhase() == MissionVote){
            ArrayList<ResPlayerCards.CardType> allVotes = new ArrayList<>();
            //System.out.println(Arrays.toString(resgs.finalTeamChoice));
            int counter = 0;
            for (int i : resgs.finalTeamChoice) {
                PartialObservableDeck<ResPlayerCards> hand = resgs.playerHandCards.get(i);
                counter += 1;

                for (ResMissionVoting cc: resgs.missionVotingChoice.get(i)) {
                    ResPlayerCards cardToReveal = hand.get(cc.cardIdx);

                    allVotes.add(cardToReveal.cardType);

                    //System.out.println(cardToReveal + "mission");

                    //if(allVotes.size() == 5){System.out.println(allVotes);}

                    //dont need to remove cards?

                    //hand.remove(cardToReveal);

                    // MIGHT NEED FOR HEURISTIC/INFO
                    //resgs.playedCards.get(i).add(cardToReveal);



//                resgs.playedCardTypes[i].get(cardToReveal.type).increment(cardToReveal.count);
//                resgs.playedCardTypesAllGame[i].get(cardToReveal.type).increment(cardToReveal.count);
//
//                //Add points to player
//                cardToReveal.type.onReveal(resgs, i);
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
        for (ResPlayerCards.CardType type: ResPlayerCards.CardType.values()) {
            type.onRoundEnd(resgs);
        }
        //resgs.setGamePhase(TeamSelectionVote);


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
