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
import org.apache.spark.internal.config.R;
import org.junit.Before;
import org.junit.Test;
import players.simple.RandomPlayer;
import utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class TestSecretHitler {

    public int atLeastOneHandRearranged = 0;
    Game secretHitler;
    List<AbstractPlayer> players;
    SHForwardModel fm = new SHForwardModel();
    RandomPlayer rnd = new RandomPlayer();


    private void progressGame(SHGameState state, SHGameState.SHGamePhase requiredGamePhase, int playerTurn) {
        int tracker = 0;
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
        boolean firstPhaseCheck = false;
        while ( state.getGameStatus() != CoreConstants.GameResult.GAME_END && !firstPhaseCheck)
        {
            for (int i = 0; i < state.getNPlayers(); i++) {
                if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
                    fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
                }
            }

            if (state.getGamePhase() == SHGameState.SHGamePhase.VotingOnLeader) {firstPhaseCheck = true;}
        }
        while (state.getCurrentPlayer() != state.getNPlayers()-1 && state.getGameStatus() != CoreConstants.GameResult.GAME_END)
        {
            if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
                fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            }
        }
        if(state.getGameStatus() != CoreConstants.GameResult.GAME_END) {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
        }
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
    public void checkingActionsForLeaderSelectsLeaderPhaseTest() {
        for (int np = 7; np < 11; np++) {
            Game game = GameType.SecretHitler.createGameInstance(np, 34, new SHParameters(-274));
            players = new ArrayList<>();
            for (int j = 0; j < np; j++) {
                players.add(new RandomPlayer());
            }

            SHGameState state = (SHGameState) game.getGameState();
            game.reset(players);
            ArrayList chosenGameBoard = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                chosenGameBoard.add(false);
            }

            state.setGamePhase(SHGameState.SHGamePhase.LeaderSelectsLeader);
            state.gameBoardValues = chosenGameBoard;
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 0);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 1);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 2);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 3);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 4);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 5);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 6);
            if (np > 7) {
                if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                    List<AbstractAction> actions = fm.computeAvailableActions(state);
                    for (int i = 0; i < actions.size(); i++) {
                        if (state.getCurrentPlayer() == state.getLeaderID()) {
                            assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                        } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
                progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 7);
                if (np > 8) {
                    if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                        List<AbstractAction> actions = fm.computeAvailableActions(state);
                        for (int i = 0; i < actions.size(); i++) {
                            if (state.getCurrentPlayer() == state.getLeaderID()) {
                                assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                            } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
                    progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 8);
                    if (np > 9) {
                        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderSelectsLeader) {
                            List<AbstractAction> actions = fm.computeAvailableActions(state);
                            for (int i = 0; i < actions.size(); i++) {
                                if (state.getCurrentPlayer() == state.getLeaderID()) {
                                    assertEquals(actions.get(i).getClass(), SHLeaderSelectsLeader.class);
                                } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
                        progressGame(state, SHGameState.SHGamePhase.LeaderSelectsLeader, 9);

                    }}}}}

    @Test
    public void checkingActionsForLeaderInvestigatesIdentityPhaseTest() {
        for (int np = 7; np < 11; np++) {
            Game game = GameType.SecretHitler.createGameInstance(np, 34, new SHParameters(-274));
            players = new ArrayList<>();
            for (int j = 0; j < np; j++) {
                players.add(new RandomPlayer());
            }

            SHGameState state = (SHGameState) game.getGameState();
            game.reset(players);
            ArrayList chosenGameBoard = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                chosenGameBoard.add(false);
            }

            state.setGamePhase(SHGameState.SHGamePhase.LeaderInvestigatesPlayer);
            state.gameBoardValues = chosenGameBoard;
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 0);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 1);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 2);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 3);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 4);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 5);
            if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                List<AbstractAction> actions = fm.computeAvailableActions(state);
                for (int i = 0; i < actions.size(); i++) {
                    if (state.getCurrentPlayer() == state.getLeaderID()) {
                        assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                    } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
            progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 6);
            if (np > 7) {
                if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                    List<AbstractAction> actions = fm.computeAvailableActions(state);
                    for (int i = 0; i < actions.size(); i++) {
                        if (state.getCurrentPlayer() == state.getLeaderID()) {
                            assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                        } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
                progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 7);
                if (np > 8) {
                    if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                        List<AbstractAction> actions = fm.computeAvailableActions(state);
                        for (int i = 0; i < actions.size(); i++) {
                            if (state.getCurrentPlayer() == state.getLeaderID()) {
                                assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                            } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
                    progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 8);
                    if (np > 9) {
                        if (state.getGamePhase() == SHGameState.SHGamePhase.LeaderInvestigatesPlayer) {
                            List<AbstractAction> actions = fm.computeAvailableActions(state);
                            for (int i = 0; i < actions.size(); i++) {
                                if (state.getCurrentPlayer() == state.getLeaderID()) {
                                    assertEquals(actions.get(i).getClass(), SHInvestigateIdentity.class);
                                } else if (!state.getDeceasedFellas().contains(state.getCurrentPlayer())) {assertEquals(actions.get(i).getClass(), SHWait.class);}}}
                        progressGame(state, SHGameState.SHGamePhase.LeaderInvestigatesPlayer, 9);

                    }}}}}



    @Test
    public void checkingGameOverWithPoliciesCriteria() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        int intTracker = 0;
        while ((Collections.frequency(state.getGameBoardValues(), true) < 5 || Collections.frequency(state.getGameBoardValues(), false) < 6) && CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            if (state.getDeceasedFellas().contains( state.getHitlerID())) {System.out.println("Hitler has been killed");}
            if (state.getDeceasedFellas().contains( state.getLeaderID())) {System.out.println("Leader Is Dead");}
            if (state.getDeceasedFellas().contains( state.getChancellorID())) {System.out.println("CHancellor Is Dead");}

            System.out.println("Stuck here");
            intTracker += 1;
            System.out.println(intTracker);
        }
        assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
    }
    @Test
    public void checkingGameOverWithHitlerTakingPower() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        boolean hitlerInPower = false;
        while ( !hitlerInPower && CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            if((Collections.frequency(state.getGameBoardValues(), false) > 3) && state.getGamePhase() == SHGameState.SHGamePhase.ChancellorSelectsPolicy && state.getChancellorID() == state.getHitlerID()){hitlerInPower = true;}
        }
        assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
    }

    @Test
    public void checkingActionSizeIsAlwaysBiggerThan0() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        while (CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            assertNotEquals(fm.computeAvailableActions(state).size(), 0);
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));

        }


    }
    @Test
    public void checkingGameOverWithHitlerDying() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        boolean hitlerDead = false;
        while ( hitlerDead == false  && CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            if((Collections.frequency(state.getGameBoardValues(), false) > 3) && state.getDeceasedFellas().contains(state.getHitlerID()))
            {
                hitlerDead = true;
                assertEquals(CoreConstants.GameResult.GAME_END, state.getGameStatus());
            }
        }
    }
    @Test
    public void checkingDrawPileIsNoLargerThan17() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        while ( CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            assertTrue(state.drawPile.getSize() < 18);
        }
    }
    @Test
    public void checkingDiscardPileIsNoLargerThan17() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        while ( CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            assertTrue(state.discardPile.getSize() < 18);
        }
    }

    @Test
    public void checkingLeaderAndChancellorAreNeverTheSamePlayer() {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        while ( CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            fm.next(state, rnd._getAction(state, fm.computeAvailableActions(state)));
            assertNotEquals(state.getLeaderID(),state.getChancellorID());
        }
    }

    @Test
    public void checkingCorrectFascistToLiberalRatio()
    {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        int liberalCount = 0;
        int fascistCount = 0;
        for (int i = 0; i < state.getNPlayers(); i++) {
            assertEquals(state.getPlayerHandCards().get(i).getSize(), 4);
            if (state.getPlayerHandCards().get(i).get(2).cardType == SHPlayerCards.CardType.Fascist )
            {fascistCount += 1;}
            if (state.getPlayerHandCards().get(i).get(2).cardType == SHPlayerCards.CardType.Liberal)
            {liberalCount += 1;}
        }
        assertEquals(fascistCount, state.factions[1]);
        assertEquals(liberalCount, state.factions[0]);
    }
