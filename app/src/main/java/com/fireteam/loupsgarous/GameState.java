package com.fireteam.loupsgarous;

import com.fireteam.loupsgarous.player.Player;
import com.fireteam.loupsgarous.player.PlayerType;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Philoupe on 18/05/2016.
 */
public class GameState {


    private Player[] players;
    private int leader;
    private int[] votes;
    private int nbPlayers;
    private int currentPlayers;

    public void init(int nbP)
    {
        nbPlayers = nbP;
        currentPlayers = 0;
        votes = new int[nbPlayers];
        players = new Player[nbPlayers];
    }

    public int addPlayer()
    {
        PlayerType type = PlayerType.VILLAGER;
        Player p = new Player(currentPlayers, type);
        players[currentPlayers] = p;
        return currentPlayers++;
    }

    public byte[] serialize() throws JSONException {
        JSONArray state = new JSONArray();
        state.put(0, nbPlayers);
        state.put(1, currentPlayers);
        state.put(2, leader);
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            state.put(index + 3, votes[index]);
        }
        int nextIndex = nbPlayers + 4, playerSize = 0;
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
        int index;
        for(index = 0; index < nbPlayers; index++)
        {
            votes[index] = state.getInt(index + 3);
        }
        int nextIndex = nbPlayers + 4, playerSize = 0;
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

    public boolean killPlayer()
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
        if(ok) {
            players[best].kill();
            if(players[best].getLoverId() != -1)
            {
                players[players[best].getLoverId()].kill();
            }
        }
        return ok;
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
}
