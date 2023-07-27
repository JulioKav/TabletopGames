package games.secrethitler.gui;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import games.pandemic.gui.PandemicBoardView;
import games.secrethitler.SHGameState;
import games.secrethitler.SHParameters;
import games.secrethitler.components.SHPlayerCards;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;
import utilities.ImageIO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collections;


/// Code Was Taken and adapted from SushiGo! Assets were taken from the secret hitler board game.
public class SHGUIManager extends AbstractGUIManager {
    // Settings for display areas

    final static int playerAreaWidth = 250;
    final static int playerAreaHeight = 130;
    final static int SHPlayerCardsWidth = 60;
    final static int SHPlayerCardsHeight = 85;
    protected JLabel fascistPolicies = new JLabel("Amount Of Fascist Policies Played :" + 0 );
    protected JLabel liberalPolicies = new JLabel("Amount Of Liberal Policies Played :" + 0 );
    protected JLabel failedVoteCounter = new JLabel("Failed Vote Counter :" + 0 );

    SHBoardView boardView;
    // List of player hand views
    SHPlayerView[] playerHands;

    // Currently active player
    int activePlayer = -1;

    // Border highlight of active player
    Border highlightActive = BorderFactory.createLineBorder(new Color(47, 132, 220), 3);
    Border[] playerViewBorders;
    public SHGUIManager(GamePanel parent, Game game, ActionController ac, int humanID) {
        super(parent, game, ac, humanID);
        if (game != null) {
            AbstractGameState gameState = game.getGameState();
            if (gameState != null) {
                //Initialise active player
                activePlayer = gameState.getCurrentPlayer();

                // Find required size of window
                int nPlayers = gameState.getNPlayers();
                int nHorizAreas = 1 + nPlayers;
                double nVertAreas = 3.5;
                this.width = playerAreaWidth * nHorizAreas;
                this.height = (int) (playerAreaHeight * nVertAreas);

                SHGameState parsedGameState = (SHGameState) gameState;
                SHParameters parameters = (SHParameters) gameState.getGameParameters();

                // Create main game area that will hold all game views
                playerHands = new SHPlayerView[nPlayers];
                playerViewBorders = new Border[nPlayers];

                JPanel mainGameArea = new JPanel();
                mainGameArea.setLayout(new BorderLayout());


                // Player hands go on the edges
                String[] locations = new String[]{BorderLayout.NORTH, BorderLayout.SOUTH};
                JPanel[] sides = new JPanel[]{new JPanel(), new JPanel(), new JPanel(), new JPanel()};
                int next = 0;
                for (int i = 0; i < nPlayers; i++) {
                    SHPlayerView playerHand = new SHPlayerView(parsedGameState.getPlayerHandCards().get(i), i, humanID, parameters.getDataPath());
                    // Get agent name
                    String[] split = game.getPlayers().get(i).getClass().toString().split("\\.");
                    String agentName = split[split.length - 1];

                    // Create border, layouts and keep track of this view
                    TitledBorder title = BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Player " + i + " [" + agentName + "]",
                            TitledBorder.CENTER, TitledBorder.BELOW_BOTTOM);
                    playerViewBorders[i] = title;
                    playerHand.setBorder(title);

                    sides[next].add(playerHand);
                    sides[next].setLayout(new GridBagLayout());
                    next = (next + 1) % (locations.length);
                    playerHands[i] = playerHand;
                    System.out.println(playerHand.playerHandView);
                }


                for (int i = 0; i < locations.length; i++) {
                    mainGameArea.add(sides[i], locations[i]);
                }


                JPanel centerArea = new JPanel();

                centerArea.setLayout(new BoxLayout(centerArea, BoxLayout.Y_AXIS));
                Image backgroundImage;
                if(gameState.getNPlayers() < 7){ backgroundImage = ImageIO.GetInstance().getImage("data/secrethitler/lib+fasc5man.png");}
                else if (gameState.getNPlayers() > 6 && gameState.getNPlayers() < 9){ backgroundImage = ImageIO.GetInstance().getImage("data/secrethitler/7manfascist.png");}
                else{ backgroundImage = ImageIO.GetInstance().getImage("data/secrethitler/10manfascist.png");}

                int newWidth = backgroundImage.getWidth(null) / 2; // Replace 2 with the desired scale factor
                int newHeight = backgroundImage.getHeight(null) /2; // Replace 2 with the desired scale factor
                backgroundImage = backgroundImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                SHBoardView jp = new SHBoardView(backgroundImage);
                jp.setLayout(new GridBagLayout());
                jp.add(centerArea);
                mainGameArea.add(jp, BorderLayout.CENTER);

                // Top area will show state information
                JPanel infoPanel = createGameStateInfoPanel("Secret Hitler", gameState, width, defaultInfoPanelHeight);
                // Bottom area will show actions available
                JComponent actionPanel = createActionPanel(new IScreenHighlight[0], width, defaultActionPanelHeight, false, true, null);

                //missionSuccessText = createGameStateInfoPanel("ROund", gameState, width, 100);
                // Add all views to frame
                parent.setLayout(new BorderLayout());
                parent.add(mainGameArea, BorderLayout.CENTER);
                //parent.add(boardView, BorderLayout.CENTER);
                parent.add(infoPanel, BorderLayout.NORTH);
                parent.add(actionPanel, BorderLayout.SOUTH);
                parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + 20));
            }

        }
        parent.revalidate();
        parent.setVisible(true);
        parent.repaint();
    }

    @Override
    protected JPanel createGameStateInfoPanel(String gameTitle, AbstractGameState gameState, int width, int height) {
        JPanel gameInfo = new JPanel();
        gameInfo.setOpaque(false);
        gameInfo.setLayout(new BoxLayout(gameInfo, BoxLayout.Y_AXIS));
        gameInfo.add(new JLabel("<html><h1>" + gameTitle + "</h1></html>"));

        SHGameState shgs = (SHGameState) gameState;

        updateGameStateInfo(gameState);

        gameInfo.add(fascistPolicies);
        gameInfo.add(liberalPolicies);
        gameInfo.add(failedVoteCounter);
        gameInfo.add(gamePhase);
        gameInfo.add(turn);
        gameInfo.add(currentPlayer);
        gameInfo.add(gameStatus);

        gameInfo.setPreferredSize(new Dimension(width/2 - 10, height));

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new FlowLayout());
        wrapper.add(gameInfo);

        historyInfo.setPreferredSize(new Dimension(width/2 - 10, height));
        historyContainer = new JScrollPane(historyInfo);
        historyContainer.setPreferredSize(new Dimension(width/2 - 25, height));
        wrapper.add(historyContainer);
        historyInfo.setOpaque(false);
        historyContainer.setOpaque(false);
        historyContainer.getViewport().setBackground(new Color(43, 108, 25, 111));
