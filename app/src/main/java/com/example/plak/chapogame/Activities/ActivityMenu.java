package com.example.plak.chapogame.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.plak.chapogame.Game;
import com.example.plak.chapogame.MusicManager;
import com.example.plak.chapogame.R;

public class ActivityMenu extends AppCompatActivity {

    private Context context;
    private boolean continueMusic;
    private FloatingActionButton fab;
    private Button buttonScores,buttonSettings, buttonInstructions;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);
        buttonScores = (Button) findViewById(R.id.buttonScores);
        buttonInstructions = (Button) findViewById(R.id.buttonInstrucciones);
        initializeListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean musicOption = preferences.getBoolean("switch_music", true);
        if(musicOption) {
            if (!continueMusic) {
                MusicManager.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean musicOption = preferences.getBoolean("switch_music", true);
        if (musicOption) {
            continueMusic = false;
            MusicManager.start(this, MusicManager.MUSIC_MENU);
        }
    }

    public void initializeListeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,Game.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("cur_level",1);
                startActivity(i);
                finish();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ActivitySettings.class);
                startActivity(i);
            }
        });

        buttonScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ActivityScores.class);
                startActivity(i);
            }
        });

        buttonInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ActivityInstructions.class);
                startActivity(i);
            }
        });
    }
}
