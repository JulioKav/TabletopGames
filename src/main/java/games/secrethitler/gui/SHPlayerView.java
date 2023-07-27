package games.secrethitler.gui;

import core.components.Deck;
import games.secrethitler.SHGameState;
import games.resistance.components.ResPlayerCards;
import games.secrethitler.SHGameState;
import games.secrethitler.components.SHPlayerCards;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

import static games.secrethitler.gui.SHGUIManager.playerAreaHeight;
import static games.secrethitler.gui.SHGUIManager.playerAreaWidth;


public class SHPlayerView extends JComponent {

    int playerId;
    SHDeckView playerHandView;

    JLabel pointsText;

    int border = 5;
    int width, height;

    SHGameState gs;

    public SHPlayerView(Deck<SHPlayerCards> deck, int playerId, int humanId, String dataPath)
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

    public void update(SHGameState gameState)
    {
        gs = gameState;
        playerHandView.updateComponent(gameState.getPlayerHandCards().get(playerId));

    }

}
