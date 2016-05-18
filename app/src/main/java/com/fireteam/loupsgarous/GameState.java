package com.fireteam.loupsgarous;

import com.fireteam.loupsgarous.player.KillState;
import com.fireteam.loupsgarous.player.Player;
import com.fireteam.loupsgarous.player.PlayerType;

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

    public void init(int nbP)
    {
        nbPlayers = nbP;
        currentPlayers = 0;
        votes = new int[nbPlayers];
        players = new Player[nbPlayers];
        middleCards = new PlayerType[2];
    }

    public int addPlayer()
    {
        Random rand = new Random();
        PlayerType type;
        int playerId = currentPlayers++;
        boolean witch = false, cupidon = false, thief = false, hunter = false, seer = false;
        if(currentPlayers == nbPlayers)
        {
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
                    Player p = new Player(i, type);
                    players[i] = p;
                }
                else
                {
                    middleCards[i - nbPlayers] = type;
                }
            }
        }
        return playerId;
    }

    public byte[] serialize() throws JSONException {
        JSONArray state = new JSONArray();
        state.put(0, nbPlayers);
        state.put(1, currentPlayers);
        state.put(2, leader);
        state.put(3, middleCards[0]);
        state.put(4, middleCards[1]);
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            state.put(index + 5, votes[index]);
        }
        int nextIndex = nbPlayers + 5, playerSize = 0;
        for(index = 0; index < nbPlayers; index++)
        {
            playerSize = players[index].serialize(state, nextIndex);
            nextIndex += playerSize;
        }
        return state.toString().getBytes();
    }

    public GameState unserialize(byte[] data) throws JSONException {
        JSONArray state = new JSONArray(new String(data));
        nbPlayers = state.getInt(0);
        currentPlayers = state.getInt(1);
        leader = state.getInt(2);
        middleCards[0] = (PlayerType) state.get(3);
        middleCards[1] = (PlayerType) state.get(4);
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            votes[index] = state.getInt(index + 5);
        }
        int nextIndex = nbPlayers + 5, playerSize = 0;
        for(index = 0; index < nbPlayers; index++)
        {
            playerSize = players[index].unserialize(state, nextIndex);
            nextIndex += playerSize;
        }
        return this;
    }

    public void voteToKillPlayer(int playerId)
    {
        votes[playerId] += 1;
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


    public KillState killPlayer(Player playerToKill)
    {
        KillState killState;
        if(playerToKill == null)
            playerToKill = getPlayerToKill();
        if(playerToKill != null) {
            playerToKill.kill();
            if (playerToKill.getLoverId() != -1) {
                players[playerToKill.getLoverId()].kill();
                if(playerToKill.getType() == PlayerType.HUNTER)
                    killState = KillState.KILLED_HUNTER;
                else
                    killState = KillState.KILLED_HUNTER_AND_LOVER;
            }
            else if(playerToKill.getType() == PlayerType.HUNTER)
                killState = KillState.KILLED_WITH_LOVER;
            else
                killState = KillState.KILLED;

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

    public void setLeader()
    {
        int best = -1, bestScore = -1;
        for(int i = 0; i < nbPlayers; i++)
        {
            if(votes[i] > bestScore)
            {
                best = i;
                bestScore = votes[i];
            }
            votes[i] = 0;
        }
        players[best].setLeader();
        leader = best;
    }

    public PlayerType getPlayerType(int playerId)
    {
        return players[playerId].getType();
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

}
