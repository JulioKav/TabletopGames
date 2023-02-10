package games.jaipurskeleton;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Counter;
import core.components.Deck;
import games.jaipurskeleton.actions.SellCards;
import games.jaipurskeleton.actions.TakeCards;
import games.jaipurskeleton.components.JaipurCard;
import games.jaipurskeleton.components.JaipurToken;
//import org.sparkproject.guava.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap;
import utilities.Utils;

import java.util.*;

import static core.CoreConstants.GameResult.*;
import static games.jaipurskeleton.components.JaipurCard.GoodType.*;
import static scala.Console.print;

/**
 * Jaipur rules: <a href="https://www.fgbradleys.com/rules/rules2/Jaipur-rules.pdf">pdf here</a>
 */
public class JaipurForwardModel extends StandardForwardModel {

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
        JaipurGameState gs = (JaipurGameState) firstState;
        JaipurParameters jp = (JaipurParameters) firstState.getGameParameters();

        // Initialize variables
        gs.market = new HashMap<>();
        for (JaipurCard.GoodType gt: JaipurCard.GoodType.values()) {
            // 5 cards in the market
            gs.market.put(gt, new Counter(0, 0, (( JaipurParameters ) gs . getGameParameters () ) . nRoundsWinForGameWin, "Market: " + gt));
        }

        gs.drawDeck = new Deck<>("Draw deck", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);
        gs.playerHands = new ArrayList<>();
        gs.playerHerds = new ArrayList<>();
        gs.nGoodTokensSold = new Counter(0, 0, JaipurCard.GoodType.values().length, "N Good Tokens Fully Sold");
        gs.goodTokens = new HashMap<>();
        gs.bonusTokens = new HashMap<>();

        // Initialize player scores, rounds won trackers, and other player-specific variables
        gs.playerScores = new ArrayList<>();
        gs.playerNRoundsWon = new ArrayList<>();
        gs.playerNGoodTokens = new ArrayList<>();
        gs.playerNBonusTokens = new ArrayList<>();
        for (int i = 0; i < gs.getNPlayers(); i++) {
            gs.playerScores.add(new Counter(0, 0, Integer.MAX_VALUE, "Player " + i + " score"));
            gs.playerNRoundsWon.add(new Counter(0, 0, Integer.MAX_VALUE, "Player " + i + " n rounds won"));
            gs.playerNGoodTokens.add(new Counter(0, 0, Integer.MAX_VALUE, "Player " + i + " n good tokens"));
            gs.playerNBonusTokens.add(new Counter(0, 0, Integer.MAX_VALUE, "Player " + i + " n bonus tokens"));

            // Create herds, maximum 11 camels in the game
            gs.playerHerds.add(new Counter(0, 0, 11, "Player " + i + " herd"));

            Map<JaipurCard.GoodType, Counter> playerHand = new HashMap<>();
            for (JaipurCard.GoodType gt: JaipurCard.GoodType.values()) {
                if (gt != JaipurCard.GoodType.Camel) {
                    // Hand limit of 7
                    playerHand.put(gt, new Counter(0, 0,  (( JaipurParameters ) gs . getGameParameters () ) . nRoundsWinForGameWin, "Player " + i + " hand: " + gt));
                }
            }
            gs.playerHands.add(playerHand);
        }

