package com.fireteam.loupsgarous;

import android.util.Log;
import android.widget.Toast;

import com.fireteam.loupsgarous.player.KillState;
import com.fireteam.loupsgarous.player.Player;
import com.fireteam.loupsgarous.player.PlayerType;
import com.google.android.gms.games.stats.PlayerStats;

import org.json.JSONArray;
import org.json.JSONException;

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

    public GameState(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
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

    public String getNextPlayerTurn()
    {
        launchKillPlayer = false;
        if(currentPlayers == nbPlayers)
        {
            switch (turnType)
            {
                case INIT_GAME_THIEF:
                    lastPlayedIdPlayer = -1;
                    turnType = TurnType.INIT_GAME_CUPIDON;
                    for (int i = 0; i < nbPlayers; i++)
                        if(players[i].getType() == PlayerType.THIEF)
                            return players[i].getParticipantId();
                    break;
                case INIT_GAME_CUPIDON:
                    lastPlayedIdPlayer = -1;
                    turnType = TurnType.VOTE_FOR_LEADER;
                    for (int i = 0; i < nbPlayers; i++)
                        if(players[i].getType() == PlayerType.CUPIDON)
                            return players[i].getParticipantId();
                    break;
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
                    lastPlayedIdPlayer = -1;
                    turnType = TurnType.NIGHT;
                    for (int i = 0; i < nbPlayers; i++)
                        if(players[i].isAlive() && players[i].getType() == PlayerType.SEER)
                            return players[i].getParticipantId();
                    break;
                case NIGHT:
                    while(lastPlayedIdPlayer < nbPlayers) {
                        ++lastPlayedIdPlayer;
                        if (players[lastPlayedIdPlayer].isAlive() && players[lastPlayedIdPlayer].getType() == PlayerType.WEREWOLF)
                            return players[lastPlayedIdPlayer].getParticipantId();
                    }
                    turnType = TurnType.WITCH_TURN;
                    break;
                case WITCH_TURN:
                    lastPlayedIdPlayer = -1;
                    turnType = TurnType.DAY;
                    for (int i = 0; i < nbPlayers; i++)
                        if(players[i].isAlive() && players[i].getType() == PlayerType.WITCH)
                            return players[i].getParticipantId();
                    launchKillPlayer = true;
                    break;
                case DAY:
                    while(lastPlayedIdPlayer < nbPlayers) {
                        ++lastPlayedIdPlayer;
                        if (players[lastPlayedIdPlayer].isAlive())
                            return players[lastPlayedIdPlayer].getParticipantId();
                    }
                    launchKillPlayer = true;
                    turnType = TurnType.SEER_TURN;
                    break;

            }
        }
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
        Random rand = new Random();
        PlayerType type;
        int playerId = currentPlayers;
        Player p = new Player(playerId, participantId, displayName);
        players[playerId] = p;
        if(currentPlayers == nbPlayers)
        {
            boolean witch = false, cupidon = false, thief = false, hunter = false, seer = false;
            for(int i = 0; i < nbPlayers + 2; i++)
            {
                if(rand.nextFloat() % 2 > 1) {
                    type = PlayerType.WEREWOLF;
                }
                else if(!witch && rand.nextFloat() % 2 > 1) {
                    type = PlayerType.WITCH;
                    witch = true;
                }
                else if(!cupidon && rand.nextFloat() % 2 > 1) {
                    type = PlayerType.CUPIDON;
                    cupidon = true;
                }
                else if(!thief && rand.nextFloat() % 2 > 1) {
                    type = PlayerType.THIEF;
                    thief = true;
                }
                else if(!hunter && rand.nextFloat() % 2 > 1) {
                    type = PlayerType.HUNTER;
                    hunter = true;
                }
                else if(!seer && rand.nextFloat() % 2 > 1) {
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
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            state.put(index + 9, votes[index]);
        }
        int nextIndex = nbPlayers + 9, playerSize = 0;
        for(index = 0; index < currentPlayers; index++)
        {
            playerSize = players[index].serialize(state, nextIndex);
            nextIndex += playerSize;
        }
        return state.toString().getBytes();
    }

    public GameState unserialize(byte[] data) throws JSONException {
        Log.i("Serialize0", "Unserialize");
        JSONArray state = new JSONArray(new String(data));
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
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            votes[index] = state.getInt(index + 9);
        }
        Log.i("Serialize0", "Unserialize players");
        int nextIndex = nbPlayers + 9, playerSize = 0;
        for(index = 0; index < currentPlayers; index++)
        {
            if(players[index] == null) {
                players[index] = new Player();
                Log.i("Serialize0", "Create player "+index);
            }
            playerSize = players[index].unserialize(state, nextIndex);
            nextIndex += playerSize;
        }
        if(launchKillPlayer)
            killPlayer(-1);
        if(launchSetLeader)
            setLeader(-1);
        return this;
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
            if(ok && votes[i] > bestScore)
            {
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
            Toast.makeText(mainActivity.getApplicationContext(), playerToKill.getDisplayName()+ mainActivity.getResources().getString(R.string.died) + playerToKill.getTypeName(), Toast.LENGTH_LONG).show();
            if (playerToKill.getLoverId() != -1) {
                Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getResources().getString(R.string.lover) + players[playerToKill.getLoverId()].getDisplayName()+ mainActivity.getResources().getString(R.string.was) + playerToKill.getTypeName(), Toast.LENGTH_LONG).show();
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
        Toast.makeText(mainActivity.getApplicationContext(), players[playerIdToSetLeader].getDisplayName()+  mainActivity.getResources().getString(R.string.new_leader), Toast.LENGTH_LONG).show();
        leader = playerIdToSetLeader;
    }

    public String getPlayerType(int playerId)
    {
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
