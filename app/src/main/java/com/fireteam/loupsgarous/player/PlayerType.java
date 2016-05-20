package com.fireteam.loupsgarous.player;

/**
 * Created by Philoupe on 18/05/2016.
 */
public enum PlayerType {
    WEREWOLF(0),
    CUPIDON(1),
    SEER(2),
    HUNTER(3),
    WITCH(4),
    VILLAGER(5),
    THIEF(6);


    private final int value;
    private PlayerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
