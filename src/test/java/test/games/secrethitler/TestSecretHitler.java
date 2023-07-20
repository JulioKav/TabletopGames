package test.games.secrethitler;

import core.AbstractPlayer;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import core.interfaces.IGamePhase;
import games.GameType;
import games.secrethitler.SHGameState;
import games.secrethitler.SHForwardModel;
import games.secrethitler.SHParameters;
import games.secrethitler.actions.SHWait;
import games.secrethitler.actions.SHVeto;
import games.secrethitler.actions.SHKill;
import games.secrethitler.actions.SHDeceased;
import games.secrethitler.actions.SHChancellorSelection;
import games.secrethitler.actions.SHInvestigateIdentity;
import games.secrethitler.actions.SHLeaderPeeks;
import games.secrethitler.actions.SHLeaderSelectsLeader;
import games.secrethitler.actions.SHPolicySelection;
import games.secrethitler.actions.SHVoting;
import games.secrethitler.components.SHPolicyCards;
import games.secrethitler.components.SHPlayerCards;
import games.secrethitler.components.SHGameBoard;
import org.junit.Before;
import org.junit.Test;
import players.simple.RandomPlayer;
import utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class TestSecretHitler {

    public int atLeastOneHandRearranged = 0;
    Game secretHitler;
    List<AbstractPlayer> players;
    SHForwardModel fm = new SHForwardModel();
    RandomPlayer rnd = new RandomPlayer();


    private void progressGame(SHGameState state, SHGameState.SHGamePhase requiredGamePhase, int playerTurn) {
            while (state.getGamePhase() != requiredGamePhase && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
            {
                fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            }
            while (state.getCurrentPlayer() != playerTurn && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
            {
                fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            }
    }
    private void progressGameAndSetGameBoard(SHGameState state, SHGameState.SHGamePhase requiredGamePhase, int playerTurn, ArrayList chosenGameBoardValues) {
        state.gameBoardValues = chosenGameBoardValues;
        while (state.getGamePhase() != requiredGamePhase && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
        while (state.getCurrentPlayer() != playerTurn && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
    }

    private void progressGameOneRound(SHGameState state) {
        while (state.getGamePhase() != SHGameState.SHGamePhase.VotingOnLeader && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
        while (state.getCurrentPlayer() != state.getNPlayers()-1 && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
    }
    @Before
    public void setup() {
        players = Arrays.asList(new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer());
        secretHitler = GameType.SecretHitler.createGameInstance(5, 34, new SHParameters(-274));
        secretHitler.reset(players);
    }

    @Test
    public void checkingActionsForVotingOnLeaderPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,0);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnLeader){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,1);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnLeader){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,2);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnLeader){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,3);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnLeader){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,4);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnLeader){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
    }
    @Test
    public void checkingActionsForLeaderSelectsChancellorPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,0);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHChancellorSelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,1);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHChancellorSelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,2);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHChancellorSelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,3);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHChancellorSelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsChancellor,4);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHChancellorSelection.class);}}}
    }
    @Test
    public void checkingActionsForVotingOnChancellorPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        progressGame(state, SHGameState.SHGamePhase.VotingOnChancellor,0);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.VotingOnChancellor,1);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.VotingOnChancellor,2);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.VotingOnChancellor,3);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
        progressGame(state, SHGameState.SHGamePhase.VotingOnChancellor,4);
        if(state.getGamePhase() == SHGameState.SHGamePhase.VotingOnChancellor){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if(!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHVoting.class);}}}
    }

    @Test
    public void checkingActionsForLeaderSelectsPolicyPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsPolicy,0);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsPolicy,1);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsPolicy,2);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsPolicy,3);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsPolicy,4);
        if(state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() != state.getLeaderID() && !state.getDeceasedFellas().contains(state.getCurrentPlayer()))
                {assertEquals(actions.get(i).getClass(), SHWait.class);}
                else {assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}}}
    }

    @Test
    public void checkingActionsForChancellorSelectsPolicyPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        progressGame(state, SHGameState.SHGamePhase.ChancellorSelectsPolicy,0);
        if(state.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID()){assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.ChancellorSelectsPolicy,1);
        if(state.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID()){assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.ChancellorSelectsPolicy,2);
        if(state.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID()){assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.ChancellorSelectsPolicy,3);
        if(state.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID()){assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.ChancellorSelectsPolicy,4);
        if(state.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy){
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID()){assertEquals(actions.get(i).getClass(), SHPolicySelection.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())){assertEquals(actions.get(i).getClass(), SHWait.class);}}}
    }
    @Test
    public void checkingActionsForVetoPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        ArrayList chosenGameBoard = new ArrayList<>();
        for (int i = 0; i < 4; i++) {chosenGameBoard.add(false);}

        state.setGamePhase(SHGameState.SHGamePhase.Veto);
        state.gameBoardValues = chosenGameBoard;
        progressGame(state, SHGameState.SHGamePhase.Veto, 0);
        if (state.getGamePhase() == SHGameState.SHGamePhase.Veto) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID() || state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHVeto.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.Veto, 1);
        if (state.getGamePhase() == SHGameState.SHGamePhase.Veto) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID() || state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHVeto.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.Veto, 2);
        if (state.getGamePhase() == SHGameState.SHGamePhase.Veto) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID() || state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHVeto.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.Veto, 3);
        if (state.getGamePhase() == SHGameState.SHGamePhase.Veto) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID() || state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHVeto.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.Veto, 4);
        if (state.getGamePhase() == SHGameState.SHGamePhase.Veto) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getChancellorID() || state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHVeto.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
    }
    @Test
    public void checkingActionsForLeaderKillsPlayerPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        ArrayList chosenGameBoard = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            chosenGameBoard.add(false);
        }

        state.setGamePhase(SHGameState.SHGamePhase.LeaderKillsPlayer);
        state.gameBoardValues = chosenGameBoard;
        progressGame(state, SHGameState.SHGamePhase.LeaderKillsPlayer, 0);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderKillsPlayer) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if ( state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHKill.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderKillsPlayer, 1);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderKillsPlayer) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if ( state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHKill.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderKillsPlayer, 2);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderKillsPlayer) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                System.out.println(state.getDeceasedFellas() + " deceased");
                if ( state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHKill.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderKillsPlayer, 3);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderKillsPlayer) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                System.out.println(state.getDeceasedFellas() + " deceased");
                if ( state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHKill.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderKillsPlayer, 4);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderKillsPlayer) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                System.out.println(state.getDeceasedFellas() + " deceased");
                if ( state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHKill.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
    }
    @Test
    public void checkingActionsForLeaderInvestigatesPolicyPhaseTest() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        ArrayList chosenGameBoard = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            chosenGameBoard.add(false);
        }

        state.setGamePhase(SHGameState.SHGamePhase.LeaderPeeksTop3Cards);
        state.gameBoardValues = chosenGameBoard;
        progressGame(state, SHGameState.SHGamePhase.LeaderPeeksTop3Cards, 0);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderPeeksTop3Cards) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHLeaderPeeks.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderPeeksTop3Cards, 1);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderPeeksTop3Cards) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHLeaderPeeks.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderPeeksTop3Cards, 2);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderPeeksTop3Cards) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHLeaderPeeks.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderPeeksTop3Cards, 3);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderPeeksTop3Cards) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHLeaderPeeks.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
        progressGame(state, SHGameState.SHGamePhase.LeaderPeeksTop3Cards, 4);
        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderPeeksTop3Cards) {
            List<AbstractAction> actions = fm.computeAvailableActions(state);
            for (int i = 0; i < actions.size(); i++) {
                if (state.getCurrentPlayer() == state.getLeaderID()) {assertEquals(actions.get(i).getClass(), SHLeaderPeeks.class);}
                else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}

    }



    @Test
    public void checkingGameOverWithPoliciesCriteria() {

        SHGameState state = (SHGameState) secretHitler.getGameState();
        progressGameOneRound(state);
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 1);
            progressGameOneRound(state);
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 2);
            progressGameOneRound(state);
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 3);
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 4);
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            assertEquals(state.getGameBoardValues().size(), 5);
        }

        if ( Collections.frequency(state.getGameBoardValues(), true) == 5)
        {
            assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ){progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 6);
            if ( Collections.frequency(state.getGameBoardValues(), true) == 5 || Collections.frequency(state.getGameBoardValues(), false) == 6) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 7);
            if (Collections.frequency(state.getGameBoardValues(), true) == 5 || Collections.frequency(state.getGameBoardValues(), false) == 6) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 8);
            if (Collections.frequency(state.getGameBoardValues(), true) == 5 || Collections.frequency(state.getGameBoardValues(), false) == 6) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 9);
            if (Collections.frequency(state.getGameBoardValues(), true) == 5 || Collections.frequency(state.getGameBoardValues(), false) == 6) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 10);
            if (Collections.frequency(state.getGameBoardValues(), true) == 5 || Collections.frequency(state.getGameBoardValues(), false) == 6) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }

        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {progressGameOneRound(state);}
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END ) {
            assertEquals(state.getGameBoardValues().size(), 11);
            if (Collections.frequency(state.getGameBoardValues(), true) == 5 || Collections.frequency(state.getGameBoardValues(), false) == 6) {
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }
    }
