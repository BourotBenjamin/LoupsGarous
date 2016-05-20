package com.fireteam.loupsgarous;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fireteam.loupsgarous.player.Player;

/**
 * Created by gregoire on 20/05/2016.
 */
public class VotingActivityWich extends AppCompatActivity {

    private GameState state;
    private Player playertoKill;
    public VotingActivityWich(GameState state, Player killedPlayer)
    {
        this.state = state;
        playertoKill = killedPlayer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witch);

        TextView tv = (TextView) findViewById(R.id.playerToKill);
        tv.setText(playertoKill.getParticipantId());



    }

    public void OnYesClicked(View v)
    {
        Intent intent = new Intent();
        intent.putExtra("playerToKill", playertoKill.getPlayerId());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void OnNoClicked(View v)
    {
        Intent intent = new Intent();
        intent.putExtra("playerToKill", -1);
        setResult(RESULT_OK, intent);
        finish();
    }
}
