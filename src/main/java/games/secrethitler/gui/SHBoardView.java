package games.secrethitler.gui;

import core.AbstractGameState;
import games.resistance.ResGameState;
import utilities.ImageIO;

import javax.swing.*;
import java.awt.*;

public class SHBoardView extends JComponent {

    String dataPath;
    ResGameState gameState;
    Image backOfCard;
    SHBoardView gameBoardView;
    public SHBoardView(AbstractGameState gs, String dataPath) {
        gameState = (ResGameState) gs;
        this.dataPath = dataPath;
        this.gameBoardView = new SHBoardView(gameState,dataPath);
        backOfCard = ImageIO.GetInstance().getImage(dataPath + "CardBack.png");
    }

    public void update(ResGameState gameState)
    {
        this.gameState = gameState;

    }
}
