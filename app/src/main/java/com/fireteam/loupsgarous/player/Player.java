package com.fireteam.loupsgarous.player;

import com.fireteam.loupsgarous.R;

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
    String displayName;

    public String toString()
    {
        return new String(" type : " + type + " Alive : " + alive + " Lover : " + loverId + " Leader : " + leader + " Name : " + displayName + "\n");
    }

    public int getPlayerId() {
        return playerId;
    }

    public  Player() {}

    public String getDisplayName() {
        return displayName;
    }

    public Player(int playerId, String participantId, String displayName)
    {
        this.playerId = playerId;
        this.alive = true;
        this.participantId = participantId;
        this.displayName = displayName;
        this.loverId = -1;
    }

    // Oui mais vous auriez pu mettre cette fonction dans l'enum
    // et faire CUPIDON.toName(); par exemple
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

    // De même que pour au dessus.
    public int getTypeImageId()
    {
        if(type != null)
            switch (type)
            {
                case CUPIDON:
                    return R.mipmap.carte_cupidon; // Attention mipmap c'est uniquement pour les icones. S'il s'agit d'une image, elles doivent être dans les dossiers drawable
                case HUNTER:
                    return R.mipmap.carte_villageois;
                case SEER:
                    return R.mipmap.carte_voyante;
                case THIEF:
                    return R.mipmap.carte_villageois;
                case VILLAGER:
                    return R.mipmap.carte_villageois;
                case WEREWOLF:
                    return R.mipmap.carte_loup;
                case WITCH:
                    return R.mipmap.carte_sorciere;
            }
        return 0;
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
        state.put(nextIndex + 6, displayName);
        return 7;
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
        displayName = state.getString(nextIndex + 6);
        return 7;
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
