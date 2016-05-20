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
    public VotingActivity(GameState state)
    {
        this.state = state;
    }

    ArrayList<String> playerNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
//fill the listView
        ListView alive = (ListView) findViewById(R.id.listViewPlayersAlive);
        playerNameList = new ArrayList<String>();
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
                Intent intent = new Intent();
                intent.putExtra("position",position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
