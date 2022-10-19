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

    double eggNigiriValue = -1;
    double salmonNigiriValue = 1;
    double squidNigiriValue = -0.01;
    double dumplingValue = -0.3;  // Play it
    double sashimiValue = -0.5;  // Play it
    double tempuraValue = -0.4;  // Play it
    double wasabiValue = 0.2;
    double puddingValue = -0.1;
    double maki1Value = -0.2;
    double maki2Value = -0.2;
    double maki3Value = -0.2;







    public SushiGoHeuristic() {
        addTunableParameter("eggNigiriValue", -1.0);
        addTunableParameter("salmonNigiriValue", 1.0);
        addTunableParameter("squidNigiriValue", -0.01);
        addTunableParameter("dumplingValue", -0.3);
        addTunableParameter("sashimiValue", -0.5);
        addTunableParameter("tempuraValue", -0.4);
        addTunableParameter("wasabiValue", 0.2);
        addTunableParameter("puddingValue", -0.1);
        addTunableParameter("maki1Value", -0.1);
        addTunableParameter("maki2Value", -0.1);
        addTunableParameter("maki3Value", -0.1);
    }


    public double evaluateState(AbstractGameState gs, int playerId) {
        SGGameState sggs = (SGGameState) gs;

        double cardValues = 0.0;
        for ( SGCard card : sggs.getPlayerDeck(playerId).getComponents())
        {
            cardValues += getCardValue(sggs, card);
        }

        return cardValues;





    }


    double getCardValue(SGGameState sggs, SGCard card) {
        switch(card.getComponentName())  {
            case "EggNigiri":
                return eggNigiriValue;
            case "SalmonNigiri":
                return salmonNigiriValue;
            case "SquidNigiri":
                return squidNigiriValue;
            case "Dumpling":
                return dumplingValue;
            case "Sashimi":
                return sashimiValue;
            case "Tempura":
                return tempuraValue;
            case "Wasabi":
                return wasabiValue;
            case "Pudding":
                return puddingValue;
            case "Maki_1":
                return maki1Value;
            case "Maki_2":
                return maki2Value;
            case "Maki_3":
                return maki3Value;
        }
        return 0;
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
        eggNigiriValue = (double) getParameterValue("eggSashimiValue");
        salmonNigiriValue = (double) getParameterValue("salmonSashimiValue");
        squidNigiriValue = (double) getParameterValue("squidSashimiValue");
        dumplingValue = (double) getParameterValue("dumplingValue");
        sashimiValue = (double) getParameterValue("sashimiValue");
        tempuraValue = (double) getParameterValue("tempuraValue");
        wasabiValue = (double) getParameterValue("wasabiValue");
        puddingValue = (double) getParameterValue("puddingValue");


    }
}


