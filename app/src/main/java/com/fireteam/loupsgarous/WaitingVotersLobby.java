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

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by gregoire on 19/05/2016.
 */

public class WaitingVotersLobby extends AppCompatActivity {

    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        String name = new String(this.getIntent().getExtras().getByteArray("voter"));
        TextView te = (TextView) findViewById(R.id.voter_name);

        te.setText(name);
        //te.set
    }
}
