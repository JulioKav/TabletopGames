package games.jaipurskeleton.stats;


import core.actions.AbstractAction;
import evaluation.listeners.GameListener;
import evaluation.metrics.AbstractMetric;
import evaluation.metrics.AbstractParameterizedMetric;
import evaluation.metrics.Event;
import evaluation.metrics.IMetricsCollection;
import evaluation.summarisers.TAGStatSummary;
import games.jaipurskeleton.JaipurGameState;
import games.jaipurskeleton.actions.TakeCards;
import games.jaipurskeleton.components.JaipurCard;
import utilities.Group;

import java.util.*;

import static scala.Console.print;

public  class JaipurMetrics implements IMetricsCollection {

    public static class CamelTokenWinner extends AbstractMetric {


        @Override
        public Object run(GameListener listener, Event e) {
            JaipurGameState gs = (JaipurGameState) e.state;
            double winner = 0;
            double CamelWinner = 0;
            int playerScore = 0;
            int playerScoreChallenger = 0;
            int playerCamels = 0;
            int playerCamelsChallenger = 0;
            int camelTokenIsWinner = 0;
            for (int i = 0; i < gs.getNPlayers(); i++) {
                playerScoreChallenger = gs.getPlayerScores().get(i).getValue();
                playerCamelsChallenger = gs.getPlayerHerds().get(i).getValue();
                if (playerScore < playerScoreChallenger) {
                    winner = i;
                    playerScore = playerScoreChallenger;
                }

                if (playerCamels < playerCamelsChallenger) {
                    CamelWinner = i;
                    playerCamels = playerCamelsChallenger;
                }

                //check if camel token
                if (CamelWinner == winner) {
                    camelTokenIsWinner = 1;
                } else {
                    camelTokenIsWinner = 0;
                }


            }
            return camelTokenIsWinner;
        }

        @Override
        public Set<Event.GameEvent> getEventTypes() {
            return Collections.singleton(Event.GameEvent.ROUND_OVER);
        }
    }



    public static class WinnerGoesFirst extends AbstractMetric {
        @Override
        public Object run(GameListener listener, Event e) {
            JaipurGameState gs = (JaipurGameState) e . state ;
            double winner = 0;
            int playerScore = 0;
            int playerScoreChallenger = 0;
            for (int i = 0; i < gs . getNPlayers () ; i ++) {
                playerScoreChallenger = gs .getPlayerNRoundsWon().get(i).getValue();

                if(playerScore < playerScoreChallenger)
                {

                    winner = i;
                    playerScore = playerScoreChallenger;
                }



            }
            return winner;
        }

        @Override
        public Set<Event.GameEvent> getEventTypes() {
            return Collections. singleton ( Event . GameEvent . GAME_OVER ) ;
        }
    }

    public static class RoundScoreDifference extends AbstractMetric {

        @Override
        public Object run(GameListener listener, Event e) {
            JaipurGameState gs = (JaipurGameState) e . state ;
            double scoreDiff = 0;
             for (int i = 0; i < gs . getNPlayers () -1; i ++) {
                 scoreDiff += Math . abs ( gs . getPlayerScores () . get ( i ) . getValue () -
                         gs . getPlayerScores () . get ( i +1) . getValue () ) ;
                 }
             return scoreDiff /( gs . getNPlayers () -1) ;


        }

        @Override
        public Set<Event.GameEvent> getEventTypes() {
            return Collections. singleton ( Event . GameEvent . ROUND_OVER ) ;
        }
    }

    public static class PurchaseFromMarket extends AbstractParameterizedMetric {
        public PurchaseFromMarket () { super () ;}
        public PurchaseFromMarket ( Object arg ) { super ( arg ) ;}

        @Override
        public Object run(GameListener listener, Event e) {
            AbstractAction action = e . action ;
            JaipurCard . GoodType goodType = ( JaipurCard . GoodType ) getParameterValue ( " goodType " ) ;
            if ( action instanceof TakeCards ) {
                 TakeCards tc = ( TakeCards ) action ;
                 if ( tc . howManyPerTypeTakeFromMarket . containsKey ( goodType ) )
                     return 1;
                 }
             return null ;
        }

        @Override
        public Set<Event.GameEvent> getEventTypes() {
            return Collections . singleton ( Event . GameEvent . ACTION_CHOSEN ) ;
        }

        @Override
        public List<Group<String, List<?>, ?>> getAllowedParameters() {
            return Collections . singletonList (new Group < >( " goodType " , Arrays. asList (
                    JaipurCard. GoodType . values () ) , JaipurCard . GoodType . Diamonds ) ) ;
        }
        @Override
            public Map < String , Object > postProcessingGameOver ( Event e , TAGStatSummary
                recordedData ) {
             // Process the recorded data during the game and return game over summarised data
             Map< String , Object > toRecord = new HashMap < >() ;
             int nTakeCardsActions = 0;
             for ( AbstractAction aa : e . state . getHistory () ) {
                 if ( aa instanceof TakeCards ) nTakeCardsActions ++;
                 }
             toRecord . put ( getName () + " : " + e . type , 100* recordedData . n () /
                    nTakeCardsActions ) ;
             return toRecord ;
             }

    }
}
