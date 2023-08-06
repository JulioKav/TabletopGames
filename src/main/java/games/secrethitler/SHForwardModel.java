package games.secrethitler;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Deck;
import core.components.PartialObservableDeck;
import games.resistance.actions.ResTeamBuilding;
import games.resistance.components.ResPlayerCards;
import games.secrethitler.actions.*;
import games.secrethitler.components.SHPlayerCards;
import games.secrethitler.components.SHPolicyCards;
import scala.Int;
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
    public int whereDrawPileCHanges;
    boolean haveBeenInLoop = false;


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
        SHGameState shgs = (SHGameState) firstState;
        SHParameters resp = (SHParameters) firstState.getGameParameters();
        System.out.println(firstState.getNPlayers() + "firststate");
        shgs.votingChoice = new ArrayList<>(firstState.getNPlayers());
        shgs.vetoChoice = new ArrayList<>(10);
        shgs.missionVotingChoice = new ArrayList<>(firstState.getNPlayers());
        shgs.gameBoardValues = new ArrayList<>(11);
        shgs.failedVoteCounter = 0;
        shgs.peekedCards = new ArrayList<>();
        shgs.playerHandCards = new ArrayList<>(firstState.getNPlayers());
        shgs.gameBoard = resp.getPlayerBoard(firstState.getNPlayers());
        shgs.previousChancellor = 66;
        shgs.previousLeader = 67;
        shgs.finalPolicyChoice = new ArrayList<>();
        shgs.knownIdentities = new HashMap<>();
        shgs.final2PolicyChoices = new ArrayList<>();
        shgs.drawnPolicies = new ArrayList<>();
        for (int i = 0; i < shgs.getNPlayers(); i++) {
            shgs.knownIdentities.put(i, new ArrayList<>());
        }
        for (int i = 0; i < shgs.getNPlayers(); i++) {
            shgs.vetoChoice.add(new ArrayList<>());
        }
        if (shgs.gameBoard == null) {
            throw new AssertionError("GameBoard shouldn't be null");
        }
        ;
        shgs.factions = resp.getFactions(firstState.getNPlayers());


        // Set up draw pile deck

        shgs.drawPile = new PartialObservableDeck<>("Draw Pile", firstState.getNPlayers());

        boolean[] visible = new boolean[17];
        for (int i = 0; i < visible.length; i++) {
            visible[i] = false;
        }
        shgs.discardPile = new PartialObservableDeck<>("discard pile", -1, visible);
        // Add 11 fascist policies
        for (int i = 0; i < 11; i++) {
            SHPolicyCards card = new SHPolicyCards(SHPolicyCards.CardType.Fascist);
            shgs.drawPile.add(card);
        }

        // Add 6 liberal policies
        for (int i = 0; i < 6; i++) {
            SHPolicyCards card = new SHPolicyCards(SHPolicyCards.CardType.Liberal);
            shgs.drawPile.add(card);
        }

        //Shuffle Deck
        shgs.drawPile.shuffle(rnd);

        int fascistCounter = 0;
        int liberalCounter = 0;
        for (int i = 0; i < firstState.getNPlayers(); i++) {
            shgs.votingChoice.add(new ArrayList<>());
            shgs.missionVotingChoice.add(new ArrayList<>());
            PartialObservableDeck<SHPlayerCards> playerCards = new PartialObservableDeck<>("Player Cards", visible);


            // Add identity cards to hand
            if (rnd.nextInt(2) == 0 && fascistCounter != shgs.factions[1]) {
                SHPlayerCards Fascist = new SHPlayerCards(SHPlayerCards.CardType.Fascist);
                SHPlayerCards FascistID = new SHPlayerCards(SHPlayerCards.CardType.FascistID);
                Fascist.setOwnerId(i);
                FascistID.setOwnerId(i);
                playerCards.add(FascistID);
                playerCards.add(Fascist);
                fascistCounter += 1;
            } else if (liberalCounter != shgs.factions[0]) {
                SHPlayerCards Liberal = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
                SHPlayerCards LiberalID = new SHPlayerCards(SHPlayerCards.CardType.LiberalID);
                Liberal.setOwnerId(i);
                LiberalID.setOwnerId(i);
                playerCards.add(LiberalID);
                playerCards.add(Liberal);

                liberalCounter += 1;
            } else if (fascistCounter != shgs.factions[1] && liberalCounter == shgs.factions[0]) {
                SHPlayerCards Fascist = new SHPlayerCards(SHPlayerCards.CardType.Fascist);
                SHPlayerCards FascistID = new SHPlayerCards(SHPlayerCards.CardType.FascistID);
                Fascist.setOwnerId(i);
                FascistID.setOwnerId(i);
                playerCards.add(FascistID);
                playerCards.add(Fascist);

                fascistCounter += 1;
            } else if (fascistCounter == shgs.factions[1] && liberalCounter != shgs.factions[0]) {
                SHPlayerCards Liberal = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
                SHPlayerCards LiberalID = new SHPlayerCards(SHPlayerCards.CardType.LiberalID);
                Liberal.setOwnerId(i);
                LiberalID.setOwnerId(i);
                playerCards.add(LiberalID);
                playerCards.add(Liberal);

                liberalCounter += 1;
            }
            //Add Voting Cards in random order, done so when getting hidden choice at index 0, will be random vote
            if (rnd.nextInt(2) == 0) {
                SHPlayerCards yes = new SHPlayerCards(SHPlayerCards.CardType.Yes);
                yes.setOwnerId(i);
                playerCards.add(yes);

                SHPlayerCards no = new SHPlayerCards(SHPlayerCards.CardType.No);
                no.setOwnerId(i);
                playerCards.add(no);
            } else {
                SHPlayerCards no = new SHPlayerCards(SHPlayerCards.CardType.No);

                no.setOwnerId(i);
                playerCards.add(no);

                SHPlayerCards yes = new SHPlayerCards(SHPlayerCards.CardType.Yes);
                yes.setOwnerId(i);
                playerCards.add(yes);
            }
            shgs.playerHandCards.add(playerCards);
        }

        //Adding leader
        int randomPlayerLeader = rnd.nextInt(shgs.getNPlayers());
        System.out.println("Random Player Leader : " + randomPlayerLeader);
        shgs.leaderID = randomPlayerLeader;
        shgs.setGamePhase(VotingOnLeader);
        shgs.previousGamePhase = VotingOnLeader;

        //Assigning a fascist as Hitler
        List<Integer> listOfFascists = new ArrayList<>();
        for (int i = 0; i < shgs.playerHandCards.size(); i++) {
            System.out.println(shgs.playerHandCards.get(i).get(3).cardType);
            if (shgs.playerHandCards.get(i).get(2).cardType == SHPlayerCards.CardType.Fascist) {
                listOfFascists.add(i);
            }
        }
        int randomPlayerHitler = rnd.nextInt(listOfFascists.size());
        shgs.hitlerID = listOfFascists.get(randomPlayerHitler);
        SHPlayerCards hitler = new SHPlayerCards(SHPlayerCards.CardType.Hitler);
        hitler.setOwnerId(shgs.hitlerID);
        shgs.playerHandCards.get(shgs.hitlerID).remove(3);
        shgs.playerHandCards.get(shgs.hitlerID).add(hitler,3);
    }

    /**
     * Calculates the list of currently available actions, possibly depending on the game phase.
     *
     * @return - List of AbstractAction objects.
     */
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {


        SHGameState shgs = (SHGameState) gameState;

        whereDrawPileCHanges = shgs.drawPile.getSize();
        List<AbstractAction> actions = new ArrayList<>();
        int currentPlayer = shgs.getCurrentPlayer();

        if (shgs.getGamePhase() == VotingOnLeader || shgs.getGamePhase() == VotingOnChancellor) {
            if (!shgs.deceasedFellas.contains(currentPlayer)) {
                actions.add(new SHVoting(currentPlayer, SHPlayerCards.CardType.Yes));
                actions.add(new SHVoting(currentPlayer, SHPlayerCards.CardType.No));
            } else {
                actions.add(new SHDeceased(currentPlayer));
            }
        }

        if (shgs.getGamePhase() == Veto) {
            if (currentPlayer == shgs.leaderID || currentPlayer == shgs.chancellorID) {
                actions.add(new SHVeto(currentPlayer, SHPlayerCards.CardType.Yes));
                actions.add(new SHVeto(currentPlayer, SHPlayerCards.CardType.No));
            }

            //Every Other Player Waits
            else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }

        if (shgs.getGamePhase() == LeaderSelectsChancellor) {
            if (currentPlayer == shgs.leaderID) {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    if (i != shgs.previousLeader && i != shgs.previousChancellor && i != shgs.leaderID && !shgs.deceasedFellas.contains(i)) {
                        actions.add(new SHChancellorSelection(currentPlayer, i));
                    }
                }
                if (actions.size() == 0) {
                    for (int i = 0; i < shgs.getNPlayers(); i++) {
                        if (i != shgs.getLeaderID() && !shgs.getDeceasedFellas().contains(i)) {
                            actions.add(new SHChancellorSelection(shgs.getCurrentPlayer(), i));
                        }
                    }
                }
            }

            //Every Other Player Waits
            else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }

        if (shgs.getGamePhase() == LeaderSelectsLeader) {
            if (currentPlayer == shgs.leaderID) {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    actions.add(new SHLeaderSelectsLeader(currentPlayer, i));
                }
            }

            //Every Other Player Waits
            else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }

        if (shgs.getGamePhase() == LeaderSelectsPolicy) {
            if (currentPlayer == shgs.leaderID) {

                System.out.println(shgs.drawnPolicies + " drawn policies");
                int[] numberOfDrawnPolicies = new int[3];
                for (int i = 0; i < 3; i++) {
                    numberOfDrawnPolicies[i] = i;
                }
                ArrayList<int[]> choiceOfPolicies = Utils.generateCombinations(numberOfDrawnPolicies, 2);

                System.out.println(shgs.drawPile.getSize() + " draw pile size");
                for (int[] combinations : choiceOfPolicies) {
                    shgs.final2PolicyChoices = new ArrayList<>();
                    for (int index : combinations) {
                        shgs.final2PolicyChoices.add(shgs.drawnPolicies.get(index));
                    }
                    actions.add(new SHPolicySelection(currentPlayer, shgs.final2PolicyChoices, shgs.drawnPolicies, shgs.discardPile));
                }
            } else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }

        if (shgs.getGamePhase() == ChancellorSelectsPolicy) {
            if (currentPlayer == shgs.chancellorID) {
                ArrayList<SHPolicyCards> finalPolicyChoice = new ArrayList<>();
                finalPolicyChoice.add(shgs.final2PolicyChoices.get(0));
                actions.add(new SHPolicySelection(currentPlayer, finalPolicyChoice, shgs.drawnPolicies, shgs.discardPile));

                ArrayList<SHPolicyCards> finalPolicyChoice1 = new ArrayList<>();
                finalPolicyChoice1.add(shgs.final2PolicyChoices.get(1));

                actions.add(new SHPolicySelection(currentPlayer, finalPolicyChoice1, shgs.drawnPolicies, shgs.discardPile));
            } else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }
        if (shgs.getGamePhase() == LeaderKillsPlayer) {
            if (currentPlayer == shgs.leaderID) {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    if (i != shgs.previousLeader && i != shgs.previousChancellor && i != shgs.leaderID && !shgs.deceasedFellas.contains(i)) {
                        actions.add(new SHKill(currentPlayer, i));
                    }
                }
            }

            //Every Other Player Waits
            else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }
        if (shgs.getGamePhase() == LeaderPeeksTop3Cards) {
            if (currentPlayer == shgs.leaderID) {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    actions.add(new SHLeaderPeeks(currentPlayer, shgs.drawPile.peek(0, 3)));
                }
            }

            //Every Other Player Waits
            else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }
        if (shgs.getGamePhase() == LeaderInvestigatesPlayer) {
            if (currentPlayer == shgs.leaderID) {
                for (int i = 0; i < shgs.getNPlayers(); i++) {
                    if (i != shgs.leaderID && !shgs.deceasedFellas.contains(i)) {
                        actions.add(new SHInvestigateIdentity(currentPlayer, i));
                    }
                }
            }

            //Every Other Player Waits
            else {
                if (!shgs.deceasedFellas.contains(currentPlayer)) {
                    actions.add(new SHWait(currentPlayer));
                } else {
                    actions.add(new SHDeceased(currentPlayer));
                }
            }
        }
        return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        SHGameState shgs = (SHGameState) currentState;
        if (shgs.getCurrentPlayer() % 4 == 0) {
            System.out.println("Current Round : " + shgs.getRoundCounter());
        }
        System.out.println("Action Taken : " + action.getString(shgs));

        //Leader Selects Team
        if (shgs.getGamePhase() == VotingOnLeader && haveBeenInLoop == false) {
            int turn = shgs.getTurnCounter();

            if ((turn + 1) % (shgs.getNPlayers()) == 0) {

                revealCards(shgs);
                if (shgs.deceasedFellas.contains(shgs.hitlerID)) {
                    liberalWin(shgs);
                    System.out.println("Hitler Has Bitten The Bullet");
                }
                //Reveal and Enact Policy On 3 Failed Votes
                else if (shgs.failedVoteCounter == 3) {
                    drawSingleCardAndPlay(shgs);
                    shgs.occurrenceCountTrue = Collections.frequency(shgs.gameBoardValues, true);
                    shgs.occurrenceCountFalse = Collections.frequency(shgs.gameBoardValues, false);
                    if (shgs.occurrenceCountTrue == 5) {
                        liberalWin(shgs);
                    } else if (shgs.occurrenceCountFalse == 6) {
                        fascistWin(shgs);
                    } else {
                        changeLeader(shgs);
                        shgs.clearVoteChoices();
                        shgs.failedVoteCounter = 0;
                        shgs.setGamePhase(VotingOnLeader);
                        endRound(shgs);
                        shgs.roundEnded = true;

                        System.out.println("Failed Vote Caused Policy To Be Flipped");
                    }
                } else {
                    if (shgs.voteSuccess == true) {
                        shgs.setGamePhase(LeaderSelectsChancellor);
                        shgs.clearVoteChoices();
                        shgs.failedVoteCounter = 0;
                    } else {
                        shgs.clearVoteChoices();


                        // CHANGE LEADER
                        changeLeader(shgs);
                        shgs.previousGamePhase = shgs.getGamePhase();

                    }
                }
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }

        if (shgs.getGamePhase() == LeaderSelectsChancellor && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                revealCards(shgs);
                if (shgs.hitlerID == shgs.chancellorID && shgs.occurrenceCountFalse > 3) {
                    fascistWin(shgs);
                    System.out.println("Hitler Has Snuck Into Power");
                }
                shgs.setGamePhase(VotingOnChancellor);
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }

        if (shgs.getGamePhase() == VotingOnChancellor && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();

            if ((turn + 1) % (shgs.getNPlayers()) == 0) {
                revealCards(shgs);
                //Reveal and Enact Policy On 3 Failed Votes
                if (shgs.failedVoteCounter == 3) {
                    drawSingleCardAndPlay(shgs);
                    shgs.occurrenceCountTrue = Collections.frequency(shgs.gameBoardValues, true);
                    shgs.occurrenceCountFalse = Collections.frequency(shgs.gameBoardValues, false);
                    if (shgs.occurrenceCountTrue == 5) {
                        liberalWin(shgs);
                    } else if (shgs.occurrenceCountFalse == 6) {
                        fascistWin(shgs);
                    } else {
                        shgs.previousLeader = shgs.leaderID;
                        changeLeader(shgs);
                        shgs.clearVoteChoices();
                        shgs.failedVoteCounter = 0;
                        shgs.setGamePhase(VotingOnLeader);
                        endRound(shgs);
                        shgs.roundEnded = true;
                        System.out.println("Failed Vote Caused Policy To Be Flipped");
                    }
                } else {
                    if (shgs.voteSuccess == true) {
                        if (Collections.frequency(shgs.gameBoardValues, false) > 3 && shgs.getHitlerID() == shgs.getChancellorID()) {
                            fascistWin(shgs);
                        }

                        shgs.clearVoteChoices();
                        shgs.failedVoteCounter = 0;
                        shgs.drawnPolicies.clear();
                        if (shgs.drawPile.getSize() < 3) {
                            shuffleDiscardsIntoDrawPile(shgs);
                        }
                        for (int i = 0; i < 3; i++) {
                            SHPolicyCards card = shgs.drawPile.draw();
                            if (card == null) {
                                throw new AssertionError("card is null after shuffle ");
                            }
                            shgs.drawnPolicies.add(card);
                        }
                        shgs.setGamePhase(LeaderSelectsPolicy);
                    } else {
                        shgs.clearCardChoices();

                        // CHANGE LEADER
                        //changeChancellor(shgs);
                        shgs.previousGamePhase = shgs.getGamePhase();
                        shgs.setGamePhase(LeaderSelectsChancellor);

                    }
                }

            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == LeaderSelectsPolicy && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                revealCards(shgs);
                shgs.setGamePhase(ChancellorSelectsPolicy);
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == ChancellorSelectsPolicy && haveBeenInLoop == false) {
            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {

                if (shgs.failedVoteCounter == 3) {
                    drawSingleCardAndPlay(shgs);
                    shgs.occurrenceCountTrue = Collections.frequency(shgs.gameBoardValues, true);
                    shgs.occurrenceCountFalse = Collections.frequency(shgs.gameBoardValues, false);
                    if (shgs.occurrenceCountTrue == 5) {
                        liberalWin(shgs);
                        System.out.println("Failed Veto Votes Caused Policy To Be Flipped For Liberal Win");
                    } else if (shgs.occurrenceCountFalse == 6) {
                        fascistWin(shgs);
                        System.out.println("Failed Veto Votes Caused Policy To Be Flipped For Fascist Win");
                    } else {
                        shgs.previousLeader = shgs.leaderID;
                        changeLeader(shgs);
                        shgs.clearVoteChoices();
                        shgs.failedVoteCounter = 0;
                        shgs.setGamePhase(VotingOnLeader);
                        endRound(shgs);
                        shgs.roundEnded = true;
                        System.out.println("Failed Veto Votes Caused Policy To Be Flipped");
                    }
                }

                shgs.occurrenceCountFalse = Collections.frequency(shgs.gameBoardValues, false);
                shgs.vetoVoteFalse = Collections.frequency(shgs.vetoChoice, SHPolicyCards.CardType.Fascist);
                if (shgs.occurrenceCountFalse == 5 && shgs.previousOccurrenceCountFalse != shgs.occurrenceCountFalse) {
                    shgs.previousOccurrenceCountFalse = shgs.occurrenceCountFalse;
                    shgs.setGamePhase(Veto);

                } else if (shgs.vetoVoteFalse == 2) {
                    shgs.clearVetoChoice();
                    shgs.setGamePhase(Veto);
                    shgs.failedVoteCounter += 1;
                } else {
                    revealCards(shgs);
                    shgs.occurrenceCountTrue = Collections.frequency(shgs.gameBoardValues, true);
                    shgs.occurrenceCountFalse = Collections.frequency(shgs.gameBoardValues, false);

                    shgs.clearCardChoices();
                    shgs.clearVoteChoices();
                    shgs.clearTeamChoices();

                    endRound(shgs);

                    shgs.roundEnded = true;
                    _endRound(shgs);


                    // Check if the game is over

                    System.out.println("Size of gameboard values: " + shgs.gameBoardValues.size());
                    System.out.println("Occurrence True : " + shgs.occurrenceCountTrue);
                    System.out.println("Occurrence False : " + shgs.occurrenceCountFalse);
                    if (shgs.occurrenceCountTrue == 5) {

                        liberalWin(shgs);
                        if (shgs.occurrenceCountTrue == 5) {
                            System.out.println("GAME ENDED BY SUCCESSFUL MISSIONS");
                        }
                    }

                    if (shgs.occurrenceCountFalse == 6) {
                        fascistWin(shgs);
                        if (shgs.occurrenceCountFalse == 6) {
                            System.out.println("GAME ENDED BY FAILED MISSIONS");
                        }
                    }

                    if (shgs.previousOccurrenceCountFalse != shgs.occurrenceCountFalse && (shgs.occurrenceCountFalse == 4 || shgs.occurrenceCountFalse == 5) && shgs.deceasedFellas.size() < 2) {
                        shgs.previousOccurrenceCountFalse = shgs.occurrenceCountFalse;

                        shgs.setGamePhase(LeaderKillsPlayer);
                    } else if (shgs.previousOccurrenceCountFalse != shgs.occurrenceCountFalse && shgs.occurrenceCountFalse == 3 && shgs.getNPlayers() >= 7) {
                        shgs.previousOccurrenceCountFalse = shgs.occurrenceCountFalse;

                        shgs.setGamePhase(LeaderSelectsLeader);
                    } else if (shgs.previousOccurrenceCountFalse != shgs.occurrenceCountFalse && shgs.occurrenceCountFalse == 2 && (shgs.getNPlayers() == 7 || shgs.getNPlayers() == 8)) {
                        shgs.previousOccurrenceCountFalse = shgs.occurrenceCountFalse;

                        shgs.setGamePhase(LeaderInvestigatesPlayer);
                    } else if (shgs.previousOccurrenceCountFalse != shgs.occurrenceCountFalse && (shgs.occurrenceCountFalse == 1 || shgs.occurrenceCountFalse == 2) && (shgs.getNPlayers() == 9 || shgs.getNPlayers() == 10)) {
                        shgs.previousOccurrenceCountFalse = shgs.occurrenceCountFalse;

                        shgs.setGamePhase(LeaderInvestigatesPlayer);
                    } else if (shgs.previousOccurrenceCountFalse != shgs.occurrenceCountFalse && (shgs.occurrenceCountFalse == 3) && (shgs.getNPlayers() == 5 || shgs.getNPlayers() == 6)) {
                        shgs.previousOccurrenceCountFalse = shgs.occurrenceCountFalse;

                        shgs.setGamePhase(LeaderPeeksTop3Cards);
                    } else {
                        if (shgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
                            shgs.previousChancellor = shgs.chancellorID;
                            shgs.previousLeader = shgs.leaderID;
                            changeLeader(shgs);
                            shgs.failedVoteCounter = 0;
                            shgs.setGamePhase(VotingOnLeader);
                        }
                    }


                }

            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == LeaderKillsPlayer && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                shgs.previousChancellor = shgs.chancellorID;
                shgs.previousLeader = shgs.leaderID;
                shgs.failedVoteCounter = 0;
                endRound(shgs);
                shgs.setGamePhase(VotingOnLeader);
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == LeaderSelectsLeader && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                revealCards(shgs);
                shgs.previousChancellor = shgs.chancellorID;
                shgs.previousLeader = shgs.leaderID;
                shgs.setGamePhase(LeaderSelectsChancellor);
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == LeaderPeeksTop3Cards && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                revealCards(shgs);
                shgs.previousChancellor = shgs.chancellorID;
                shgs.previousLeader = shgs.leaderID;
                shgs.failedVoteCounter = 0;
                shgs.setGamePhase(VotingOnLeader);
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.getGamePhase() == Veto && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                revealCards(shgs);
                shgs.clearVoteChoices();
                shgs.failedVoteCounter = 0;
                shgs.drawnPolicies.clear();
                if (shgs.drawPile.getSize() < 3) {
                    shuffleDiscardsIntoDrawPile(shgs);
                }
                for (int i = 0; i < 3; i++) {
                    SHPolicyCards card = shgs.drawPile.draw();
                    if (card == null) {
                        throw new AssertionError("card is null after shuffle ");
                    }
                    shgs.drawnPolicies.add(card);
                }
                shgs.setGamePhase(LeaderSelectsPolicy);
            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }

        if (shgs.getGamePhase() == LeaderInvestigatesPlayer && haveBeenInLoop == false) {

            int turn = shgs.getTurnCounter();
            System.out.println();
            if ((turn + 1) % shgs.getNPlayers() == 0 && shgs.previousGamePhase == shgs.getGamePhase()) {
                revealCards(shgs);
                shgs.previousChancellor = shgs.chancellorID;
                shgs.previousLeader = shgs.leaderID;
                changeLeader(shgs);
                shgs.failedVoteCounter = 0;
                shgs.setGamePhase(VotingOnLeader);

            } else {
                shgs.previousGamePhase = shgs.getGamePhase();
            }
            haveBeenInLoop = true;
        }
        if (shgs.deceasedFellas.contains(shgs.hitlerID)) {
            liberalWin(shgs);
            System.out.println("Hitler Has Bitten The Bullet");
        }
        //End player turn
        if (shgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING && shgs.roundEnded == false) {
            endPlayerTurn(shgs);
        }
        haveBeenInLoop = false;
        shgs.roundEnded = false;

    }

    void revealCards(SHGameState shgs) {
        if (shgs.getGamePhase() == VotingOnLeader || shgs.getGamePhase() == VotingOnChancellor) {

            ArrayList<SHPlayerCards.CardType> allVotes = new ArrayList<>();
            for (int i = 0; i < shgs.getNPlayers(); i++) {
                for (SHVoting cc : shgs.votingChoice.get(i)) {
                    allVotes.add(cc.cardType);
                }
            }
            System.out.println(allVotes);

            int occurrenceCount = Collections.frequency(allVotes, SHPlayerCards.CardType.Yes);
            System.out.println("Team Vote :  " + allVotes);
            if (allVotes.contains(SHPlayerCards.CardType.Fascist) || allVotes.contains(SHPlayerCards.CardType.Liberal)) {
                throw new AssertionError("Incorrect Type in Team Vote");
            }
            System.out.println("Team Yes Occurrence Count : " + occurrenceCount);
            if (occurrenceCount > allVotes.size() / 2) {
                shgs.voteSuccess = true;
            } else {
                shgs.voteSuccess = false;
                shgs.failedVoteCounter += 1;
            }
        }

        if (shgs.getGamePhase() == LeaderSelectsPolicy) {
            //Adding Excess Card To DiscardPile

            if (shgs.discardPile == null) {

                boolean[] visible = new boolean[17];
                for (int i = 0; i < visible.length; i++) {
                    visible[i] = false;
                }

                shgs.discardPile = new PartialObservableDeck<>("discard pile", -1, visible);
            }
            if (!shgs.final2PolicyChoices.contains(shgs.drawnPolicies.get(0))) {
                shgs.discardPile.add(shgs.drawnPolicies.get(0));
            }
            if (!shgs.final2PolicyChoices.contains(shgs.drawnPolicies.get(1))) {
                shgs.discardPile.add(shgs.drawnPolicies.get(1));
            }
            if (!shgs.final2PolicyChoices.contains(shgs.drawnPolicies.get(2))) {
                shgs.discardPile.add(shgs.drawnPolicies.get(2));
            }
            System.out.println(shgs.discardPile + "discard pile");

        }

        if (shgs.getGamePhase() == ChancellorSelectsPolicy) {
            //Adding Excess Card To DiscardPile
            if (!shgs.finalPolicyChoice.contains(shgs.final2PolicyChoices.get(0))) {
                shgs.discardPile.add(shgs.final2PolicyChoices.get(0));
            }
            if (!shgs.finalPolicyChoice.contains(shgs.final2PolicyChoices.get(1))) {
                shgs.discardPile.add(shgs.final2PolicyChoices.get(1));
            }
            //Adding CardType To GameBoard
            if (shgs.finalPolicyChoice.get(0).cardType == SHPolicyCards.CardType.Fascist) {
                shgs.gameBoardValues.add(false);
            } else {
                shgs.gameBoardValues.add(true);
            }
        }


    }

    public void _startRound(SHGameState shgs) {

    }

    boolean isRoundOver(SHGameState shgs) {

        return true;
    }

    public void _endRound(SHGameState shgs) {
        shgs.roundEnded = true;
    }

    @Override
    protected void endGame(AbstractGameState gs) {
        gs.setGameStatus(CoreConstants.GameResult.GAME_END);
        if (gs.getCoreGameParameters().verbose) {
            System.out.println(Arrays.toString(gs.getPlayerResults()));
        }
    }

    public void changeLeader(SHGameState shgs) {
        int i = shgs.leaderID;
        int placeHolder = shgs.leaderID;
        int counter = 0;
        while ((i == shgs.leaderID || i == shgs.previousChancellor || i == shgs.previousLeader || shgs.deceasedFellas.contains(i) || i == shgs.chancellorID) && counter < 100) {
            if (i == shgs.getNPlayers() - 1) {
                i = 0;
            } else {
                i += 1;
            }
            counter += 1;
            System.out.println(counter + "leaderchange counter");
        }
        shgs.leaderID = i;
        shgs.previousChancellor = placeHolder;
    }


    public void drawSingleCardAndPlay(SHGameState shgs) {
        if (shgs.drawPile.getSize() < 1) {
            shuffleDiscardsIntoDrawPile(shgs);
        }
        SHPolicyCards card = shgs.drawPile.draw();
        if (card.cardType == SHPolicyCards.CardType.Liberal) {
            shgs.gameBoardValues.add(true);
        } else {
            shgs.gameBoardValues.add(false);
        }
    }

    public void shuffleDiscardsIntoDrawPile(SHGameState shgs) {
        for (int i = 0; i < shgs.discardPile.getSize(); i++) {
            shgs.drawPile.add(shgs.discardPile.get(i));
        }
        shgs.clearDiscardPile();
        System.out.println(shgs.discardPile.getSize() + "discard pile after shuffle size");
    }

    public void liberalWin(SHGameState shgs) {
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

    }

    public void fascistWin(SHGameState shgs) {
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
    }

}
