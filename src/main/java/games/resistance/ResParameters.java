package games.resistance;

import core.AbstractGameState;
import core.AbstractParameters;
import games.explodingkittens.ExplodingKittensParameters;

import java.util.HashMap;
import java.util.Objects;

/**
 * <p>This class should hold a series of variables representing game parameters (e.g. number of cards dealt to players,
 * maximum number of rounds in the game etc.). These parameters should be used everywhere in the code instead of
 * local variables or hard-coded numbers, by accessing these parameters from the game state via {@link AbstractGameState#getGameParameters()}.</p>
 *
 * <p>It should then implement appropriate {@link #_copy()}, {@link #_equals(Object)} and {@link #hashCode()} functions.</p>
 *
 * <p>The class can optionally extend from {@link evaluation.TunableParameters} instead, which allows to use
 * automatic game parameter optimisation tools in the framework.</p>
 */
public class ResParameters extends AbstractParameters {
    public ResParameters(long seed) {
        super(seed);
    }

    private ResGameState gameState;
    public int[] gameBoard = getPlayerBoard(gameState.getNPlayers());
    public int[] factions = getFactions(gameState.getNPlayers());






    // might be wrong
    public int[] getPlayerBoard(int numberPlayers){

        if (numberPlayers == 5)
        {return new int[]{2, 3, 2, 3, 3};}
        if (numberPlayers == 6)
        {return new int[]{2, 3, 4, 3, 4};}
        if (numberPlayers == 7)
        {return new int[]{2, 3, 3, 4, 4};}
        if (numberPlayers == 8 || numberPlayers == 9 || numberPlayers == 10)
        {return new int[]{3, 4, 4, 5, 5};}

        return null;

    }

    public int[] getFactions(int numberPlayers){

        if (numberPlayers == 5)
        {return new int[]{3,2};}
        if (numberPlayers == 6)
        {return new int[]{4,2};}
        if (numberPlayers == 7)
        {return new int[]{4,3};}
        if (numberPlayers == 8)
        {return new int[]{5,3};}
        if (numberPlayers == 9)
        {return new int[]{6,3};}
        if (numberPlayers == 10)
        {return new int[]{6,4};}
        return null;

    }

    @Override
    protected AbstractParameters _copy() {
        // TODO: deep copy of all variables.
        ResParameters resp = new ResParameters(System.currentTimeMillis());


        resp.gameBoard = gameBoard;
        resp.factions = factions;
        return resp;

    }

    @Override
    protected boolean _equals(Object o) {
        // TODO: compare all variables.
        if (this == o) return true;
        if (!(o instanceof ResParameters)) return false;
        if (!super.equals(o)) return false;
        ResParameters that = (ResParameters) o;
        return  Objects.equals(gameBoard, that.gameBoard) &&
                Objects.equals(factions, that.factions);
        
    }

    @Override
    public int hashCode() {
        // TODO: include the hashcode of all variables.
        return Objects.hash(super.hashCode(),gameBoard,factions);
    }
}
