package com.fireteam.loupsgarous.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fireteam.loupsgarous.GameState;
import com.fireteam.loupsgarous.R;
import com.fireteam.loupsgarous.player.Player;

/**
 * Created by gregoire on 20/05/2016.
 */
public class VotingActivityWich extends AppCompatActivity {

    int playerIdToKill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witch);

        TextView tv = (TextView) findViewById(R.id.playerToKill);
        tv.setText( getIntent().getStringExtra("playerToKill"));
        playerIdToKill =  getIntent().getIntExtra("playerIdToKill", -1);

    }

    public void OnYesClicked(View v)
    {
        Intent intent = new Intent();
        intent.putExtra("playerToKill", playerIdToKill);
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
