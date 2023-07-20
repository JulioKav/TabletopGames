package games.secrethitler.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.PartialObservableDeck;
import core.interfaces.IExtendedSequence;
import games.resistance.actions.ResWait;
import games.secrethitler.SHGameState;
import games.secrethitler.components.SHPlayerCards;
import games.secrethitler.components.SHPolicyCards;
import utilities.Utils;

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
        SHGameState shgs = (SHGameState) state;
        List<AbstractAction> actions = new ArrayList<>();
        if (playerId == shgs.getLeaderID() && shgs.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsPolicy) {

            shgs.drawnPolicies = new ArrayList<>();
            //shgs.discardPile.add(card); REMMEBER TO ADD DISCARDS/PLAYED CARDS CORRECTLY

            if(shgs.drawPile.getSize() < 3)
            {
                shuffleDiscardsIntoDrawPile(shgs);
            }

            SHPolicyCards card = shgs.drawPile.draw();
            shgs.drawnPolicies.add(card);
            SHPolicyCards card1 = shgs.drawPile.draw();
            shgs.drawnPolicies.add(card1);
            SHPolicyCards card2 = shgs.drawPile.draw();
            shgs.drawnPolicies.add(card2);

            System.out.println(shgs.drawnPolicies + " drawn policies");
            int[] numberOfDrawnPolicies = new int[3];
            for (int i = 0; i < 3; i++) {numberOfDrawnPolicies[i] = i;}
            ArrayList<int[]> choiceOfPolicies = Utils.generateCombinations(numberOfDrawnPolicies, 2);

            for(int[] combinations : choiceOfPolicies)
            {
                shgs.final2PolicyChoices = new ArrayList<>();
                for (int index:combinations) {shgs.final2PolicyChoices.add(shgs.drawnPolicies.get(index));}
                //System.out.println(shgs.final2PolicyChoices);
                actions.add(new SHPolicySelection(playerId,shgs.final2PolicyChoices,shgs.drawnPolicies,shgs.discardPile));
            }

        }
        if (playerId == shgs.getChancellorID() && shgs.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy) {
            ArrayList<SHPolicyCards> finalPolicyChoice = new ArrayList<>();
            finalPolicyChoice.add(shgs.final2PolicyChoices.get(0));
            actions.add(new SHPolicySelection(playerId,finalPolicyChoice,shgs.drawnPolicies,shgs.discardPile));

            ArrayList<SHPolicyCards> finalPolicyChoice1 = new ArrayList<>();
            finalPolicyChoice1.add(shgs.final2PolicyChoices.get(1));
            actions.add(new SHPolicySelection(playerId,finalPolicyChoice1,shgs.drawnPolicies,shgs.discardPile));
        }
        return actions;
    }

    private void shuffleDiscardsIntoDrawPile(SHGameState shgs) {
        for (int i = 0; i < shgs.discardPile.getSize(); i++) {
            shgs.drawPile.add(shgs.discardPile.get(i));
        }
        shgs.clearDiscardPile();
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
        return playerId == that.playerId && selectedCards.equals(that.selectedCards) && drawn3Cards.equals(that.drawn3Cards) && discardPile.equals(that.discardPile);
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
