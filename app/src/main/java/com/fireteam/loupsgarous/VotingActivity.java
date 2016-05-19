package com.fireteam.loupsgarous;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fireteam.loupsgarous.player.Player;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by gregoire on 19/05/2016.
 */
public class VotingActivity extends AppCompatActivity{

    private GameState state;
    ArrayList<String> playerNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

//fill the listView
        ListView alive = (ListView) findViewById(R.id.listViewPlayersAlive);

        playerNameList = new ArrayList<String>();

        byte [] serializedGameState = this.getIntent().getExtras().getByteArray("GameState");

        try
        {
            state.unserialize(serializedGameState);
            for(Player p : state.getPlayers())
            {
                if(p.isAlive())
                    playerNameList.add(p.getParticipantId());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, playerNameList);

            alive.setAdapter(arrayAdapter);

            alive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // argument position gives the index of item which is clicked
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                //set the vote and change activity to wait other player
                    String selectedAnimal = playerNameList.get(position);
                    state.voteToKillPlayer(position);
                    state.getNextPlayerTurn();
                    Toast.makeText(getApplicationContext(), "Vous avez voté contre : " + selectedAnimal, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VotingActivity.this, WaitingVotersLobby.class);
                    intent.putExtra("voter", selectedAnimal);
                    startActivity(intent);
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }



    }
}
