package com.fireteam.loupsgarous.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fireteam.loupsgarous.GameState;
import com.fireteam.loupsgarous.R;
import com.fireteam.loupsgarous.player.Player;

import java.util.ArrayList;

/**
 * Created by gregoire on 20/05/2016.
 */
public class SelectLovers extends AppCompatActivity {

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
        playerNameList = getIntent().getStringArrayListExtra("playersNames"); // Pour être sur en cas de changement il vaut mieux utiliser des constantes.
        // playerNameList = getIntent().getStringArrayListExtra(PLAYER_NAMES_KEY);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, playerNameList);
        alive.setAdapter(arrayAdapter); // Attention alive peut être null. La méthode findViewById peut renvoyer null s'il ne trouve pas le composant dans le layout
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
                        Toast.makeText(getApplicationContext(), "Ce joueur est déjà selectionné ", Toast.LENGTH_LONG).show(); // Attention à l'extraction de toutes les strings dans le fichier xml.
                }
                if(selected == 2)
                {
                    Intent intent = new Intent();
                    intent.putExtra("firstLover",firstLover); // Pareil que pour playerNames
                    intent.putExtra("secondLover",secondLover);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        // Oui bonne implémentation du setActivityForResult et du listener sur les dans une liste.

    }
}
