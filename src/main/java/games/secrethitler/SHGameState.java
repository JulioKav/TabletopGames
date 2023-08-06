package games.secrethitler;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.PartialObservableDeck;
import core.interfaces.IGamePhase;
import games.GameType;
import games.resistance.ResGameState;
import games.secrethitler.actions.*;
import games.secrethitler.components.SHGameBoard;
import games.secrethitler.components.SHPlayerCards;
import games.secrethitler.components.SHPolicyCards;

import java.util.*;

/**
 * <p>The game state encapsulates all game information. It is a data-only class, with game functionality present
 * in the Forward Model or actions modifying the state of the game.</p>
 * <p>Most variables held here should be {@link Component} subclasses as much as possible.</p>
 * <p>No initialisation or game logic should be included here (not in the constructor either). This is all handled externally.</p>
 * <p>Computation may be included in functions here for ease of access, but only if this is querying the game state information.
 * Functions on the game state should never <b>change</b> the state of the game.</p>
 */
public class SHGameState extends AbstractGameState {

    boolean roundEnded = false;
    int previousChancellor;
    int hitlerID;
    int previousLeader;
    int spyCounter = 0;
    boolean hasSomeoneBeenKilledThisRound = false;
    int resistanceCounter = 0;
    public int[] factions;
    public List<Boolean> gameBoardValues = new ArrayList<>();
    public ArrayList<SHPolicyCards> drawnPolicies;
    public ArrayList<SHPolicyCards> final2PolicyChoices;
    boolean voteSuccess;
    int leaderID;
    int winners = 3;
    int failedVoteCounter = 0;
    int occurrenceCountTrue = 0;
    int occurrenceCountFalse = 0;

    int vetoVoteFalse = 0;
    int previousOccurrenceCountFalse = 0; // Used for checking If you've killed someone at that fascist policy stage/level
    List<List<SHVoting>> votingChoice;
    List<List<SHVeto>> vetoChoice;
    List<SHPolicyCards> peekedCards;
    int knowerOfPeekedCards = 999;

    public PartialObservableDeck<SHPolicyCards> drawPile;
    public PartialObservableDeck<SHPolicyCards> discardPile;

    public List<List<SHPolicySelection>> missionVotingChoice;

    HashMap<Integer,List<Integer>> knownIdentities;
    List<int[]> teamChoice = new ArrayList<>();
    IGamePhase previousGamePhase = null;

    List<Integer> deceasedFellas = new ArrayList<>();
    int whereDrawPileCHanges;
    int chancellorID;
    int investigatingID;


