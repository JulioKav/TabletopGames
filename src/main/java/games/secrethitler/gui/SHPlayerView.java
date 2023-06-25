package games.secrethitler.gui;

import core.components.Deck;
import games.resistance.ResGameState;
import games.resistance.components.ResPlayerCards;

import javax.swing.*;
import java.awt.*;

import static games.secrethitler.gui.SHGUIManager.playerAreaHeight;
import static games.secrethitler.gui.SHGUIManager.playerAreaWidth;


public class SHPlayerView extends JComponent {

    // ID of player showing
    int playerId;
    // Number of points player has
    SHDeckView playerHandView;

    JLabel pointsText;
    JLabel missionSuccessText;

    // Border offsets
    int border = 5;
    int borderBottom = 20;
    int width, height;

    ResGameState gs;

    public SHPlayerView(Deck<ResPlayerCards> deck, int playerId, int humanId, String dataPath)
    {
        this.width = playerAreaWidth;
        this.height = playerAreaHeight;
        this.playerId = playerId;
        this.playerHandView = new SHDeckView(humanId, deck, true, dataPath, new Rectangle(border, border, playerAreaWidth, playerAreaHeight));
        this.pointsText = new JLabel(0 + " points");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(playerHandView);
        add(pointsText);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    public void update(ResGameState gameState)
    {
        gs = gameState;
        playerHandView.updateComponent(gameState.getPlayerHandCards().get(playerId));

    }
}