        // Set up the first round
        setupRound(gs, jp);
    }

    private void setupRound(JaipurGameState gs, JaipurParameters jp) {
        Random r = new Random(jp.getRandomSeed());

        // Market initialisation
        // Place 3 camel cards in the market
        for (JaipurCard.GoodType gt: JaipurCard.GoodType.values()) {
            if (gt == JaipurCard.GoodType.Camel) {
                gs.market.get(gt).setValue((( JaipurParameters ) gs . getGameParameters () ) . getStartingCamels());
            } else {
                gs.market.get(gt).setValue(0);
            }
        }

        // Create deck of cards
        gs.drawDeck.clear();
        for (int i = 0; i < 6; i++) {  // 6 Diamond cards
            JaipurCard card = new JaipurCard(Diamonds);
            gs.drawDeck.add(card);
        }
        for (int i = 0; i < 6; i++) {  // 6 Gold cards
            JaipurCard card = new JaipurCard(Gold);
            gs.drawDeck.add(card);
        }
        for (int i = 0; i < 6; i++) {  // 6 Silver cards
            JaipurCard card = new JaipurCard(Silver);
            gs.drawDeck.add(card);
        }
        for (int i = 0; i < 8; i++) {  // 8 Cloth cards
            JaipurCard card = new JaipurCard(JaipurCard.GoodType.Cloth);
            gs.drawDeck.add(card);
        }
        for (int i = 0; i < 8; i++) {  // 8 Spice cards
            JaipurCard card = new JaipurCard(JaipurCard.GoodType.Spice);
            gs.drawDeck.add(card);
        }
        for (int i = 0; i < 10; i++) {  // 10 Leather cards
            JaipurCard card = new JaipurCard(JaipurCard.GoodType.Leather);
            gs.drawDeck.add(card);
        }
        for (int i = 0; i < (( JaipurParameters ) gs . getGameParameters () ) . camelsInDeck; i++) {  // 11 Camel cards, - 3 already in the market
            JaipurCard card = new JaipurCard(JaipurCard.GoodType.Camel);
            gs.drawDeck.add(card);
        }
        gs.drawDeck.shuffle(r);

        // Deal N cards to each player
        for (int i = 0; i < gs.getNPlayers(); i++) {
            Map<JaipurCard.GoodType, Counter> playerHand = gs.playerHands.get(i);

            // First, reset
            gs.playerHerds.get(i).setValue(0);
            for (JaipurCard.GoodType gt: JaipurCard.GoodType.values()) {
                if (gt != JaipurCard.GoodType.Camel) {
                    playerHand.get(gt).setValue(0);
                }
            }

            // Deal cards
            for (int j = 0; j < (( JaipurParameters ) gs . getGameParameters () ) . startingHandSize; j++) {  // 5 cards in hand
                JaipurCard card = gs.drawDeck.draw();

                // If camel, it goes into the herd instead
                if (card.goodType == JaipurCard.GoodType.Camel) {
                    gs.playerHerds.get(i).increment();
                } else {
                    // Otherwise, into the player's hand
                    playerHand.get(card.goodType).increment();
                }
            }
        }

        // Take first 2 cards from the deck and place them face up in the market.
        for (int i = 0; i < 2; i++) {
            JaipurCard card = gs.drawDeck.draw();
            gs.market.get(card.goodType).increment();
        }

        // Initialize tokens
        gs.nGoodTokensSold.setValue(0);
        gs.goodTokens.clear();
        gs.bonusTokens.clear();

        // Initialize the good tokens
        for ( JaipurCard . GoodType type : jp . goodTokensProgression . keySet () ) {
             Integer [] progression = jp . goodTokensProgression . get ( type ) ;
             Deck < JaipurToken > tokenDeck = new Deck < >( " Good tokens " + type ,
                    CoreConstants . VisibilityMode . VISIBLE_TO_ALL ) ;
             for (int p : progression ) {
                 tokenDeck . add (new JaipurToken ( type , p ) ) ;
                 }
             gs . goodTokens . put ( type , tokenDeck ) ;
             }

        // Initialize the bonus tokens
        for (int nSold: jp.bonusTokensAvailable.keySet()) {
            Integer[] values = jp.bonusTokensAvailable.get(nSold);
            Deck<JaipurToken> tokenDeck = new Deck<>("Bonus tokens " + nSold, CoreConstants.VisibilityMode.HIDDEN_TO_ALL);
            for (int v: values) {
                tokenDeck.add(new JaipurToken(v));
            }
            // Shuffle
            tokenDeck.shuffle(r);
            gs.bonusTokens.put(nSold, tokenDeck);
        }

        // Reset player-specific variables that don't persist between rounds
        for (int i = 0; i < gs.getNPlayers(); i++) {
            gs.playerScores.get(i).setValue(0);
            gs.playerNGoodTokens.get(i).setValue(0);
            gs.playerNBonusTokens.get(i).setValue(0);
        }

        // First player
        gs.setFirstPlayer(0);
    }

    /**
     * Calculates the list of currently available actions, possibly depending on the game phase.
     * @return - List of AbstractAction objects.
     */
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        List<AbstractAction> actions = new ArrayList<>();
        JaipurGameState jgs = (JaipurGameState) gameState;
        JaipurParameters jp = (JaipurParameters) gameState.getGameParameters();
        int currentPlayer = gameState.getCurrentPlayer();
        Map<JaipurCard.GoodType, Counter> playerHand = jgs.playerHands.get(currentPlayer);

        // Can sell cards from hand
        // TODO: Follow lab 1 instructions (Section 3.1) to fill in this method here.
        for ( JaipurCard . GoodType gt : playerHand . keySet () ) {
             if ( playerHand . get ( gt ) . getValue () >= jp . goodNCardsMinimumSell . get ( gt ) )
            {
                 // Can sell this good type ! We can choose any number of cards to
                //sell of this type between minimum and how many we have
                 for (int n = jp . goodNCardsMinimumSell . get ( gt ) ; n <= playerHand . get (
                    gt ) . getValue () ; n ++) {
                 actions . add (new SellCards( gt , n ) ) ;
                 }
                 }
             }

        // Can take cards from the market, respecting hand limit
        // Option C: Take all camels, they don't count towards hand limit
        // TODO 1: Check how many camel cards are in the market. If more than 0, construct one TakeCards action object and add it to the `actions` ArrayList. (The `howManyPerTypeGiveFromHand` argument should be null)

        if (jgs.getMarket().get(Camel) != null && jgs.getMarket().get(Camel).getValue() != 0)
        {
            Map<JaipurCard.GoodType, Integer> camelsTakenMap = new HashMap<JaipurCard.GoodType, Integer>() {{
                put(Camel, jgs.getMarket().get(Camel).getValue());

            }};

            ImmutableMap<JaipurCard.GoodType, Integer> camelsTaken =
                    ImmutableMap.<JaipurCard.GoodType, Integer>builder()
                            .putAll(camelsTakenMap)
                            .build();

            actions . add (new TakeCards(camelsTaken,null,currentPlayer ));
        }
        int nCardsInHand = 0;
        for (JaipurCard.GoodType gt: playerHand.keySet()) {
            nCardsInHand += playerHand.get(gt).getValue();
        }

        // Check hand limit for taking non-camel cards in hand
        if (nCardsInHand <  (( JaipurParameters ) gameState . getGameParameters () ) . handLimit) {
            // Option B: Take a single (non-camel) card from the market
            // TODO 2: For each good type in the market, if there is at least 1 of that type (which is not a Camel), construct one TakeCards action object to take 1 of that type from the market, and add it to the `actions` ArrayList. (The `howManyPerTypeGiveFromHand` argument should be null)

            for (int i = 0; i < JaipurCard.GoodType.values().length; i++)
            {
                JaipurCard.GoodType currentCard = JaipurCard.GoodType.values()[i];

                if (jgs.getMarket().get(JaipurCard.GoodType.values()[i]) != null && jgs.getMarket().get(JaipurCard.GoodType.values()[i]).getValue()  >= 1 && JaipurCard.GoodType.values()[i] != Camel)
                {
                    Map<JaipurCard.GoodType, Integer> cardTakenMap = new HashMap<JaipurCard.GoodType, Integer>() {{
                        put(currentCard, 1);

                    }};

                    ImmutableMap<JaipurCard.GoodType, Integer> cardTaken =
                            ImmutableMap.<JaipurCard.GoodType, Integer>builder()
                                    .putAll(cardTakenMap)
                                    .build();

                    actions . add (new TakeCards(cardTaken,null,currentPlayer ));
                }
            }
            // Option A: Take several (non-camel) cards and replenish with cards of different types from hand
            // TODO (Advanced, bonus, optional): Calculate legal option A variations
            ///
            int numberDrawn = ((JaipurParameters) gameState.getGameParameters()).handLimit;
            List<Integer> SeveralCardsTaken = new ArrayList<>();
            List<Integer> SeveralCardsGiven = new ArrayList<>();
            //setup take cards and give cards arrays

                if (jgs.getMarket().get(Diamonds) != null && jgs.getMarket().get(Diamonds).getValue()!= 0) {
                    for (int i = 0; i < jgs.getMarket().get(Diamonds).getValue(); i++) {
                        SeveralCardsTaken.add(0);
                    }
                }
                if (jgs.getMarket().get(Gold) != null && jgs.getMarket().get(Gold).getValue() != 0) {
                    for (int i = 0; i < jgs.getMarket().get(Gold).getValue(); i++) {
                        SeveralCardsTaken.add(1);
                    }
                }
                if (jgs.getMarket().get(Silver) != null && jgs.getMarket().get(Silver).getValue() != 0) {
                    for (int i = 0; i < jgs.getMarket().get(Silver).getValue(); i++) {
                        SeveralCardsTaken.add(2);
                    }
                }
                if (jgs.getMarket().get(Cloth) != null && jgs.getMarket().get(Cloth).getValue() != 0) {
                    for (int i = 0; i < jgs.getMarket().get(Cloth).getValue(); i++) {
                        SeveralCardsTaken.add(3);
                    }
                }
                if (jgs.getMarket().get(Spice) != null && jgs.getMarket().get(Spice).getValue() != 0) {
                    for (int i = 0; i < jgs.getMarket().get(Spice).getValue(); i++) {
                        SeveralCardsTaken.add(4);
                    }
                }
                if (jgs.getMarket().get(Leather) != null && jgs.getMarket().get(Leather).getValue() != 0) {
                    for (int i = 0; i < jgs.getMarket().get(Leather).getValue(); i++) {
                        SeveralCardsTaken.add(5);
                    }
                }



            int[] SeveralCardsTakenArray = SeveralCardsTaken.stream().mapToInt(i->i).toArray();


                if (jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Diamonds) != null && jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Diamonds).getValue() > 0) {
                    for (int i = 0; i < jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Diamonds).getValue(); i++) {
                        SeveralCardsGiven.add(0);
                    }
                }
                if (jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Gold) != null && jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Gold).getValue() > 0) {
                    for (int i = 0; i < jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Gold).getValue(); i++) {
                        SeveralCardsGiven.add(1);
                    }
                }
                if (jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Silver) != null && jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Silver).getValue() > 0) {
                    for (int i = 0; i < jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Silver).getValue(); i++) {
                        SeveralCardsGiven.add(2);
                    }
                }
                if (jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Cloth) != null && jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Cloth).getValue() > 0) {
                    for (int i = 0; i < jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Cloth).getValue(); i++) {
                        SeveralCardsGiven.add(3);
                    }
                }
                if (jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Spice) != null && jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Spice).getValue() > 0) {
                    for (int i = 0; i < jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Spice).getValue(); i++) {
                        SeveralCardsGiven.add(4);
                    }
                }
                if (jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Leather) != null && jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Leather).getValue() > 0) {
                    for (int i = 0; i < jgs.getPlayerHands().get(jgs.getCurrentPlayer()).get(Leather).getValue(); i++) {
                        SeveralCardsGiven.add(5);
                    }
                }


            int[] SeveralCardsGivenArray = SeveralCardsGiven.stream().mapToInt(i->i).toArray();

            //select number drawn



            for (int j = 1; j < numberDrawn; j++) {
                //create combinations for drawn
                ArrayList<int[]> chosenMarket = Utils.generateCombinations(SeveralCardsTakenArray, j);

                for (int n = 0; n < chosenMarket.size(); n++) {
                    int diamondCounter = 0;
                    int goldCounter = 0;
                    int silverCounter = 0;
                    int clothCounter = 0;
                    int spiceCounter = 0;
                    int leatherCounter = 0;
                    Map<JaipurCard.GoodType, Integer> severalTakenMap = new HashMap<JaipurCard.GoodType, Integer>() {{}};
                    for (int m = 0; m < chosenMarket.get(n).length; m++) {


                        if (chosenMarket.get(n)[m] == 0) {
                            ++diamondCounter;
                        }
                        if (chosenMarket.get(n)[m] == 1) {
                            ++goldCounter;
                        }
                        if (chosenMarket.get(n)[m] == 2) {
                            ++silverCounter;
                        }
                        if (chosenMarket.get(n)[m] == 3) {
                            ++clothCounter;
                        }
                        if (chosenMarket.get(n)[m] == 4) {
                            ++spiceCounter;
                        }
                        if (chosenMarket.get(n)[m] == 5) {
                            ++leatherCounter;
                        }





                    }
                    if(diamondCounter != 0) {
                        severalTakenMap.put(Diamonds, diamondCounter);
                    }if(goldCounter != 0) {
                        severalTakenMap.put(Gold, goldCounter);
                    }if(silverCounter != 0) {
                        severalTakenMap.put(Silver, silverCounter);
                    }if(clothCounter != 0) {
                        severalTakenMap.put(Cloth, clothCounter);
                    }if(spiceCounter != 0) {
                        severalTakenMap.put(Spice, spiceCounter);
                    }if(leatherCounter != 0) {
                        severalTakenMap.put(Leather, leatherCounter);
                    }

                    ImmutableMap<JaipurCard.GoodType, Integer> severalTakenMapImm =
                            ImmutableMap.<JaipurCard.GoodType, Integer>builder()
                                    .putAll(severalTakenMap)
                                    .build();


                    ArrayList<int[]> givenMarket = Utils.generateCombinations(SeveralCardsGivenArray, j);
                    //create combinations for given for each drawn combination
                    for (int k = 0; k < givenMarket.size(); k++) {
                        int diamondCounterGiven = 0;
                        int goldCounterGiven = 0;
                        int silverCounterGiven = 0;
                        int clothCounterGiven = 0;
                        int spiceCounterGiven = 0;
                        int leatherCounterGiven = 0;
                        Map<JaipurCard.GoodType, Integer> severalGivenMap = new HashMap<JaipurCard.GoodType, Integer>() {{}};
                        for (int m = 0; m < givenMarket.get(k).length; m++) {
                            if (givenMarket.get(k)[m] == 0) {
                                ++diamondCounterGiven;
                            }
                            if (givenMarket.get(k)[m] == 1) {
                                ++goldCounterGiven;
                            }
                            if (givenMarket.get(k)[m] == 2) {
                                ++silverCounterGiven;
                            }
                            if (givenMarket.get(k)[m] == 3) {
                                ++clothCounterGiven;
                            }
                            if (givenMarket.get(k)[m] == 4) {
                                ++spiceCounterGiven;
                            }
                            if (givenMarket.get(k)[m] == 5) {
                                ++leatherCounterGiven;
                            }
                        }
                        if(diamondCounterGiven != 0) {
                            severalGivenMap.put(Diamonds, diamondCounterGiven);
                        }if(goldCounterGiven != 0) {
                            severalGivenMap.put(Gold, goldCounterGiven);
                        }if(silverCounterGiven != 0) {
                            severalGivenMap.put(Silver, silverCounterGiven);
                        }if(clothCounterGiven != 0) {
                            severalGivenMap.put(Cloth, clothCounterGiven);
                        }if(spiceCounterGiven != 0) {
                            severalGivenMap.put(Spice, spiceCounterGiven);
                        }if(leatherCounterGiven != 0) {
                            severalGivenMap.put(Leather, leatherCounterGiven);
                        }




                        ImmutableMap<JaipurCard.GoodType, Integer> severalGivenMapImm =
                                ImmutableMap.<JaipurCard.GoodType, Integer>builder()
                                        .putAll(severalGivenMap)
                                        .build();

                        actions.add(new TakeCards(severalTakenMapImm, severalGivenMapImm, currentPlayer));

                    }

                }


            }
        }

        return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction actionTaken) {
        if (currentState.isActionInProgress()) return;

        // Check game end
        JaipurGameState jgs = (JaipurGameState) currentState;
        JaipurParameters jp = (JaipurParameters) currentState.getGameParameters();
        if (actionTaken instanceof TakeCards && ((TakeCards)actionTaken).isTriggerRoundEnd() || jgs.nGoodTokensSold.getValue() == jp.nGoodTokensEmptyRoundEnd) {
            // Round end!
            endRound(currentState);

            // Check most camels, add extra points
            int maxCamels = 0;
            HashSet<Integer> pIdMaxCamels = new HashSet<>();
            for (int i = 0; i < jgs.getNPlayers(); i++) {
                if (jgs.playerHerds.get(i).getValue() > maxCamels) {
                    maxCamels = jgs.playerHerds.get(i).getValue();
                    pIdMaxCamels.clear();
                    pIdMaxCamels.add(i);
                } else if (jgs.playerHerds.get(i).getValue() == maxCamels) {
                    pIdMaxCamels.add(i);
                }
            }
            if (pIdMaxCamels.size() == 1) {
                // Exactly 1 player has most camels, they get bonus. If tied, nobody gets bonus.
                int player = pIdMaxCamels.iterator().next();
                jgs.playerScores.get(player).increment(jp.nPointsMostCamels);
                if (jgs.getCoreGameParameters().recordEventHistory) {
                    jgs.recordHistory("Player " + player + " earns the " + jp.nPointsMostCamels + " Camel bonus points (" + maxCamels + " camels)");
                }
            }

            // Decide winner of round
            int roundsWon = 0;
            int winner = -1;
            StringBuilder scores = new StringBuilder();
            for (int p = 0; p < jgs.getNPlayers(); p++) {
                int o = jgs.getOrdinalPosition(p);
                scores.append(p).append(":").append(jgs.playerScores.get(p).getValue());
                if (o == 1) {
                    jgs.playerNRoundsWon.get(p).increment();
                    roundsWon = jgs.playerNRoundsWon.get(p).getValue();
                    winner = p;
                    scores.append(" (win)");
                }
                scores.append(", ");
            }
            scores.append(")");
            scores = new StringBuilder(scores.toString().replace(", )", ""));
            if (jgs.getCoreGameParameters().recordEventHistory) {
                jgs.recordHistory("Round scores: " + scores);
            }

            if (roundsWon == ((JaipurParameters) jgs . getGameParameters () ) . nRoundsWinForGameWin) {
                // Game over, this player won
                jgs.setGameStatus(CoreConstants.GameResult.GAME_END);
                for (int i = 0; i < jgs.getNPlayers(); i++) {
                    if (i == winner) {
                        jgs.setPlayerResult(WIN, i);
                    } else {
                        jgs.setPlayerResult(LOSE, i);
                    }
                }
                return;
            }

            // Reset and set up for next round
            setupRound(jgs, jp);

        } else {
            // It's next player's turn
            endPlayerTurn(jgs);
        }
    }
}
