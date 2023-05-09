package games.resistance;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Deck;
import core.components.PartialObservableDeck;
import games.resistance.actions.ResAction;
import games.resistance.actions.ResVoting;
import games.resistance.components.ResPlayerCards;
import games.sushigo.SGGameState;
import games.sushigo.SGParameters;
import games.sushigo.actions.ChooseCard;
import games.sushigo.cards.SGCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static games.resistance.ResGameState.ResGamePhase.TeamSelection;
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

        resgs.votingChoice = new ArrayList<>(firstState.getNPlayers());

        resgs.playerHandCards = new ArrayList<>(resgs.getNPlayers());
        //could be wrong
        resgs.gameBoard = resp.getPlayerBoard(resgs.getNPlayers());
        if(resgs.gameBoard == null)
        {throw new AssertionError("GameBoard shouldn't be null");};
        resgs.factions = resp.factions;
        
        

        //List<PartialObservableDeck<ResPlayerCards>> playerHandCards = new ArrayList<>(firstState.getNPlayers());
        int spyCounter = 0;
        for (int i = 0; i < firstState.getNPlayers(); i++) {
            resgs.votingChoice.add(new ArrayList<>());
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
            //Add Voting Cards
            ResPlayerCards yes = new ResPlayerCards(ResPlayerCards.CardType.Yes);
            yes.setOwnerId(i);
            playerCards.add(yes);

            ResPlayerCards no = new ResPlayerCards(ResPlayerCards.CardType.No);
            no.setOwnerId(i);
            playerCards.add(no);

        }

        //Adding leader card
        ResPlayerCards leader = new ResPlayerCards(ResPlayerCards.CardType.LEADER);
        int randomPlayerLeader = rnd.nextInt(resgs.getNPlayers());
        leader.setOwnerId(randomPlayerLeader);
        resgs.playerHandCards.get(randomPlayerLeader).add(leader);
        // DOUBLE CHECK LAST PLAYER CAN GET LEADER

        //System.out.println(resgs.playerHandCards.get(0));
        resgs.setGamePhase(TeamSelection);
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
        for (int i = 0; i < currentPlayerHand.getSize(); i++) {
            // All players can do is choose a card in hand to play.
            actions.add(new ResVoting(currentPlayer, i));
        }
        return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        if (currentState.isActionInProgress())
            return; // we only want to trigger this processing if an extended action sequence (i.e. Chopsticks) has been terminated
        ResGameState resgs = (ResGameState) currentState;

        // Check if all players made their choice
        int turn = resgs.getTurnCounter();
        if ((turn + 1) % resgs.getNPlayers() == 0) {
            // They did! Reveal all cards at once. Process card reveal rules.
            revealCards(resgs);

            // Check if the round is over
            if (isRoundOver(resgs)) {
                // It is! Process end of round rules.
                endRound(resgs);
                _endRound(resgs);

                // Clear card choices from this turn, ready for the next simultaneous choice.
                resgs.clearCardChoices();

                // Check if the game is over
                if (resgs.getRoundCounter() >= ((ResParameters)resgs.getGameParameters()).) {
                    // It is! Process end of game rules.nRounds
                    for (ResPlayerCards.CardType type: values()) {
                        type.onGameEnd(resgs);
                    }
                    // Decide winner
                    endGame(resgs);
                    return;
                }

                _startRound(resgs);
                return;
            } else {
                // Round is not over, keep going. Rotate hands for next player turns.
                rotatePlayerHands(resgs);

                // Clear card choices from this turn, ready for the next simultaneous choice.
                resgs.clearCardChoices();
            }
        }

        // End player turn
        if (resgs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
            endPlayerTurn(resgs);
        }
    }

    void revealCards(ResGameState gs) {
        for (int i = 0; i < gs.getNPlayers(); i++) {
            Deck<SGCard> hand = gs.getPlayerHands().get(i);
            for (ChooseCard cc: gs.cardChoices.get(i)) {
                SGCard cardToReveal = hand.get(cc.cardIdx);

                hand.remove(cardToReveal);
                gs.playedCards.get(i).add(cardToReveal);
                gs.playedCardTypes[i].get(cardToReveal.type).increment(cardToReveal.count);
                gs.playedCardTypesAllGame[i].get(cardToReveal.type).increment(cardToReveal.count);

                //Add points to player
                cardToReveal.type.onReveal(gs, i);
            }
        }
    }

    public void _startRound(ResGameState resgs) {
        //Draw new hands for players
        for (int i = 0; i < resgs.getNPlayers(); i++){
            for (int j = 0; j < resgs.nCardsInHand; j++)
            {
                if (resgs.drawPile.getSize() == 0) {
                    // Reshuffle discard into draw pile
                    resgs.drawPile.add(resgs.discardPile);
                    resgs.discardPile.clear();
                    resgs.drawPile.shuffle(new Random(resgs.getGameParameters().getRandomSeed()));
                }
                resgs.playerHands.get(i).add(resgs.drawPile.draw());
            }
            resgs.deckRotations = 0;
        }
    }

    boolean isRoundOver(ResGameState resgs) {
        //
        for (int i = 0; i < resgs.getPlayerHands().size(); i++) {
            if (resgs.getPlayerHands().get(i).getSize() > 0) return false;
        }
        return true;
    }
    public void _endRound(ResGameState resgs) {
        // Apply card end of round rules
        for (ResPlayerCards.CardType type: ResPlayerCards.CardType.values()) {
            type.onRoundEnd(resgs);
        }

        // Clear played hands if they get discarded between rounds, they go in the discard pile
        for (int i = 0; i < resgs.getNPlayers(); i++) {
            Deck<SGCard> cardsToKeep = resgs.playedCards.get(i).copy();
            cardsToKeep.clear();
            for (SGCard card : resgs.playedCards.get(i).getComponents()) {
                if (card.type.isDiscardedBetweenRounds()) {
                    resgs.discardPile.add(card);
                    resgs.playedCardTypes[i].get(card.type).setValue(0);
                } else {
                    cardsToKeep.add(card);
                }
            }
            resgs.playedCards.get(i).clear();
            resgs.playedCards.get(i).add(cardsToKeep);
        }
    }
}
