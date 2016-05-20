package com.fireteam.loupsgarous;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fireteam.loupsgarous.player.Player;

import java.util.ArrayList;

/**
 * Created by gregoire on 20/05/2016.
 */
public class SelectLovers extends AppCompatActivity {

    private GameState state;
    public SelectLovers(GameState state)
    {
        this.state = state;
    }
    private int selected = 0;
    private int firstLover;
    private int secondLover;
    ArrayList<String> playerNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupidon);

//fill the listView
        ListView alive = (ListView) findViewById(R.id.listViewPlayersAlive);
        playerNameList = new ArrayList<String>();
        for(Player p : state.getPlayers())
        {
            playerNameList.add(p.getParticipantId());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, playerNameList);
        alive.setAdapter(arrayAdapter);
        alive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                if(selected == 0)
                {
                    firstLover = position;
                    ++selected;
                }
                else if(selected == 1)
                {
                    if(position != firstLover)
                    {
                        secondLover = position;
                        ++selected;
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Ce joueur est déjà selectionné ", Toast.LENGTH_LONG).show();
                }
                if(selected == 2)
                {
                    Intent intent = new Intent();
                    intent.putExtra("firstLover",firstLover);
                    intent.putExtra("secondLover",secondLover);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


    }
}
