package com.example.plak.chapogame.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.plak.chapogame.AdapterScores;
import com.example.plak.chapogame.R;

import java.util.ArrayList;

public class ActivityScores extends AppCompatActivity {

    private ArrayList data;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private AdapterScores adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        context = this;
        recyclerView = (RecyclerView)findViewById(R.id.scroll);
        data = new ArrayList<>();
        data.add("s");
        data.add("4");
        data.add("5");
        inicializarLista(data);
    }

    public void inicializarLista(ArrayList dataForAdapter){
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        adapter = new AdapterScores(context, R.layout.item_score, dataForAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