    ArrayList<SHPolicyCards> finalPolicyChoice;

    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHGameState)) return false;
        if (!super.equals(o)) return false;
        SHGameState that = (SHGameState) o;
        return
                leaderID == that.leaderID && chancellorID == that.chancellorID && hitlerID == that.hitlerID &&
                        Objects.equals(playerHandCards, that.playerHandCards)  && Objects.equals(gameBoardValues, that.gameBoardValues) && Objects.equals(deceasedFellas, that.deceasedFellas)
                        && Objects.equals(drawnPolicies, that.drawnPolicies) && Objects.equals(teamChoice, that.teamChoice) && Objects.equals(drawPile, that.drawPile) && Objects.equals(finalPolicyChoice, that.finalPolicyChoice)
                        && Objects.equals(missionVotingChoice, that.missionVotingChoice)  && Objects.equals(peekedCards, that.peekedCards) && Objects.equals(vetoChoice, that.vetoChoice)  && Objects.equals(final2PolicyChoices, that.final2PolicyChoices);


    }

    @Override
    public int hashCode() {
        int result =  super.hashCode() + 31 *
                Objects.hash( gameBoardValues,playerHandCards,deceasedFellas)
                * Objects.hash( drawnPolicies,drawPile,teamChoice,finalPolicyChoice)
                * Objects.hash(final2PolicyChoices,missionVotingChoice,vetoChoice,peekedCards)
                * leaderID * chancellorID * hitlerID;

        return result;
    }

    public void addPeekedCards(SHLeaderPeeks shLeaderPeeks) {
        peekedCards = shLeaderPeeks.cardsPeeked;
        knowerOfPeekedCards = shLeaderPeeks.playerId;
    }
    public void addChosenLeaderChoice(SHLeaderSelectsLeader shLeaderSelectsLeader) {
        leaderID = shLeaderSelectsLeader.getchosenLeaderID();

    }
    public void addChancellorChoice(SHChancellorSelection shChancellorSelection) {
        chancellorID = shChancellorSelection.getChancellorID();

    }


    public void addInvestigatingChoice(SHInvestigateIdentity SHInvestigateIdentity,SHGameState shgs) {
        investigatingID = SHInvestigateIdentity.getinvestigatingID();
        int currentPlayer = SHInvestigateIdentity.getCurrentPlayer(shgs);
        List<Integer> currentPlayerList = new ArrayList<>();
        currentPlayerList.add(investigatingID);

        if(knownIdentities.containsKey(currentPlayer))
        {
            List<Integer> newKnownIdentities = knownIdentities.get(currentPlayer);
            newKnownIdentities.add(investigatingID);
            knownIdentities.put(currentPlayer,newKnownIdentities);
        }
        else {knownIdentities.put(currentPlayer,currentPlayerList);}
    }




    public void addKillChoice(SHKill shKillSelection, int currentPlayer) {
        deceasedFellas.add(shKillSelection.victim);
    }
    public void addPolicyChoice(SHPolicySelection shPolicySelection, int currentPlayer) {
        if(currentPlayer == leaderID)
        {
            final2PolicyChoices = new ArrayList<>();
            final2PolicyChoices.add(shPolicySelection.selectedCards.get(0));
            final2PolicyChoices.add(shPolicySelection.selectedCards.get(1));





        }
        if(currentPlayer == chancellorID)
        {
            finalPolicyChoice = new ArrayList<>();
            finalPolicyChoice.add(shPolicySelection.selectedCards.get(0));
        }
    }

    public enum SHGamePhase implements IGamePhase {

        LeaderSelectsChancellor,VotingOnLeader, VotingOnChancellor,
        LeaderSelectsPolicy,ChancellorSelectsPolicy,LeaderKillsPlayer,
        LeaderInvestigatesPlayer,LeaderSelectsLeader,LeaderPeeksTop3Cards, Veto
    }

    List<PartialObservableDeck<SHPlayerCards>> playerHandCards = new ArrayList<>(10);
    public SHGameBoard gameBoard = new SHGameBoard(new int[nPlayers]);


    /**
     * @param gameParameters - game parameters.
     * @param nPlayers       - number of players in the game
     */



    public SHGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
    }

    /**
     * @return the enum value corresponding to this game, declared in {@link GameType}.
     */
    @Override
    protected GameType _getGameType() {
        return GameType.Resistance;
    }

    /**
     * Returns all Components used in the game and referred to by componentId from actions or rules.
     * This method is called after initialising the game state, so all components will be initialised already.
     *
     * @return - List of Components in the game.
     */
    @Override
    protected List<Component> _getAllComponents() {
        if(gameBoard == null)
        {throw new AssertionError("GameBoard shouldn't be null");};
        return new ArrayList<Component>() {{
            add(gameBoard);
            addAll(playerHandCards);
        }};
    }



    /**
     * <p>Create a deep copy of the game state containing only those components the given player can observe.</p>
     * <p>If the playerID is NOT -1 and If any components are not visible to the given player (e.g. cards in the hands
     * of other players or a face-down deck), then these components should instead be randomized (in the previous examples,
     * the cards in other players' hands would be combined with the face-down deck, shuffled together, and then new cards drawn
     * for the other players).</p>
     * <p>If the playerID passed is -1, then full observability is assumed and the state should be faithfully deep-copied.</p>
     *
     * <p>Make sure the return type matches the class type, and is not AbstractGameState.</p>
     *
     * @param playerId - player observing this game state.
     */

    @Override
    protected SHGameState _copy(int playerId) {

        whereDrawPileCHanges = drawPile.getSize();
        SHGameState copy = new SHGameState(gameParameters.copy(), getNPlayers());
        copy.gameBoard = gameBoard;
        copy.factions = factions;
        copy.knownIdentities = new HashMap<>();
        copy.previousGamePhase = previousGamePhase;
        copy.voteSuccess = voteSuccess;
        copy.teamChoice = new ArrayList<>();
        copy.votingChoice = new ArrayList<>();
        copy.missionVotingChoice = new ArrayList<>();
        copy.playerHandCards = new ArrayList<>();
        copy.finalPolicyChoice = new ArrayList<>();
        copy.gameBoardValues = new ArrayList<>();
        copy.drawnPolicies = new ArrayList<>();
        copy.final2PolicyChoices = new ArrayList<>();
        copy.deceasedFellas = new ArrayList<>();
        copy.peekedCards = new ArrayList<>();
        copy.vetoChoice = new ArrayList<>();
        boolean[] visible = new boolean[17];
        for (int i = 0; i < visible.length ; i++) {
            visible[i] = false;
        }

        if (playerId == -1) {
            copy.roundEnded = roundEnded;
            copy.investigatingID = investigatingID;
            copy.hitlerID = hitlerID;
            copy.leaderID = leaderID;
            copy.chancellorID = chancellorID;
            copy.previousLeader = previousLeader;
            copy.previousChancellor = previousChancellor;
            copy.winners = winners;
            copy.drawPile = drawPile.copy();
            copy.discardPile = discardPile.copy();
            copy.hasSomeoneBeenKilledThisRound = hasSomeoneBeenKilledThisRound;
            copy.previousOccurrenceCountFalse = previousOccurrenceCountFalse;
            copy.occurrenceCountTrue = occurrenceCountTrue;
            copy.occurrenceCountFalse = occurrenceCountFalse;
            copy.vetoVoteFalse = vetoVoteFalse;
            copy.knowerOfPeekedCards = knowerOfPeekedCards;
            for (int i = 0; i < deceasedFellas.size(); i++) {
                copy.deceasedFellas.add(deceasedFellas.get(i));
            }
            for (int i = 0; i < peekedCards.size(); i++) {
                copy.peekedCards.add(peekedCards.get(i));
            }

            for (int i = 0; i < getNPlayers(); i++) {
                    copy.knownIdentities.put(i,knownIdentities.get(i));
            }

            for (int i = 0; i < final2PolicyChoices.size(); i++) {
                copy.final2PolicyChoices.add(final2PolicyChoices.get(i));
            }

            for (int i = 0; i < drawnPolicies.size(); i++) {
                copy.drawnPolicies.add(drawnPolicies.get(i));
            }

            for (int i = 0; i < getNPlayers(); i++) {
                copy.playerHandCards.add(playerHandCards.get(i));
            }

            for(int i = 0; i < gameBoardValues.size(); i++){
                copy.gameBoardValues.add(gameBoardValues.get(i));
            }
            for(int i = 0; i < finalPolicyChoice.size(); i++){
                copy.finalPolicyChoice.add(finalPolicyChoice.get(i));
            }
            for(int i = 0; i < teamChoice.size(); i++){
                copy.teamChoice.add(teamChoice.get(i));
            }
            for (int j = 0; j < vetoChoice.size(); j++) {
                copy.vetoChoice.add(vetoChoice.get(j));
            }

            for (int i = 0; i < getNPlayers(); i++) {
                copy.votingChoice.add(new ArrayList<>(votingChoice.get(i)));
                if(i < missionVotingChoice.size()){copy.missionVotingChoice.add(new ArrayList<>(missionVotingChoice.get(i)));}
            }

        }
        else {
            spyCounter = 0;
            resistanceCounter = 0;
            for (int i = 0; i < getNPlayers(); i++) {
                //Knowledge of Own Hand/Votes
                if (i == playerId) {
                    copy.drawPile = drawPile.copy();
                    copy.roundEnded = roundEnded;
                    copy.leaderID = leaderID;
                    copy.chancellorID = chancellorID;
                    copy.previousLeader = previousLeader;
                    copy.previousChancellor = previousChancellor;
                    copy.discardPile = discardPile.copy();
                    copy.hasSomeoneBeenKilledThisRound = hasSomeoneBeenKilledThisRound;
                    copy.previousOccurrenceCountFalse = previousOccurrenceCountFalse;
                    copy.occurrenceCountTrue = occurrenceCountTrue;
                    copy.occurrenceCountFalse = occurrenceCountFalse;
                    copy.knowerOfPeekedCards = knowerOfPeekedCards;
                    copy.vetoVoteFalse = vetoVoteFalse;
                    if(i == hitlerID){copy.hitlerID = hitlerID;}

                    if(knowerOfPeekedCards < 11){
                        if(i== knowerOfPeekedCards){
                            for (int j = 0; j < peekedCards.size(); j++) {
                                copy.peekedCards.add(peekedCards.get(j));
                            }
                        }
                    }
                    if(i == leaderID){
                        if(drawnPolicies != null) {
                            for (int j = 0; j < drawnPolicies.size(); j++) {
                                copy.drawnPolicies.add(drawnPolicies.get(j));
                            }
                        }
                    }
                    if(i == chancellorID || i == leaderID)
                    {
                        if(final2PolicyChoices != null) {
                            for (int j = 0; j < final2PolicyChoices.size(); j++) {
                                copy.final2PolicyChoices.add(final2PolicyChoices.get(j));
                            }
                        }
                        if(finalPolicyChoice != null) {
                            for (int j = 0; j < finalPolicyChoice.size(); j++) {
                                copy.finalPolicyChoice.add(finalPolicyChoice.get(j));
                            }
                        }
                    }

                    for (int j = 0; j < getNPlayers(); j++) {
                        if(playerId == j)
                        {copy.knownIdentities.put(j,knownIdentities.get(j));}
                        else{copy.knownIdentities.put(j, new ArrayList<>());}
                    }

                    for(int j = 0; j < gameBoardValues.size(); j++){
                        copy.gameBoardValues.add(gameBoardValues.get(j));
                    }

                    for (int j = 0; j < deceasedFellas.size(); j++) {
                        copy.deceasedFellas.add(deceasedFellas.get(j));
                    }

                    for (int j = 0; j < vetoChoice.size(); j++) {
                        copy.vetoChoice.add(vetoChoice.get(j));
                    }

                    for(int j = 0; j < teamChoice.size(); j++){
                        copy.teamChoice.add(teamChoice.get(j));
                    }

                    copy.votingChoice.add(new ArrayList<>(votingChoice.get(i)));
                    copy.playerHandCards.add(playerHandCards.get(i));
                    copy.leaderID = leaderID;
                    //Checking MissionVote Eligibility
                    copy.missionVotingChoice.add(new ArrayList<>(missionVotingChoice.get(i)));
                }
                else{
                //Allowing Spies To Know All Card Types with Hitler Logic Done
                if(playerHandCards.get(playerId).get(playerHandCards.get(playerId).getSize()-1).cardType == SHPlayerCards.CardType.Fascist && i != playerId){
                    if(getNPlayers() < 7 && playerId == hitlerID){copy.playerHandCards.add(playerHandCards.get(i));}
                    else if (getNPlayers() < 7) {copy.playerHandCards.add(playerHandCards.get(i));}
                    if (getNPlayers() > 6 && playerId != hitlerID){copy.playerHandCards.add(playerHandCards.get(i));}
                    else if (getNPlayers() > 6 && playerId == hitlerID)
                    {
                        if(knownIdentities.get(playerId).contains(i)){copy.playerHandCards.add(playerHandCards.get(i));}
                        else{copy.playerHandCards.add(createHiddenHands(i));}
                    }
                    copy.hitlerID = hitlerID;
                }

                else if (i != playerId){
                    if(knownIdentities.get(playerId).contains(i)){copy.playerHandCards.add(playerHandCards.get(i));}
                    else{copy.playerHandCards.add(createHiddenHands(i));}
                }

                if (i != playerId){
                    ArrayList<SHVoting> hiddenChoiceVote = new ArrayList<>();
                    ArrayList<SHPolicySelection> hiddenChoiceMissionVote = new ArrayList<>();

                    copy.votingChoice.add(hiddenChoiceVote);
                    copy.missionVotingChoice.add(hiddenChoiceMissionVote);}
                }
            }

        }

        return copy;

    }

    public void clearCardChoices() {
        for (int i = 0; i < getNPlayers(); i++) votingChoice.get(i).clear();
    }
    public void clearVetoChoice() {
        for (int i = 0; i < getNPlayers(); i++) vetoChoice.get(i).clear();
    }

    public void clearDiscardPile() {
        boolean[] visible = new boolean[17];
        for (int i = 0; i < visible.length ; i++) {
            visible[i] = false;
        }

        discardPile = new PartialObservableDeck<>("discard pile", -1, visible);
    }

    public void addCardChoice(SHVoting SHVoting, int playerId) {
        votingChoice.get(playerId).add(SHVoting);
    }
    public void addVetoChoice(SHVeto shVeto, int playerId) {
        vetoChoice.get(playerId).add(shVeto);
    }

    public void clearTeamChoices() {
        for (int i = 0; i < getNPlayers(); i++) teamChoice.clear();
        for (int i = 0; i < getNPlayers(); i++) finalPolicyChoice.clear();
    }
    public void clearVoteChoices() {
        for (int i = 0; i < getNPlayers(); i++) votingChoice.get(i).clear();
    }

    /**
     * @param playerId - player observing the state.
     * @return a score for the given player approximating how well they are doing (e.g. how close they are to winning
     * the game); a value between 0 and 1 is preferred, where 0 means the game was lost, and 1 means the game was won.
     */
    @Override
    protected double _getHeuristicScore(int playerId) {
        if (isNotTerminal()) {
            return 0;
        } else {
            // The game finished, we can instead return the actual result of the game for the given player.
            return getPlayerResults()[playerId].value;
        }
    }

    /**
     * @param playerId - player observing the state.
     * @return the true score for the player, according to the game rules. May be 0 if there is no score in the game.
     */
    @Override
    public double getGameScore(int playerId) {
        // TODO: What is this player's score (if any)?
        return 0;
    }

    public List<PartialObservableDeck<SHPlayerCards>> getPlayerHandCards(){
        return playerHandCards;
    }

    private PartialObservableDeck<SHPlayerCards> createHiddenHands(int i){
        PartialObservableDeck<SHPlayerCards> hiddenPlayerHandCards = new PartialObservableDeck<>("hiddenDeck",i);
        Random rnd = new Random();
        if (rnd.nextInt(2) == 0 && spyCounter != factions[1]) {
            SHPlayerCards SPY = new SHPlayerCards(SHPlayerCards.CardType.Fascist);
            SPY.setOwnerId(i);
            hiddenPlayerHandCards.add(SPY);
            spyCounter += 1;
        }
        else if (resistanceCounter != factions[0])
        {
            SHPlayerCards resistor = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
            resistor.setOwnerId(i);
            hiddenPlayerHandCards.add(resistor);
            resistanceCounter += 1;
        }

        else if (spyCounter != factions[1] && resistanceCounter == factions[0])
        {
            SHPlayerCards SPY = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
            SPY.setOwnerId(i);
            hiddenPlayerHandCards.add(SPY);
            spyCounter += 1;
        }
        else if (spyCounter == factions[1] && resistanceCounter != factions[0])
        {
            SHPlayerCards resistor = new SHPlayerCards(SHPlayerCards.CardType.Liberal);
            resistor.setOwnerId(i);
            hiddenPlayerHandCards.add(resistor);
            resistanceCounter += 1;
        }
        SHPlayerCards No = new SHPlayerCards(SHPlayerCards.CardType.No);
        No.setOwnerId(i);
        SHPlayerCards Yes = new SHPlayerCards(SHPlayerCards.CardType.Yes);
        No.setOwnerId(i);
        hiddenPlayerHandCards.add(No);
        hiddenPlayerHandCards.add(Yes);


        return hiddenPlayerHandCards;
    }

    public int getLeaderID() {
        return leaderID;
    }
    public int getChancellorID() {
        return chancellorID;
    }
    public int getPreviousChancellor() {
        return previousChancellor;
    }
    public int getPreviousLeader() {
        return previousLeader;
    }
    public int getHitlerID() {
        return hitlerID;
    }
    public List<Integer> getDeceasedFellas() {
        return deceasedFellas;
    }
    public PartialObservableDeck<SHPolicyCards> getDrawPile() {
        return drawPile;
    }
    public ArrayList<SHPolicyCards> getPoliciesDrawnPile() {
        return drawnPolicies;
    }
    public ArrayList<SHPolicyCards> getFinalPolicyChoice() {
        return finalPolicyChoice;
    }
    public List<Boolean> getGameBoardValues() {
        return gameBoardValues;
    }
    public int getFailedVoteCounter() {
        return failedVoteCounter;
    }
    public int getWinners() {
        return winners;
    }
    public boolean getVoteSuccess() {
        return voteSuccess;
    }

}
