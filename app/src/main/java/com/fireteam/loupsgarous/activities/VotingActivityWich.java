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
// Typo ^^ ? Witch
public class VotingActivityWich extends AppCompatActivity {

    public static final String PLAYER_TO_KILL_KEY = "playerToKill";
    int playerIdToKill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witch);

        TextView tv = (TextView) findViewById(R.id.playerToKill);
        tv.setText( getIntent().getStringExtra("playerToKill")); // Ok, cela dit si j'ai bien compris c'est playerToSave.
        playerIdToKill =  getIntent().getIntExtra("playerIdToKill", -1);
    }

    // Arf, oui ça marche mais il vaut mieux faire le binding des clicks à la main et ne pas utiliser l'attribut onClick du xml.
    // Cela créer une dépendance entre la vue et son controller.
    // ((Button) findViewById(R.id.yesButton)).setOnClickListener(new View.OnClickListener(){ ...});
    public void OnYesClicked(View v)
    {
        Intent intent = new Intent();
        intent.putExtra("playerToKill", -1);
        // intent.putExtra(PLAYER_TO_KILL_KEY, -1); moins sujet aux erreurs
        setResult(RESULT_OK, intent);
        finish();
    }

    public void OnNoClicked(View v)
    {
        Intent intent = new Intent();
        intent.putExtra("playerToKill", playerIdToKill);
        setResult(RESULT_OK, intent);
        finish();
    }
}
