package games.secrethitler;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Deck;
import core.components.PartialObservableDeck;
import games.resistance.actions.ResTeamBuilding;
import games.secrethitler.actions.SHChancellorSelection;
import games.secrethitler.actions.SHPolicySelection;
import games.secrethitler.actions.SHVoting;
import games.secrethitler.actions.SHWait;
import games.secrethitler.components.SHPlayerCards;
import games.secrethitler.components.SHPolicyCards;
import utilities.Utils;

import java.util.*;

import static games.secrethitler.SHGameState.SHGamePhase.*;


/**
 * <p>The forward model contains all the game rules and logic. It is mainly responsible for declaring rules for:</p>
 * <ol>
 *     <li>Game setup</li>
 *     <li>Actions available to players in a given game state</li>
 *     <li>Game events or rules applied after a player's action</li>
 *     <li>Game end</li>
 * </ol>
 */
public class SHForwardModel extends StandardForwardModel {
    public int counter = 0;
    boolean haveBeenInLoop = false;
    boolean roundEnded = false;

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
        SHGameState shgs = (SHGameState)firstState;
        SHParameters resp = (SHParameters)firstState.getGameParameters();
        System.out.println(firstState.getNPlayers() +"firststate");
        shgs.votingChoice = new ArrayList<>(firstState.getNPlayers());
        shgs.missionVotingChoice = new ArrayList<>(firstState.getNPlayers());
        shgs.gameBoardValues = new ArrayList<>(5);
        shgs.failedVoteCounter = 0;
        shgs.playerHandCards = new ArrayList<>(firstState.getNPlayers());
        shgs.gameBoard = resp.getPlayerBoard(firstState.getNPlayers());
        if(shgs.gameBoard == null)
        {throw new AssertionError("GameBoard shouldn't be null");};
        shgs.factions = resp.getFactions(firstState.getNPlayers());


        // Set up draw pile deck
        PartialObservableDeck<SHPolicyCards> drawPile = new PartialObservableDeck<>("Draw Pile", firstState.getNPlayers());
        shgs.setDrawPile(drawPile);

        // Add 11 fascist policies
        for (int i = 0; i < 11; i++) {
            SHPolicyCards card = new SHPolicyCards(SHPolicyCards.CardType.Fascist);
            drawPile.add(card);
        }

        // Add 6 liberal policies
        for (int i = 0; i < 6; i++) {
            SHPolicyCards card = new SHPolicyCards(SHPolicyCards.CardType.Liberal);
            drawPile.add(card);
        }

        //Shuffle Deck
        shgs.getDrawPile().shuffle(rnd);

        int fascistCounter = 0;
        int liberalCounter = 0;
        for (int i = 0; i < firstState.getNPlayers(); i++) {
            shgs.votingChoice.add(new ArrayList<>());
            shgs.missionVotingChoice.add(new ArrayList<>());
            boolean[] visible = new boolean[firstState.getNPlayers()];
            visible[i] = false;
            PartialObservableDeck<SHPlayerCards> playerCards = new PartialObservableDeck<>("Player Cards", visible);


            // Add identity cards to hand
            if (rnd.nextInt(2) == 0 && fascistCounter != shgs.factions[1]) {
                SHPlayerCards Fascist = new SHPlayerCards(SHPlayerCards.CardType.Fascist);
                Fascist.setOwnerId(i);
                playerCards.add(Fascist);
                fascistCounter += 1;
            }
            else if (liberalCounter != shgs.factions[0])
            {
                SHPlayerCards Liberal = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
                Liberal.setOwnerId(i);
                playerCards.add(Liberal);
                liberalCounter += 1;
            }

            else if (fascistCounter != shgs.factions[1] && liberalCounter == shgs.factions[0] )
            {
                SHPlayerCards Fascist = new SHPlayerCards(SHPlayerCards.CardType.Fascist);
                Fascist.setOwnerId(i);
                playerCards.add(Fascist);
                fascistCounter += 1;
            }
            else if (fascistCounter == shgs.factions[1] && liberalCounter != shgs.factions[0] )
            {
                SHPlayerCards Liberal = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
                Liberal.setOwnerId(i);
                playerCards.add(Liberal);
                liberalCounter += 1;
            }
            //Add Voting Cards in random order, done so when getting hidden choice at index 0, will be random vote
            if(rnd.nextInt(2) == 0) {
                SHPlayerCards yes = new SHPlayerCards(SHPlayerCards.CardType.Yes);
                yes.setOwnerId(i);
                playerCards.add(yes);

                SHPlayerCards no = new SHPlayerCards(SHPlayerCards.CardType.No);
                no.setOwnerId(i);
                playerCards.add(no);
            }
            else{
                SHPlayerCards no = new SHPlayerCards(SHPlayerCards.CardType.No);

                no.setOwnerId(i);
                playerCards.add(no);

                SHPlayerCards yes = new SHPlayerCards(SHPlayerCards.CardType.Yes);
                yes.setOwnerId(i);
                playerCards.add(yes);
            }
            shgs.playerHandCards.add(playerCards);
        }

