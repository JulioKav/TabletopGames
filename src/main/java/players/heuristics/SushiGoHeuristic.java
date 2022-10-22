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

import static games.sushigo.cards.SGCard.SGCardType.*;

public class SushiGoHeuristic extends TunableParameters implements IStateHeuristic {

    public double evaluateState(AbstractGameState gs, int playerId) {
        SGGameState sggs = (SGGameState) gs;

        double cardValues = 0.0;
        if (sggs.isNotTerminal()) {

            return (sggs.getPlayerScore()[playerId]
                    + sggs.getPlayerField(playerId).getSize()
                   // - sggs.getPlayerFields().get(0).getSize()
                    //- sggs.getPlayerFields().get(1).getSize()
                    //- sggs.getPlayerFields().get(2).getSize()
                    //- sggs.getPlayerFields().get(3).getSize()

            )/ 50.0;
        }
        return sggs.getPlayerResults()[playerId].value;









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


