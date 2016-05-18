package com.fireteam.loupsgarous;

/**
 * Created by Philoupe on 18/05/2016.
 */
public class GameState {

    byte[] b;

    public byte[] getData()
    {
        b = new byte[1];
        b[0] = 1;
        return b;
    }

    public void update()
    {
        b = new byte[2];
        b[0] = 0;
        b[1] = 1;
    }

}
