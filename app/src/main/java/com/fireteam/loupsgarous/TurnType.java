package com.fireteam.loupsgarous;

public enum TurnType
{
    NIGHT(0),
    DAY(1),
    INIT_GAME_THIEF(2),
    INIT_GAME_CUPIDON(3),
    VOTE_FOR_LEADER(4),
    SEER_TURN(5),
    WITCH_TURN(6),
    WAITING_PLAYERS(7);


    private final int value;
    private TurnType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
