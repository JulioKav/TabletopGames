package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.PartialObservableDeck;
import core.interfaces.IExtendedSequence;
import games.secrethitler.SHGameState;
import games.secrethitler.components.SHPlayerCards;
import games.secrethitler.components.SHPolicyCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SHPolicySelection extends AbstractAction implements IExtendedSequence {
    public final int playerId;
    public final ArrayList<SHPolicyCards> selectedCards;
    public final ArrayList<SHPolicyCards> drawn3Cards;
    public final PartialObservableDeck<SHPolicyCards> discardPile;

    public SHPolicySelection(int playerId, ArrayList<SHPolicyCards> selectedCards,ArrayList<SHPolicyCards> drawn3Cards,PartialObservableDeck<SHPolicyCards> discardPile) {
        this.playerId = playerId;
        this.selectedCards = selectedCards;
        this.drawn3Cards = drawn3Cards;
        this.discardPile = discardPile;
    }

//    public SHPolicySelection getHiddenChoice(int i) {
//        Random rnd = new Random();
//        if (rnd.nextInt(2) == 0){return new SHPolicySelection(i, SHPlayerCards.CardType.Yes);}
//        else {return new SHPolicySelection(i, SHPolicyCards.CardType.No);}

//        if (resgs.getPlayerHandCards().get(i).getSize() > 3)
//        {
//
//            return new SHPolicySelection(i, 0);}
//        else{return new SHPolicySelection(i, 0);}
 //   }

    @Override
    public boolean execute(AbstractGameState gs) {

        ((SHGameState)gs).addPolicyChoice(this, gs.getCurrentPlayer());
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
//
        SHGameState resgs = (SHGameState) state;
        List<AbstractAction> actions = new ArrayList<>();
//        if(resgs.getFinalTeam().contains(playerId)){
//
//            actions.add(new SHPolicySelection(playerId, SHPlayerCards.CardType.Yes));
//            actions.add(new SHPolicySelection(playerId, SHPlayerCards.CardType.No));
//        }
//        else {
//            actions.add(new SHWait(playerId));
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
    public SHPolicySelection copy() {
        return this; // immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHPolicySelection)) return false;
        SHPolicySelection that = (SHPolicySelection) o;
        return playerId == that.playerId && selectedCards == that.selectedCards && drawn3Cards == that.drawn3Cards && discardPile == that.discardPile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, selectedCards,drawn3Cards,discardPile);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return selectedCards + ".";
    }
}
