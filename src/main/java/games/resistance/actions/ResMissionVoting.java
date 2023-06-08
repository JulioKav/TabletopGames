package games.resistance.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.PartialObservableDeck;
import core.interfaces.IExtendedSequence;
import games.resistance.ResGameState;
import games.resistance.components.ResPlayerCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ResMissionVoting extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final ResPlayerCards.CardType cardType;

    public ResMissionVoting(int playerId, ResPlayerCards.CardType cardType) {
        this.playerId = playerId;
        this.cardType = cardType;
    }

    public ResMissionVoting getHiddenChoice( int i) {
        Random rnd = new Random();
        if (rnd.nextInt(2) == 0){return new ResMissionVoting(i, ResPlayerCards.CardType.Yes);}
        else {return new ResMissionVoting(i, ResPlayerCards.CardType.No);}

//        if (resgs.getPlayerHandCards().get(i).getSize() > 3)
//        {
//
//            return new ResMissionVoting(i, 0);}
//        else{return new ResMissionVoting(i, 0);}
    }

    @Override
    public boolean execute(AbstractGameState gs) {

        ((ResGameState)gs).addMissionChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {

        List<AbstractAction> actions = new ArrayList<>();
//        ResGameState resgs = (ResGameState) state;
//        int idxSelected = resgs.getvotingChoice().get(playerId).get(0).cardIdx;
//
//
//        PartialObservableDeck<ResPlayerCards> currentPlayerHand = resgs.getPlayerHandCards().get(playerId);
//        for (int i = 0; i < currentPlayerHand.getSize(); i++) {
//            if (idxSelected != i) {
//                actions.add(new ResMissionVoting(playerId, i));
//            }
//        }

        return actions;
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return playerId;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {

    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return false;
    }

    @Override
    public ResMissionVoting copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResMissionVoting)) return false;
        ResMissionVoting that = (ResMissionVoting) o;
        return playerId == that.playerId && cardType == that.cardType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, cardType);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Choose card " + cardType;
    }
}
