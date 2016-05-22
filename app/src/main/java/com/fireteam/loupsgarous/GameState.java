package com.fireteam.loupsgarous;

import android.util.Log;
import android.widget.Toast;

import com.fireteam.loupsgarous.activities.MainActivity;
import com.fireteam.loupsgarous.player.KillState;
import com.fireteam.loupsgarous.player.Player;
import com.fireteam.loupsgarous.player.PlayerType;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Philoupe on 18/05/2016.
 */
public class GameState {


    private Player[] players;
    private int leader;
    private int[] votes;
    private int nbPlayers;
    private int currentPlayers;
    PlayerType[] middleCards;
    TurnType turnType;
    private int lastPlayedIdPlayer;
    private MainActivity mainActivity;
    private boolean launchKillPlayer;
    private boolean launchSetLeader;
    private String currentTurnActions;

    public GameState(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        currentTurnActions = "";
    }

    public String getPlayerName(int playerId)
    {
        return players[playerId].getDisplayName();
    }

    public String getLeaderParticipantId()
    {
        for (int i = 0; i < nbPlayers; i++)
            if(players[i].isLeader())
                return players[i].getParticipantId();
        return null;
    }

    public String getHunterParticipantId()
    {
        for (int i = 0; i < nbPlayers; i++)
            if(players[i].getType() == PlayerType.HUNTER)
                return players[i].getParticipantId();
        return null;
    }

    public TurnType getTurnType()
    {
        return turnType;
    }

    public String getNextPlayerByType(PlayerType type, TurnType nextType, boolean killPlayer)
    {
        while(lastPlayedIdPlayer < nbPlayers - 1) {
            ++lastPlayedIdPlayer;
            if (players[lastPlayedIdPlayer].isAlive() && players[lastPlayedIdPlayer].getType() == type)
                return players[lastPlayedIdPlayer].getParticipantId();
        }
        lastPlayedIdPlayer = -1;
        turnType = nextType;
        Log.i("TURNS", " Return null : " + turnType);
        if(!killPlayer)
            return getNextPlayerTurn();
        launchKillPlayer = true;
        return null;
    }

    public String getNextPlayerTurn()
    {
        Log.i("TURNS", "getNextPlayerTurn");
        if(currentPlayers == nbPlayers)
        {
            Log.i("TURNS", " currentPlayers == nbPlayers / type  : " + turnType);
            switch (turnType)
            {
                case WAITING_PLAYERS:
                    turnType = TurnType.INIT_GAME_CUPIDON;
                    return getNextPlayerTurn();
                case INIT_GAME_THIEF:
                    return getNextPlayerByType(PlayerType.THIEF, TurnType.INIT_GAME_CUPIDON, false);
                case INIT_GAME_CUPIDON:
                    return getNextPlayerByType(PlayerType.CUPIDON, TurnType.VOTE_FOR_LEADER, false);
                case VOTE_FOR_LEADER:
                    ++lastPlayedIdPlayer;
                    if(lastPlayedIdPlayer < nbPlayers)
                        return players[lastPlayedIdPlayer].getParticipantId();
                    else {
                        turnType = TurnType.SEER_TURN;
                        launchSetLeader = true;
                    }
                    break;
                case SEER_TURN:
                    return getNextPlayerByType(PlayerType.SEER, TurnType.NIGHT, false);
                case NIGHT:
                    return getNextPlayerByType(PlayerType.WEREWOLF, TurnType.WITCH_TURN, false);
                case WITCH_TURN:
                    return getNextPlayerByType(PlayerType.WITCH, TurnType.DAY, true);
                case DAY:
                    while(lastPlayedIdPlayer < nbPlayers - 1) {
                        ++lastPlayedIdPlayer;
                        if (players[lastPlayedIdPlayer].isAlive())
                            return players[lastPlayedIdPlayer].getParticipantId();
                    }
                    launchKillPlayer = true;
                    turnType = TurnType.SEER_TURN;
                    break;
            }
        }
        Log.i("TURNS", " Return null : " + turnType);
        return null;
    }


    public void init(int nbPlayers)
    {
        this.nbPlayers = nbPlayers;
        currentPlayers = 0;
        votes = new int[nbPlayers];
        players = new Player[nbPlayers];
        middleCards = new PlayerType[2];
        turnType = TurnType.WAITING_PLAYERS;
        launchKillPlayer = false;
    }

