package players.heuristics;

import core.interfaces.IStateHeuristic;
import evaluation.TunableParameters;
import core.AbstractGameState;
import core.AbstractParameters;
import core.CoreConstants;
import core.CoreParameters;
import core.components.Component;
import core.components.Deck;
import games.GameType;
import games.explodingkittens.ExplodingKittensGameState;
import games.explodingkittens.ExplodingKittensHeuristic;
import games.explodingkittens.actions.IsNopeable;
import games.explodingkittens.cards.ExplodingKittensCard;
import games.sushigo.SGGameState;
import games.sushigo.cards.SGCard;
import utilities.Utils;

import java.util.*;

public class SushiGoHeuristic extends TunableParameters implements IStateHeuristic {




    public double evaluateState(AbstractGameState gs, int playerId) {
        SGGameState sggs = (SGGameState) gs;


        for (int i=0;i < sggs.getPlayerDeck(playerId).getSize();i++
        )

        {
            if ( sggs.getPlayerDeck(playerId) == SGCard.SGCardType()) {


            }

        }


        return sggs.getGameScore(playerId);


    }




    @Override
    protected AbstractParameters _copy() {
        return null;
    }

    @Override
    protected boolean _equals(Object o) {
        return false;
    }

    @Override
    public Object instantiate() {
        return null;
    }

    @Override
    public void _reset() {


    }
}


