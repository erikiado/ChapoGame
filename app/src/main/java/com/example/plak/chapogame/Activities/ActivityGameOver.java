package com.example.plak.chapogame.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.plak.chapogame.MusicManager;
import com.example.plak.chapogame.R;

import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityGameOver extends AppCompatActivity {

    private Context context;
    private boolean continueMusic;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_over);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent ie = getIntent();
        int score = ie.getIntExtra("score", 0);

        TextView t = (TextView) findViewById(R.id.score);
        t.setText(String.valueOf(score*12));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent i = new Intent(context, ActivityMenu.class);
                context.startActivity(i);
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
            MusicManager.start(this, MusicManager.MUSIC_END_GAME);
        }
    }

    public void guardar() throws IOException {
        String FILENAME = "hello_file";
        String string = "hello world!";

        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
        fos.write(string.getBytes());
        fos.close();
    }

}
