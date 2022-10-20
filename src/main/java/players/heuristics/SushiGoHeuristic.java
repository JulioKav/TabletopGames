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

    double eggNigiriValue = 1;
    double salmonNigiriValue = 2;
    double squidNigiriValue = 3;
    double dumplingValue = 1;  // Play it
    double sashimiValue = 0;  // Play it
    double tempuraValue = 0;  // Play it
    double wasabiValue = 4.5;
    double puddingValue = 1;
    double maki1Value = 0;
    double maki2Value = 0.5;
    double maki3Value = 1;
    double chopsticksValue = 5;







    public SushiGoHeuristic() {
        addTunableParameter("eggNigiriValue", 1.0);
        addTunableParameter("salmonNigiriValue", 2);
        addTunableParameter("squidNigiriValue", 3);
        addTunableParameter("dumplingValue", 1.0);
        addTunableParameter("sashimiValue", 0.0);
        addTunableParameter("tempuraValue", 0.0);
        addTunableParameter("wasabiValue", 4.5);
        addTunableParameter("puddingValue", 1);
        addTunableParameter("maki1Value", 0.5);
        addTunableParameter("maki2Value", 0.7);
        addTunableParameter("maki3Value", 1.0);
        addTunableParameter("chopsticksValue", 5.0);
    }


    public double evaluateState(AbstractGameState gs, int playerId) {
        SGGameState sggs = (SGGameState) gs;
        //if (sggs.isNotTerminal())
        //    return (sggs.getPlayerScore()[playerId] + sggs.getPlayerScoreToAdd(playerId))/ 50.0;
       // return sggs.getPlayerResults()[playerId].value;

        double cardValues = 0.0;
        if (sggs.isNotTerminal()) {
            for (SGCard card : sggs.getPlayerDeck(playerId).getComponents()) {
                cardValues += getCardValue(sggs, card, playerId);
            }

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


    double getCardValue(SGGameState sggs, SGCard card, int playerID) {
        switch(card.getComponentName())  {
            case "EggNigiri":
                if (sggs.getPlayerWasabiAvailable(playerID) >= 1)
            {
                return eggNigiriValue * 3;
            }
                return eggNigiriValue;
            case "SalmonNigiri":
                if (sggs.getPlayerWasabiAvailable(playerID) >= 1)
                {
                    return salmonNigiriValue * 3;
                }
                return salmonNigiriValue;
            case "SquidNigiri":
                if (sggs.getPlayerWasabiAvailable(playerID) >= 1)
                {
                    return squidNigiriValue * 3;
                }
                return squidNigiriValue;
            case "Dumpling":
                if (sggs.getPlayerDumplingAmount(playerID) == 2)
                {
                    return dumplingValue * 2;
                }
                if (sggs.getPlayerDumplingAmount(playerID) == 2)
                {
                    return dumplingValue * 3;
                }
                if (sggs.getPlayerDumplingAmount(playerID) == 3)
                {
                    return dumplingValue * 4;
                }
                if (sggs.getPlayerDumplingAmount(playerID) == 4)
                {
                    return dumplingValue * 5;
                }
                if (sggs.getPlayerDumplingAmount(playerID) > 5)
                {
                    return dumplingValue * 0;
                }
                return dumplingValue;
            case "Sashimi":
                if (sggs.getPlayerSashimiAmount(playerID) == 3)
                {
                    return sashimiValue + 10;
                }
                if (sggs.getPlayerSashimiAmount(playerID) == 6)
                {
                    return sashimiValue + 10;
                }
                if (sggs.getPlayerSashimiAmount(playerID) == 9)
                {
                    return sashimiValue + 10;
                }
                if (sggs.getPlayerSashimiAmount(playerID) == 12)
                {
                    return sashimiValue + 10;
                }
                return sashimiValue;
            case "Tempura":
                if (sggs.getPlayerTempuraAmount(playerID) == 2)
                {
                    return tempuraValue + 5;
                }
                if (sggs.getPlayerTempuraAmount(playerID) == 4)
                {
                    return tempuraValue + 5;
                }
                if (sggs.getPlayerTempuraAmount(playerID) == 6)
                {
                    return tempuraValue + 5;
                }
                if (sggs.getPlayerTempuraAmount(playerID) == 8)
                {
                    return tempuraValue + 5;
                }
                if (sggs.getPlayerTempuraAmount(playerID) == 10)
                {
                    return tempuraValue + 5;
                }
                if (sggs.getPlayerTempuraAmount(playerID) == 12)
                {
                    return tempuraValue + 5;
                }
                return tempuraValue;
            case "Wasabi":
                //if (sggs.getPlayerDeck(playerID).getSize() <= 5)
                 //   return wasabiValue 0;
                return wasabiValue;
            case "Pudding":
                return puddingValue;
            case "Maki_1":
                return maki1Value;
            case "Maki_2":
                return maki2Value;
            case "Maki_3":
                return maki3Value;
            case "Chopsticks":
                return chopsticksValue;
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
        chopsticksValue = (double) getParameterValue("puddingValue");


    }
}