        //Adding leader card
        int randomPlayerLeader = rnd.nextInt(shgs.getNPlayers());
        System.out.println("Random Player Leader : " + randomPlayerLeader);
        shgs.leaderID = randomPlayerLeader;
        shgs.setGamePhase(VotingOnLeader);
        shgs.previousGamePhase = VotingOnLeader;
    }

    /**
     * Calculates the list of currently available actions, possibly depending on the game phase.
     * @return - List of AbstractAction objects.
     */
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {

        SHGameState shgs = (SHGameState) gameState;

        List<AbstractAction> actions = new ArrayList<>();
        int currentPlayer = shgs.getCurrentPlayer();

        Deck<SHPlayerCards> currentPlayerHand = shgs.getPlayerHandCards().get(currentPlayer);

        if (shgs.getGamePhase() == VotingOnLeader || shgs.getGamePhase() == VotingOnChancellor)
        {
            actions.add(new SHVoting(currentPlayer, SHPlayerCards.CardType.Yes));
            actions.add(new SHVoting(currentPlayer, SHPlayerCards.CardType.No));
        }


        if(shgs.getGamePhase()== LeaderSelectsChancellor) {
            if (currentPlayer == shgs.leaderID) {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    if (shgs.getRoundCounter() != 0) {
                        if (i != shgs.previousLeader && i != shgs.previousChancellor) {
                            actions.add(new SHChancellorSelection(currentPlayer, i));
                        }
                    }
                }
            }

            //Every Other Player Waits
            else{
                actions.add(new SHWait(currentPlayer));
            }
        }

        if(shgs.getGamePhase()== LeaderSelectsPolicy) {
            if (currentPlayer == shgs.leaderID) {
                shgs.drawnPolicies = new ArrayList<>();
                //shgs.discardPile.add(card); REMMEBER TO ADD DISCARDS/PLAYED CARDS CORRECTLY
                SHPolicyCards card = shgs.drawPile.draw();
                shgs.drawnPolicies.add(card);
                SHPolicyCards card1 = shgs.drawPile.draw();
                shgs.drawnPolicies.add(card1);
                SHPolicyCards card2 = shgs.drawPile.draw();
                shgs.drawnPolicies.add(card2);

                int[] numberOfDrawnPolicies = new int[3];
                for (int i = 0; i < 3; i++) {numberOfDrawnPolicies[i] = i;}
                ArrayList<int[]> choiceOfPolicies = Utils.generateCombinations(numberOfDrawnPolicies, 2);

                for(int[] combinations : choiceOfPolicies)
                {
                    shgs.final2PolicyChoices = new ArrayList<>();
                    for (int index:combinations) {shgs.final2PolicyChoices.add(shgs.drawnPolicies.get(index));}
                    actions.add(new SHPolicySelection(currentPlayer,shgs.final2PolicyChoices));
                }
            }

            else {
                actions.add(new SHWait(currentPlayer));
            }
        }

        if(shgs.getGamePhase()== ChancellorSelectsPolicy) {
            if (currentPlayer == shgs.chancellorID) {
                    ArrayList<SHPolicyCards> finalPolicyChoice = new ArrayList<>();
                    finalPolicyChoice.add(shgs.finalPolicyChoice.get(0));
                    actions.add(new SHPolicySelection(currentPlayer,finalPolicyChoice));

                    ArrayList<SHPolicyCards> finalPolicyChoice1 = new ArrayList<>();
                    finalPolicyChoice1.add(shgs.finalPolicyChoice.get(1));
                    actions.add(new SHPolicySelection(currentPlayer,finalPolicyChoice1));
            }

            else {
                actions.add(new SHWait(currentPlayer));
            }
        }

            //System.out.println(actions.get(0).toString());
            return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        SHGameState shgs = (SHGameState) currentState;
        if(shgs.getCurrentPlayer() % 4 == 0) {System.out.println("Current Round : " + shgs.getRoundCounter());}
        System.out.println("Action Taken : " + action.getString(shgs));

        //Leader Selects Team
        if (shgs.getGamePhase() == VotingOnLeader && haveBeenInLoop == false) {
            //if (shgs.getCurrentPlayer() == shgs.leaderID){System.out.println("Action Taken By Leader : " + action.getString(shgs));}
            int turn = shgs.getTurnCounter();

            if ((turn + 1) % (shgs.getNPlayers()) == 0) {
                revealCards(shgs);
                if(shgs.voteSuccess == true){
                    shgs.setGamePhase(LeaderSelectsChancellor);
                    shgs.clearVoteChoices();
                    shgs.failedVoteCounter = 0;
                }
                else{
                    //shgs.clearCardChoices();
                    //shgs.clearTeamChoices();
                    shgs.clearVoteChoices();


                    // CHANGE LEADER
                    changeLeader(shgs);
                    shgs.previousGamePhase = shgs.getGamePhase();

                }
                //shgs.previousGamePhase = shgs.getGamePhase();


            }
            else{shgs.previousGamePhase = shgs.getGamePhase();}
            haveBeenInLoop = true;
        }

        if (shgs.getGamePhase() == LeaderSelectsChancellor && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn+1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase())
            {
                revealCards(shgs);
                shgs.setGamePhase(VotingOnChancellor);
            }

            else{shgs.previousGamePhase = shgs.getGamePhase();}
            haveBeenInLoop = true;
        }

        if (shgs.getGamePhase() == VotingOnChancellor && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();

            if ((turn + 1) % (shgs.getNPlayers()) == 0) {
                revealCards(shgs);
                if (shgs.voteSuccess == true) {
                    shgs.setGamePhase(LeaderSelectsPolicy);
                    shgs.clearVoteChoices();
                    shgs.failedVoteCounter = 0;
                } else {
                    shgs.clearCardChoices();
                    // CHANGE LEADER
                    changeLeader(shgs);
                    shgs.previousGamePhase = shgs.getGamePhase();

                }

            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == LeaderSelectsPolicy && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn+1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase())
            {
                revealCards(shgs);
                shgs.setGamePhase(ChancellorSelectsPolicy);
            }

            else{shgs.previousGamePhase = shgs.getGamePhase();}
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == ChancellorSelectsPolicy && haveBeenInLoop == false) {
            int turn = shgs.getTurnCounter();
            //if(shgs.finalTeamChoice.size() == 0) {throw new AssertionError("Final Team Choice Size is Zero");}
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {

                revealCards(shgs);


//                // Check if the round is over
  //              if (isRoundOver(shgs)) {
                // It is! Process end of round rules.
                shgs.clearCardChoices();
                shgs.clearMissionChoices();
                shgs.clearVoteChoices();
                shgs.clearTeamChoices();
                changeLeader(shgs);
                endRound(shgs);
                roundEnded = true;
                _endRound(shgs);

                // Clear card choices from this turn, ready for the next simultaneous choice.



                // Check if the game is over
                int occurrenceCountTrue = Collections.frequency(shgs.gameBoardValues, true);
                int occurrenceCountFalse = Collections.frequency(shgs.gameBoardValues, false);
                System.out.println("Size of gameboard values: " +  shgs.gameBoardValues.size());
                System.out.println("Occurrence True : " + occurrenceCountTrue);
                System.out.println("Occurrence False : " + occurrenceCountFalse);
                if (occurrenceCountTrue == 3) {
                    // Decide winner
                    for (int i = 0; i < shgs.getNPlayers(); i++) {
                        PartialObservableDeck<SHPlayerCards> hand = shgs.playerHandCards.get(i);
                        if (hand.get(2).cardType == SHPlayerCards.CardType.Liberal) {
                            shgs.setPlayerResult(CoreConstants.GameResult.WIN, i);
                        } else {
                            shgs.setPlayerResult(CoreConstants.GameResult.LOSE, i);
                        }
                    }
                    shgs.winners = 0;
                    shgs.setGameStatus(CoreConstants.GameResult.GAME_END);
                    endGame(shgs);
                    //roundEnded = true;
                    if(occurrenceCountTrue == 3){ System.out.println("GAME ENDED BY SUCCESSFUL MISSIONS");}
                    ////MAYBE GET RID OF RETURNS
                    //return;
                }

                if (occurrenceCountFalse == 3) {
                    // Decide winner
                    for (int i = 0; i < shgs.getNPlayers(); i++) {
                        PartialObservableDeck<SHPlayerCards> hand = shgs.playerHandCards.get(i);
                        if (hand.get(2).cardType == SHPlayerCards.CardType.Fascist) {
                            shgs.setPlayerResult(CoreConstants.GameResult.WIN, i);
                        } else {
                            shgs.setPlayerResult(CoreConstants.GameResult.LOSE, i);
                        }
                    }
                    System.out.println(shgs.getPlayerResults()[0] + "      Player Results");
                    shgs.winners = 1;
                    shgs.setGameStatus(CoreConstants.GameResult.GAME_END);
                    endGame(shgs);
                    //roundEnded = true;
                    if(occurrenceCountFalse == 3){ System.out.println("GAME ENDED BY FAILED MISSIONS");}
                    //return;

                }
                if(shgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
                    shgs.failedVoteCounter = 0;
                    shgs.setGamePhase(VotingOnLeader);
                }



            //}
//                else {
//                    // Clear card choices
//                    shgs.clearCardChoices();
//                    shgs.clearMissionChoices();
//                    shgs.clearTeamChoices();
                //}
            }

            else{shgs.previousGamePhase = shgs.getGamePhase();}
            haveBeenInLoop = true;
        }
        //End player turn
        if (shgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING && roundEnded == false) {
            endPlayerTurn(shgs);
        }
        haveBeenInLoop = false;
        roundEnded = false;

    }

    void revealCards(SHGameState shgs) {
        if (shgs.getGamePhase() == VotingOnLeader || shgs.getGamePhase() == VotingOnChancellor){

            ArrayList<SHPlayerCards.CardType> allVotes = new ArrayList<>();
        for (int i = 0; i < shgs.getNPlayers(); i++) {
            for (SHVoting cc: shgs.votingChoice.get(i)) {
                allVotes.add(cc.cardType);
            }
        }

            int occurrenceCount = Collections.frequency(allVotes, SHPlayerCards.CardType.Yes);
            System.out.println("Team Vote :  " + allVotes);
            if( allVotes.contains(SHPlayerCards.CardType.Fascist ) || allVotes.contains(SHPlayerCards.CardType.Liberal ))
            {throw new AssertionError("Incorrect Type in Team Vote");}
            System.out.println("Team Yes Occurrence Count : " + occurrenceCount  );
            if (occurrenceCount > allVotes.size()/2){shgs.voteSuccess = true;}
            else{shgs.voteSuccess = false; shgs.failedVoteCounter += 1;}
        }

        if (shgs.getGamePhase() == LeaderSelectsChancellor){}
        if (shgs.getGamePhase() == VotingOnChancellor){}


        if (shgs.getGamePhase() == LeaderSelectsPolicy)
        {
                //Adding Excess Card To DiscardPile
                if(!shgs.final2PolicyChoices.contains(shgs.drawnPolicies.get(0))){shgs.discardPile.add(shgs.drawnPolicies.get(0));}
                if(!shgs.final2PolicyChoices.contains(shgs.drawnPolicies.get(1))){shgs.discardPile.add(shgs.drawnPolicies.get(1));}
                if(!shgs.final2PolicyChoices.contains(shgs.drawnPolicies.get(2))){shgs.discardPile.add(shgs.drawnPolicies.get(2));}
        }

        if (shgs.getGamePhase() == ChancellorSelectsPolicy)
        {
            //Adding Excess Card To DiscardPile
            if(!shgs.finalPolicyChoice.contains(shgs.final2PolicyChoices.get(0))){shgs.discardPile.add(shgs.final2PolicyChoices.get(0));}
            if(!shgs.finalPolicyChoice.contains(shgs.final2PolicyChoices.get(1))){shgs.discardPile.add(shgs.final2PolicyChoices.get(1));}
            //Adding CardType To GameBoard
            if(shgs.finalPolicyChoice.get(0).cardType == SHPolicyCards.CardType.Fascist){shgs.gameBoardValues.add(false);}
            else {shgs.gameBoardValues.add(true);}
        }



    }

    public void _startRound(SHGameState shgs) {

    }

    boolean isRoundOver(SHGameState shgs) {
        //some logic

        return true;
    }
    public void _endRound(SHGameState shgs) {
        // Apply card end of round rules
//        for (SHPlayerCards.CardType type: SHPlayerCards.CardType.values()) {
//            type.onRoundEnd(shgs);
//        }
        //shgs.setGamePhase(LeaderSelectsTeam);
        //shgs.teamChoice.clear();
        roundEnded = true;


    }
    @Override
    protected void endGame(AbstractGameState gs) {
        gs.setGameStatus(CoreConstants.GameResult.GAME_END);
        if (gs.getCoreGameParameters().verbose) {
            System.out.println(Arrays.toString(gs.getPlayerResults()));
        }
    }
    public void changeLeader(SHGameState shgs) {
        for (int i = 0; i < shgs.getNPlayers(); i++){
            if (i == shgs.leaderID)
            {
                if(i + 1 == shgs.getNPlayers()) {shgs.leaderID = 0;}
                else{shgs.leaderID = i + 1;}
                break;
            }
        }
    }
}
