package com.example.plak.chapogame.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.plak.chapogame.AdapterScores;
import com.example.plak.chapogame.MusicManager;
import com.example.plak.chapogame.R;

import java.util.ArrayList;

public class ActivityScores extends AppCompatActivity {

    private ArrayList data;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private AdapterScores adapter;
    private SharedPreferences preferences;
    private boolean continueMusic;

    private ArrayList names, scores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        context = this;
        recyclerView = (RecyclerView)findViewById(R.id.scroll);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        names = new ArrayList();
        scores = new ArrayList();

        scores.add(preferences.getInt("score_1", 0));
        scores.add(preferences.getInt("score_2", 0));
        scores.add(preferences.getInt("score_3", 0));
        scores.add(preferences.getInt("score_4", 0));
        scores.add(preferences.getInt("score_5", 0));

        names.add(preferences.getString("name_1", "--- ---"));
        names.add(preferences.getString("name_2", "--- ---"));
        names.add(preferences.getString("name_3", "--- ---"));
        names.add(preferences.getString("name_4", "--- ---"));
        names.add(preferences.getString("name_5", "--- ---"));


        inicializarLista(names, scores);
    }

    public void inicializarLista(ArrayList names,ArrayList scores){
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        adapter = new AdapterScores(context, R.layout.item_score, names,scores);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