    public int addPlayer(String participantId, String displayName)
    {
        Log.i("INIT", "Add Player ! ");
        Random rand = new Random();
        PlayerType type;
        int playerId = currentPlayers;
        Player p = new Player(playerId, participantId, displayName);
        players[playerId] = p;
        if(currentPlayers + 1 == nbPlayers)
        {
            Log.i("INIT", "INIT ALL PLAYERS TYPES ! ");
            boolean witch = false, cupidon = false, thief = false, hunter = false, seer = false;
            for(int i = 0; i < nbPlayers + 2; i++)
            {
                if(rand.nextInt() % 2 > 0) {
                    type = PlayerType.WEREWOLF;
                }
                else if(!witch && rand.nextInt() % 2 > 0) {
                    type = PlayerType.WITCH;
                    witch = true;
                }
                else if(!cupidon && rand.nextInt() % 2 > 0) {
                    type = PlayerType.CUPIDON;
                    cupidon = true;
                }
                else if(!thief && rand.nextInt() % 2 > 0) {
                    type = PlayerType.THIEF;
                    thief = true;
                }
                else if(!hunter && rand.nextInt() % 2 > 0) {
                    type = PlayerType.HUNTER;
                    hunter = true;
                }
                else if(!seer && rand.nextInt() % 2 > 0) {
                    type = PlayerType.SEER;
                    seer = true;
                }
                else {
                    type = PlayerType.VILLAGER;
                }
                if(i < nbPlayers) {
                    players[i].setType(type);
                }
                else
                {
                    middleCards[i - nbPlayers] = type;
                }
                turnType = TurnType.INIT_GAME_THIEF;
            }
        }
        ++currentPlayers;
        Log.i("INIT", "Players : " + currentPlayers);
        return playerId;
    }