//
    @Test
    public void checkingWinnersAreCorrect()
    {
        SHGameState state = (SHGameState) secretHitler.getGameState();
        while (CoreConstants.GameResult.GAME_END != state.getGameStatus())
        {
            progressGameOneRound(state);
        }

        for (int i = 0; i < state.getNPlayers()-1; i++) {
            if(state.getWinners() == 1)
            {
                if(state.getPlayerHandCards().get(i).get(2).cardType == SHPlayerCards.CardType.Fascist)
                {assertEquals( CoreConstants.GameResult.WIN,state.getPlayerResults()[i]);}
                if(state.getPlayerHandCards().get(i).get(2).cardType == SHPlayerCards.CardType.Liberal)
                {assertEquals(CoreConstants.GameResult.LOSE,state.getPlayerResults()[i] );}
            }

            if(state.getWinners() == 0)
            {
                if(state.getPlayerHandCards().get(i).get(2).cardType == SHPlayerCards.CardType.Fascist)
                {assertEquals(CoreConstants.GameResult.LOSE,state.getPlayerResults()[i] );}
                if(state.getPlayerHandCards().get(i).get(2).cardType == SHPlayerCards.CardType.Liberal)
                {assertEquals( CoreConstants.GameResult.WIN,state.getPlayerResults()[i]);}
            }
        }
    }
}
