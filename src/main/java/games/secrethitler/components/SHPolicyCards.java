package games.secrethitler.components;

import core.components.Card;
import core.components.Component;

import java.util.Objects;

/**
 * <p>Components represent a game piece, or encompass some unit of game information (e.g. cards, tokens, score counters, boards, dice etc.)</p>
 * <p>Components in the game can (and should, if applicable) extend one of the other components, in package {@link core.components}.
 * Or, the game may simply reuse one of the existing core components.</p>
 * <p>They need to extend at a minimum the {@link Component} super class and implement the {@link Component#copy()} method.</p>
 * <p>They also need to include {@link Object#equals(Object)} and {@link Object#hashCode()} methods.</p>
 * <p>They <b>may</b> keep references to other components or actions (but these should be deep-copied in the copy() method, watch out for infinite loops!).</p>
 */
public class SHPolicyCards extends Card {
    public enum CardType {
        Fascist,
        Liberal,
    }

    /**
     * @return Make sure to return an exact <b>deep</b> copy of the object, including all of its variables.
     * Make sure the return type is this class (e.g. GTComponent) and NOT the super class Component.
     * <p>
     * <b>IMPORTANT</b>: This should have the same componentID
     * (using the protected constructor on the Component super class which takes this as an argument).
     * </p>
     * <p>The function should also call the {@link Component#copyComponentTo(Component)} method, passing in as an
     * argument the new copy you've made.</p>
     * <p>If all variables in this class are final or effectively final, then you can just return <code>`this`</code>.</p>
     */

    public final CardType cardType;

    public SHPolicyCards(CardType cardType){
        super(cardType.toString());
        this.cardType = cardType;
    }

    protected SHPolicyCards(CardType cardType, int ID){
        super(cardType.toString(), ID);
        this.cardType = cardType;
    }

    @Override
    public SHPolicyCards copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHPolicyCards)) return false;
        if (!super.equals(o)) return false;
        SHPolicyCards shCard = (SHPolicyCards) o;
        return  cardType == this.cardType ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    public String toString() {
        return cardType.name();
    }
}
