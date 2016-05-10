package com.example.plak.chapogame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.plak.chapogame.Activities.ActivityScore;
import com.example.plak.chapogame.Activities.ActivityScores;

import java.util.ArrayList;

/**
 * Created by erikiado on 5/10/16.
 */
public class AdapterScores extends RecyclerView.Adapter<AdapterScores.ViewHolder>{

private Context context;
private int layoutResourceId;
private RecyclerView recyclerView;
private LinearLayoutManager linearLayoutManager;
private View view;
    private ArrayList dataNames, dataScores;

public AdapterScores(Context context, int layoutResourceId, ArrayList names, ArrayList scores) {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    this.dataNames = names;
    this.dataScores = scores;
          }


@Override
public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResourceId, viewGroup, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(AdapterScores.ViewHolder holder, int position) {
    final String name = (String)dataNames.get(position);
    final int score = (int)dataScores.get(position);

    holder.scoreName.setText(name);
    holder.scoreScore.setText(String.valueOf(score));
    holder.scorePlace.setText(String.valueOf(position+1));

        }

@Override
public int getItemCount() {
        return dataNames.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView place, scoreName, scoreScore, scorePlace;
    LinearLayout background;

    public ViewHolder(View itemView) {
        super(itemView);
        scoreScore = (TextView) itemView.findViewById(R.id.score_score);
        scoreName = (TextView) itemView.findViewById(R.id.score_name);
        scorePlace = (TextView) itemView.findViewById(R.id.score_place);
        //personName = (TextView)itemView.findViewById(R.id.coupons_name);
        //durationTime = (TextView)itemView.findViewById(R.id.coupons_duration);
        //background = (LinearLayout) itemView.findViewById(R.id.background_layout);
    }
}

    public void add(Object item, int position) {
        dataNames.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Object item) {
        int position = dataNames.indexOf(item);
        dataNames.remove(position);
        notifyItemRemoved(position);
    }

}
