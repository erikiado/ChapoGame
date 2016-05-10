package com.example.plak.chapogame.Activities;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.plak.chapogame.Game;
import com.example.plak.chapogame.MusicManager;
import com.example.plak.chapogame.R;

public class ActivityScore extends AppCompatActivity {
    private boolean continueMusic;
    private SharedPreferences preferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent entrada = getIntent();
        context = this;
        Window ventana = getWindow();

        ventana.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int level = entrada.getIntExtra("cur_level", 0);
        int score = entrada.getIntExtra("score",0);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ((TextView)findViewById(R.id.score)).setText(String.valueOf(score));
        ((TextView)findViewById(R.id.title_score)).setText("Level "+level);

        switch (level){
            case 1:
                level = 2;
                break;
            case 2:
                level = 3;
                break;
            case 3:
                level = 1;
                break;
            default:
                level = 1;
                break;
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final int finalLevel = level;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Game.class);
                intent.putExtra("cur_level", finalLevel);
                startActivity(intent);
                finish();
            }
        });
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

}
