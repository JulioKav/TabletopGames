package games.resistance.gui;

import core.AbstractGameState;
import core.components.Deck;
import games.pandemic.PandemicGameState;
import games.resistance.ResGameState;
import games.resistance.components.ResPlayerCards;
import gui.views.CardView;
import gui.views.DeckView;
import utilities.ImageIO;

import javax.swing.*;
import java.awt.*;

import static games.resistance.gui.ResGUIManager.ResPlayerCardsHeight;
import static games.resistance.gui.ResGUIManager.ResPlayerCardsWidth;

public class ResBoardView extends JComponent {

    String dataPath;
    ResGameState gameState;
    Image backOfCard;
    ResBoardView gameBoardView;
    public ResBoardView(AbstractGameState gs,String dataPath) {
        gameState = (ResGameState) gs;
        this.dataPath = dataPath;
        this.gameBoardView = new ResBoardView(gameState,dataPath);
        backOfCard = ImageIO.GetInstance().getImage(dataPath + "CardBack.png");
    }

    public void update(ResGameState gameState)
    {
        this.gameState = gameState;

    }
}
