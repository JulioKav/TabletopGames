package games.secrethitler.components;

import core.components.Component;

import java.util.Arrays;
import java.util.Objects;

import static core.CoreConstants.ComponentType.BOARD;

public class SHGameBoard extends Component {

    int[] missionSuccessValues = new int[5];
    SHGameBoard type;

    public enum BoardTileType {
        Bullet,
        InspectDeck,
        InspectIdentity,
    }
    public SHGameBoard(int[] missionSuccessValues) {
        super(BOARD, "Board");
        this.missionSuccessValues = missionSuccessValues;
    }

    protected SHGameBoard(int[] missionSuccessValues, int componentID) {
        super(BOARD, "Board", componentID);
        this.missionSuccessValues = missionSuccessValues;
    }

    public void setType(SHGameBoard type) {
        this.type = type;
    }
    public int[] getMissionSuccessValues() {
        return missionSuccessValues;
    }

    @Override
    public SHGameBoard copy() {
        SHGameBoard copy = new SHGameBoard(missionSuccessValues, componentID);
        copyComponentTo(copy);
        copy.type = type;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHGameBoard)) return false;
        if (!super.equals(o)) return false;

        return Arrays.equals(missionSuccessValues,this.missionSuccessValues) && type == this.type;
    }

    @Override
    public int hashCode() {
        // Potentially get rid of ownerID. Sets the owner of the gameboard as the game.
        int result = Objects.hash(super.hashCode(), ownerId, type, missionSuccessValues);
        return result;
    }
}
