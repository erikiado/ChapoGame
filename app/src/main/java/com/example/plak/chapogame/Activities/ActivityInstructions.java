package com.example.plak.chapogame.Activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.plak.chapogame.MusicManager;
import com.example.plak.chapogame.R;

public class ActivityInstructions extends AppCompatActivity {
    private boolean continueMusic;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_instructions);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

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
