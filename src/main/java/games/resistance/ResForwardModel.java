package games.resistance;

import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.PartialObservableDeck;
import games.resistance.actions.ResAction;
import games.resistance.components.ResPlayerCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

        resgs.playerHandCards = new ArrayList<>();
        //could be wrong
        resgs.gameBoard = Arrays. asList(resp.gameBoard);
        resgs.factions = resp.factions;

        List<PartialObservableDeck<ResPlayerCards>> playerHandCards = new ArrayList<>(firstState.getNPlayers());
        int spyCounter = 0;
        for (int i = 0; i < firstState.getNPlayers(); i++) {
            boolean[] visible = new boolean[firstState.getNPlayers()];
            visible[i] = true;
            //might see sabotage cards
            PartialObservableDeck<ResPlayerCards> playerCards = new PartialObservableDeck<>("Player Cards", visible);
            playerHandCards.add(playerCards);

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
        playerHandCards.get(randomPlayerLeader+1).add(leader);
    }

    /**
     * Calculates the list of currently available actions, possibly depending on the game phase.
     * @return - List of AbstractAction objects.
     */
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        List<AbstractAction> actions = new ArrayList<>();
        // TODO: create action classes for the current player in the given game state and add them to the list. Below just an example that does nothing, remove.
        actions.add(new ResAction());
        return actions;
    }
}