//    @Test
//    public void checkingGameOverWithFailedVotesCriteria() {
//
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        while ( state.getFailedVoteCounter() != 5 && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
//        {
//            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//        }
//        //fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//        if(state.getFailedVoteCounter() == 5){
//
//            assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
//        }
//    }
//
//    @Test
//    public void checkingHandInitialisation() {
//
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        for (int i = 0; i < state.getNPlayers(); i++) {
//            assertEquals(state.getPlayerHandCards().get(i).getSize(), 3);
//            if (state.getPlayerHandCards().get(i).get(2).cardType != ResPlayerCards.CardType.secretHitler && state.getPlayerHandCards().get(i).get(2).cardType != ResPlayerCards.CardType.SPY)
//            {throw new AssertionError("last card isn't SPY or secretHitler");}
//            if (state.getPlayerHandCards().get(i).get(0).cardType != ResPlayerCards.CardType.No && state.getPlayerHandCards().get(i).get(0).cardType != ResPlayerCards.CardType.Yes)
//            {throw new AssertionError("first card isn't yes or no");}
//            if (state.getPlayerHandCards().get(i).get(1).cardType != ResPlayerCards.CardType.Yes && state.getPlayerHandCards().get(i).get(1).cardType != ResPlayerCards.CardType.No)
//            {throw new AssertionError("second card isn't yes or no");}
//        }
//
//    }
//    @Test
//    public void checkingCorrectSpyTosecretHitlerRatio()
//    {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        int spyCount = 0;
//        int secretHitlerCount = 0;
//        for (int i = 0; i < state.getNPlayers(); i++) {
//            assertEquals(state.getPlayerHandCards().get(i).getSize(), 3);
//            if (state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.secretHitler)
//            {secretHitlerCount += 1;}
//            if (state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.SPY)
//            {spyCount += 1;}
//        }
//        assertEquals(secretHitlerCount, state.factions[0]);
//        assertEquals(spyCount, state.factions[1]);
//    }
//
//    @Test
//    public void checkingWinnersAreCorrect()
//    {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        while (CoreConstants.GameResult.GAME_END != state.getGameStatus())
//        {
//            progressGameOneRound(state);
//        }
//
//        for (int i = 0; i < state.getNPlayers()-1; i++) {
//            if(state.getWinners() == 0)
//            {
//                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.secretHitler)
//                {assertEquals( CoreConstants.GameResult.WIN,state.getPlayerResults()[i]);}
//                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.SPY)
//                {assertEquals(CoreConstants.GameResult.LOSE,state.getPlayerResults()[i] );}
//            }
//
//            if(state.getWinners() == 1)
//            {
//                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.secretHitler)
//                {assertEquals(CoreConstants.GameResult.LOSE,state.getPlayerResults()[i] );}
//                if(state.getPlayerHandCards().get(i).get(2).cardType == ResPlayerCards.CardType.SPY)
//                {assertEquals( CoreConstants.GameResult.WIN,state.getPlayerResults()[i]);}
//            }
//        }
//    }
//
//    @Test
//    public void checkingLeaderMovesAfterFailedTeamVote() {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        progressGame(state, SHGameState.SHGamePhase.TeamSelectionVote, state.getNPlayers() -1);
//        int previousLeader = state.getLeaderID();
//
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//        if(state.getVoteSuccess() == false) {assertNotEquals( previousLeader,state.getLeaderID());}
//        else {assertEquals( previousLeader,state.getLeaderID());}
//    }
//
//    @Test
//    public void checkingTeamSize() {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//
//        //Checking Round 0
//        progressGame(state, SHGameState.SHGamePhase.TeamSelectionVote, 0);
//        assertEquals(state.gameBoard.getMissionSuccessValues()[state.getRoundCounter()],state.getFinalTeam().size());
//
//        //Checking Round 1
//        progressGame(state, SHGameState.SHGamePhase.TeamSelectionVote, 1);
//        assertEquals(state.gameBoard.getMissionSuccessValues()[state.getRoundCounter()],state.getFinalTeam().size());
//
//        //Checking Round 2
//        progressGame(state, SHGameState.SHGamePhase.TeamSelectionVote, 2);
//        assertEquals(state.gameBoard.getMissionSuccessValues()[state.getRoundCounter()],state.getFinalTeam().size());
//
//        //Checking Round 3
//        progressGame(state, SHGameState.SHGamePhase.TeamSelectionVote, 3);
//        assertEquals(state.gameBoard.getMissionSuccessValues()[state.getRoundCounter()],state.getFinalTeam().size());
//
//        //Checking Round 4
//        progressGame(state, SHGameState.SHGamePhase.TeamSelectionVote, 4);
//        assertEquals(state.gameBoard.getMissionSuccessValues()[state.getRoundCounter()],state.getFinalTeam().size());
//    }
//
//    @Test
//    public void checkingLeaderMovesAfterRoundEnds() {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        progressGame(state, SHGameState.SHGamePhase.MissionVote, state.getNPlayers() -1);
//        int previousLeader = state.getLeaderID();
//        int previousRound = state.getRoundCounter();
//
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//        if(previousRound != state.getRoundCounter()) {assertNotEquals( previousLeader,state.getLeaderID());}
//    }
//
//    @Test
//    public void checkingSpiesKnowEveryonesCards() {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        List<ResPlayerCards.CardType> listOfIdentityCards =  new ArrayList<>();
//        for (int i = 0; i < state.getNPlayers(); i++) {
//            listOfIdentityCards.add(state.getPlayerHandCards().get(i).get(2).cardType);
//        }
//
//        //Checking Player 0
//        SHGameState playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingSpiesKnowEveryonesCardsMethod(state, playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 1
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingSpiesKnowEveryonesCardsMethod(state, playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 2
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingSpiesKnowEveryonesCardsMethod(state, playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 3
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingSpiesKnowEveryonesCardsMethod(state, playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 4
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingSpiesKnowEveryonesCardsMethod(state, playerState,listOfIdentityCards);
//    }
//
//    @Test
//    public void checkingsecretHitlerDontKnowEveryonesCards() {
//        //This Test May Fail Due To A Random Arrangement Of Hidden Cards Aligning With Actual Identity Cards, I have minimised this
//        // Chance by checking multiple secretHitler members views of other players hands
//        // If atleast one hand is not equal the default copy/gamestate, the hand redeterminisation works
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//
//        List<ResPlayerCards.CardType> listOfIdentityCards =  new ArrayList<>();
//        for (int i = 0; i < state.getNPlayers(); i++) {
//            listOfIdentityCards.add(state.getPlayerHandCards().get(i).get(2).cardType);
//        }
//
//        //Checking Player 0
//        SHGameState playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingsecretHitlerDontKnowEveryonesCardsMethod(state,playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 1
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingsecretHitlerDontKnowEveryonesCardsMethod(state,playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 2
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingsecretHitlerDontKnowEveryonesCardsMethod(state,playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 3
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingsecretHitlerDontKnowEveryonesCardsMethod(state,playerState,listOfIdentityCards);
//        fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
//
//        //Checking Player 4
//        playerState = (SHGameState) state.copy(state.getCurrentPlayer());
//        checkingsecretHitlerDontKnowEveryonesCardsMethod(state,playerState,listOfIdentityCards);
//
//        assertNotEquals(atLeastOneHandRearranged, 0);
//    }
//
//
//private void checkingSpiesKnowEveryonesCardsMethod(SHGameState state,SHGameState playerState, List<ResPlayerCards.CardType> listOfIdentityCards)
//{
//    if(state.getPlayerHandCards().get(state.getCurrentPlayer()).get(2).cardType == ResPlayerCards.CardType.SPY)
//    {
//        List<ResPlayerCards.CardType> listOfSpyKnownIdentityCards =  new ArrayList<>();
//        for (int j = 0; j < state.getNPlayers(); j++) {
//            listOfSpyKnownIdentityCards.add(playerState.getPlayerHandCards().get(j).get(2).cardType);
//        }
//        assertEquals(listOfSpyKnownIdentityCards,listOfIdentityCards);
//    }}
//
//    private void checkingsecretHitlerDontKnowEveryonesCardsMethod(SHGameState state,SHGameState playerState, List<ResPlayerCards.CardType> listOfIdentityCards)
//    {
//        if (state.getPlayerHandCards().get(state.getCurrentPlayer()).get(2).cardType == ResPlayerCards.CardType.secretHitler) {
//            List<ResPlayerCards.CardType> listOfsecretHitlerKnownIdentityCards = new ArrayList<>();
//            for (int j = 0; j < state.getNPlayers(); j++) {
//                listOfsecretHitlerKnownIdentityCards.add(playerState.getPlayerHandCards().get(j).get(2).cardType);
//            }
//
//            if(listOfsecretHitlerKnownIdentityCards != listOfIdentityCards) {atLeastOneHandRearranged += 1;}
//        }
//    }

    //
//    @Test
//    public void testTeamVoteNumber() {
//        SHGameState state = (SHGameState) secretHitler.getGameState();
//        fm.next(state, new RollDice());
//        do {
//            fm.next(state, fm.computeAvailableActions(state).get(0));
//            fm.next(state, new RollDice());
//            // we keep rolling dice until we go bust
//        } while (!fm.computeAvailableActions(state).get(0).equals(new Pass(true)));
//        fm.next(state, new Pass(true));
//        assertEquals(CantStopGamePhase.Decision, state.getGamePhase());
//    }


}