    public byte[] serialize() throws JSONException {
        JSONArray state = new JSONArray();
        state.put(0, nbPlayers);
        state.put(1, currentPlayers);
        state.put(2, leader);
        if( middleCards[0] == null)
            state.put(3, -1);
        else
            state.put(3, middleCards[0].getValue());
        if( middleCards[1] == null)
            state.put(4, -1);
        else
            state.put(4, middleCards[1].getValue());
        if( turnType == null)
            state.put(5, -1);
        else
            state.put(5, turnType.getValue());
        state.put(6, lastPlayedIdPlayer);
        state.put(7, launchKillPlayer);
        state.put(8, launchSetLeader);
        state.put(9, currentTurnActions);
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            state.put(index + 10, votes[index]);
        }
        int nextIndex = nbPlayers + 10, playerSize = 0;
        for(index = 0; index < currentPlayers; index++)
        {
            playerSize = players[index].serialize(state, nextIndex);
            nextIndex += playerSize;
        }
        Log.i("SERIALIZE", state.toString());
        return state.toString().getBytes();
    }

    public ArrayList<String> getPlayersNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        for(int i = 0; i < currentPlayers; i++)
        {
            names.add(players[i].getDisplayName());
        }
        return names;
    }


    public GameState unserialize(byte[] data) throws JSONException {
        Log.i("Serialize0", "Unserialize");
        JSONArray state = new JSONArray(new String(data));
        Log.i("UNSERIALIZE", state.toString());
        nbPlayers = state.getInt(0);
        currentPlayers = state.getInt(1);
        leader = state.getInt(2);
        if(state.getInt(3) == -1)
            middleCards[0] = null;
        else
            middleCards[0] = PlayerType.values()[state.getInt(3)];
        if(state.getInt(4) == -1)
            middleCards[1] = null;
        else
            middleCards[1] = PlayerType.values()[state.getInt(4)];
        if(state.getInt(5) == -1)
            turnType = null;
        else
            turnType = TurnType.values()[state.getInt(5)];
        lastPlayedIdPlayer = state.getInt(6);
        launchKillPlayer = state.getBoolean(7);
        launchSetLeader = state.getBoolean(8);
        currentTurnActions = state.getString(9);
        if(!currentTurnActions.isEmpty())
            Toast.makeText(mainActivity.getApplicationContext(), currentTurnActions, Toast.LENGTH_LONG).show();
        currentTurnActions = "";
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            votes[index] = state.getInt(index + 10);
        }
        Log.i("Serialize0", "Unserialize players");
        int nextIndex = nbPlayers + 10, playerSize = 0;
        for(index = 0; index < currentPlayers; index++)
        {
            if(players[index] == null) {
                players[index] = new Player();
                Log.i("Serialize0", "Create player "+index);
            }
            playerSize = players[index].unserialize(state, nextIndex);
            nextIndex += playerSize;
        }
        return this;
    }

    public void executeNextTurnActions()
    {
        Log.i("GAME", "Kill : " + launchKillPlayer + " Leader : " + launchSetLeader);
        if(launchKillPlayer)
            killPlayer(-1);
        if(launchSetLeader)
            setLeader(-1);
    }

    public String getPlayersToString()
    {
        String s = new String("");
        for(int index = 0; index < currentPlayers; index++)
        {
            if( players[index] != null)
                s += "    Player " + index + " : " + players[index].toString();
            else
                s += "    Player " + index + " is empty !! ";
        }
        return s;
    }

    public String toString() {
        return new String("Games Data : \n" +
                nbPlayers + " players MAX \n" +
                currentPlayers + " players \n" +
                "Leader : " + leader + "\n" +
                "middle 1 " + middleCards[0] + "\n"+
                "middle 2 " + middleCards[1] + "\n"+
                "Last player " + lastPlayedIdPlayer + "\n" +
                "Kill " + launchKillPlayer + "\n" +
                "Elect " + launchSetLeader + "\n" +
                "Players :  " + players.length + "\n" +
                getPlayersToString()
        );
    }

    public void voteToKillPlayer(int playerId)
    {
        votes[playerId] += 1;
    }

    public boolean isPlayerAlive(int playerId)
    {
        return players[playerId].isAlive();
    }

    public Player getPlayerToKill()
    {
        int best = -1, bestScore = -1;
        boolean ok = true;
        for(int i = 0; i < nbPlayers; i++)
        {
            if(votes[i] > bestScore)
            {
                ok = true;
                best = i;
                bestScore = votes[i];
            }
            else if(votes[i] == bestScore)
                ok = false;
            votes[i] = 0;
        }
        if(ok)
            return players[best];
        else
            return null;

    }


    public KillState killPlayer(int playerIdToKill)
    {
        KillState killState;
        Player playerToKill;
        if(playerIdToKill == -1)
            playerToKill = getPlayerToKill();
        else
            playerToKill = players[playerIdToKill];
        if(playerToKill != null && playerToKill.isAlive()) {
            playerToKill.kill();
            currentTurnActions += playerToKill.getDisplayName()+ " " +  mainActivity.getResources().getString(R.string.died) + " " + playerToKill.getTypeName() + ". " ;
            if (playerToKill.getLoverId() != -1) {
                currentTurnActions += mainActivity.getResources().getString(R.string.lover) + " " + players[playerToKill.getLoverId()].getDisplayName() + " " + mainActivity.getResources().getString(R.string.was) + " " + playerToKill.getTypeName();
                players[playerToKill.getLoverId()].kill();
                if(playerToKill.getType() == PlayerType.HUNTER) {
                    if (players[playerToKill.getLoverId()].isLeader() || playerToKill.isLeader())
                        killState = KillState.KILLED_HUNTER_LEADER_AND_LOVER;
                    else
                        killState = KillState.KILLED_HUNTER_AND_LOVER;
                }
                else {
                    if (players[playerToKill.getLoverId()].isLeader() || playerToKill.isLeader())
                        killState = KillState.KILLED_WITH_LOVER_AND_LEADER;
                    else
                        killState = KillState.KILLED_WITH_LOVER;
                }
            }
            else if(playerToKill.getType() == PlayerType.HUNTER){
                if (playerToKill.isLeader())
                    killState = KillState.KILLED_HUNTER_AND_LEADER;
                else
                    killState = KillState.KILLED_HUNTER;
            }
            else{
                if (playerToKill.isLeader())
                    killState = KillState.KILLED_LEADER;
                else
                    killState = KillState.KILLED;
            }

        }
        else
            killState = KillState.NONE_KILLED;
        launchKillPlayer = false;
        return killState;
    }

    public void setLovers(int player1, int player2)
    {
        players[player1].setLoverId(player2);
        players[player2].setLoverId(player1);
    }

    public void voteToSetLeader(int playerId)
    {
        votes[playerId] += 1;
    }

    public int getPlayerIdToSetLeader() {
        int best = -1, bestScore = -1;
        for (int i = 0; i < nbPlayers; i++) {
            if (votes[i] > bestScore) {
                best = i;
                bestScore = votes[i];
            }
            votes[i] = 0;
        }
        return best;
    }

    public void setLeader(int playerIdToSetLeader)
    {
        if(playerIdToSetLeader == - 1)
            playerIdToSetLeader = getPlayerIdToSetLeader();
        players[playerIdToSetLeader].setLeader();
        currentTurnActions += players[playerIdToSetLeader].getDisplayName() + " " +  mainActivity.getResources().getString(R.string.new_leader);
        leader = playerIdToSetLeader;
        launchSetLeader = false;
    }

    public String getPlayerType(int playerId)
    {
        if(playerId == -1)
            return "No player selected";
        else
            return players[playerId].getTypeName();
    }

    public PlayerType[] getCardsForThief()
    {
        return middleCards;
    }

    public void changeType(int playerId, PlayerType type)
    {
        if(players[playerId].getType() == PlayerType.THIEF)
        {
            players[playerId].setType(type);
        }
    }

    public Player[] getPlayers() {
        return players;
    }
}