//        historyContainer.getViewport().setOpaque(false);
        historyInfo.setEditable(false);
        return wrapper;
    }
    @Override
    public int getMaxActionSpace() {
        return 15;
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        if (gameState != null) {
            if (gameState.getCurrentPlayer() != activePlayer) {
                playerHands[activePlayer].playerHandView.setCardHighlight(-1);
                activePlayer = gameState.getCurrentPlayer();
            }


            // Update decks and visibility
            SHGameState parsedGameState = (SHGameState) gameState;

            //missionSuccessText = createGameStateInfoPanel("Size of Mission Team needed : " + parsedGameState.gameBoard.getMissionSuccessValues()[parsedGameState.getRoundCounter()], gameState, width, 100);
            for (int i = 0; i < gameState.getNPlayers(); i++) {
                playerHands[i].update(parsedGameState);
                if(((SHGameState) gameState).getPlayerHandCards().get(gameState.getCurrentPlayer()).get(2).cardType == SHPlayerCards.CardType.Fascist )
                {
                    if(gameState.getCurrentPlayer() == ((SHGameState) gameState).getHitlerID() && gameState.getNPlayers() < 7 )
                    {playerHands[i].playerHandView.setFront(true);}
                    else
                    {
                        if (i == gameState.getCurrentPlayer()
                                || i == humanPlayerId) {
                            playerHands[i].playerHandView.setFront(true);
                            playerHands[i].setFocusable(true);
                        } else {
                            playerHands[i].playerHandView.setFront(false);
                        }

                    }
                    if(gameState.getCurrentPlayer() != ((SHGameState) gameState).getHitlerID()){playerHands[i].playerHandView.setFront(true);}
                }
                else{
                    if (i == gameState.getCurrentPlayer()
                            || i == humanPlayerId) {
                        playerHands[i].playerHandView.setFront(true);
                        playerHands[i].setFocusable(true);
                    } else {
                        playerHands[i].playerHandView.setFront(false);
                    }}

                // Highlight active player
                if (i == gameState.getCurrentPlayer()) {
                    Border compound = BorderFactory.createCompoundBorder(
                            highlightActive, playerViewBorders[i]);
                    playerHands[i].setBorder(compound);
                } else {
                    playerHands[i].setBorder(playerViewBorders[i]);
                }
            }

        }
    }

    protected void updateGameStateInfo(AbstractGameState gameState) {
        super.updateGameStateInfo(gameState);
        SHGameState shgs = (SHGameState) gameState;

        fascistPolicies.setText( "Amount Of Fascist Policies Played :" + Collections.frequency(shgs.gameBoardValues,false) );
        liberalPolicies.setText( "Amount Of Liberal Policies Played :" + Collections.frequency(shgs.gameBoardValues,true) );
        failedVoteCounter.setText("Failed Vote Counter :" + shgs.getFailedVoteCounter());
    }
}
