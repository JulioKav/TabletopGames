package players.heuristics;

import core.interfaces.IStateHeuristic;
import core.AbstractGameState;
import games.sushigo.SGGameState;

public class SushiGoHeuristic implements IStateHeuristic {
    public double evaluateState(AbstractGameState gs, int playerId) {
        SGGameState sggs = (SGGameState) gs;
        if (sggs.isNotTerminal()) {

            return (sggs.getPlayerScore()[playerId]
                    + sggs.getPlayerField(playerId).getSize()
                    //- sggs.getPlayerFields().get(0).getSize()
                    //- sggs.getPlayerFields().get(1).getSize()
                    //- sggs.getPlayerFields().get(2).getSize()
                    //- sggs.getPlayerFields().get(3).getSize()
            )/50.0;
        }
        return sggs.getPlayerResults()[playerId].value;
    }
}


