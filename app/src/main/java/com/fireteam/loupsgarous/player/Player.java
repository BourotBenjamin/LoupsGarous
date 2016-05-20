package com.fireteam.loupsgarous.player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player {

    int playerId;
    PlayerType type;
    boolean alive;
    int loverId;
    boolean leader;
    String participantId;

    public String toString()
    {
        return new String(" type : " + type + " Alive : " + alive + " Lover : " + loverId + " Leader : " + leader + " Name : " + participantId + "\n");
    }

    public int getPlayerId() {
        return playerId;
    }

    public  Player() {}

    public Player(int playerId, String participantId)
    {
        this.playerId = playerId;
        this.alive = true;
        this.participantId = participantId;
        this.loverId = -1;
    }

    public String getTypeName()
    {
        if(type != null)
            switch (type)
            {
                case CUPIDON:
                    return "Cupidon";
                case HUNTER:
                    return "Chasseur";
                case SEER:
                    return "Voyant";
                case THIEF:
                    return "Voleur";
                case VILLAGER:
                    return "Villageois";
                case WEREWOLF:
                    return "Loup";
                case WITCH:
                    return "Sorcier";
            }
        return "";
    }

    public void setLeader() {
        this.leader = true;
    }

    public void setLoverId(int loverId) {
        this.loverId = loverId;
    }

    public int getLoverId() {
        return loverId;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public boolean kill()
    {
        alive = false;
        return (type == PlayerType.HUNTER);
    }

    public int serialize(JSONArray state, int nextIndex) throws JSONException {
        state.put(nextIndex, playerId);
        state.put(nextIndex + 1, alive);
        state.put(nextIndex + 2, leader);
        if(type == null)
            state.put(nextIndex + 3, -1);
        else
            state.put(nextIndex + 3, type.getValue());
        state.put(nextIndex + 4, loverId);
        state.put(nextIndex + 5, participantId);
        return 6;
    }

    public int unserialize(JSONArray state, int nextIndex) throws JSONException {
        playerId = state.getInt(nextIndex);
        alive = state.getBoolean(nextIndex + 1);
        leader = state.getBoolean(nextIndex + 2);
        if(state.getInt(nextIndex + 3) == -1)
            type = null;
        else
            type = PlayerType.values()[state.getInt(nextIndex + 3)];
        loverId = state.getInt(nextIndex + 4);
        participantId = state.getString(nextIndex + 5);
        return 6;
    }

    public String getParticipantId() {
        return participantId;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isLeader() {
        return leader;
    }
}
