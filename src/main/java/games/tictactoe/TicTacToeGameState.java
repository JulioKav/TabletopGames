package games.tictactoe;

import core.components.GridBoard;
import core.AbstractGameState;
import core.interfaces.IGridGameState;
import core.observations.VectorObservation;
import core.turnorders.AlternatingTurnOrder;
import utilities.Utils;

import java.util.ArrayList;
import java.util.HashMap;


public class TicTacToeGameState extends AbstractGameState implements IGridGameState<Character> {

    GridBoard<Character> gridBoard;
    final ArrayList<Character> playerMapping = new ArrayList<Character>() {{
        add('x');
        add('o');
    }};

    public TicTacToeGameState(TicTacToeGameParameters gameParameters, int nPlayers){
        super(gameParameters, new AlternatingTurnOrder(nPlayers));
    }

    @Override
    public void addAllComponents() {
        allComponents.putComponent(gridBoard);
    }

    @Override
    protected AbstractGameState copy(int playerId) {
        TicTacToeGameState s = new TicTacToeGameState((TicTacToeGameParameters)gameParameters, getNPlayers());
        s.gridBoard = gridBoard.copy();
        return s;
    }

    @Override
    public VectorObservation getVectorObservation() {
        return new VectorObservation<>(gridBoard.flattenGrid());
    }

    @Override
    public double[] getDistanceFeatures(int playerId) {
        return new double[0];
    }

    @Override
    public HashMap<HashMap<Integer, Double>, Utils.GameResult> getTerminalFeatures(int playerId) {
        return null;
    }

    @Override
    public double getScore(int playerId) {
        int nChars = 0;
        for (int i = 0; i < gridBoard.getWidth(); i++) {
            for (int j = 0; j < gridBoard.getHeight(); j++) {
                if (gridBoard.getElement(i, j) == playerMapping.get(playerId)) nChars++;
            }
        }
        return nChars;
    }

    @Override
    public GridBoard<Character> getGridBoard() {
        return gridBoard;
    }
}
