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
    private ArrayList data;

public AdapterScores(Context context, int layoutResourceId, ArrayList data) {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
          }


@Override
public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResourceId, viewGroup, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(AdapterScores.ViewHolder holder, int position) {
final String objeto = (String)data.get(position);
    holder.scoreText.setText(objeto);
        //holder.nombreSubCategoria.setText(objeto.getString("Nombre"));
        //holder.personName.setText(objeto.getString("name"));

        //holder.durationTime.setText(getFechas(objeto));

        //holder.background.

        }

@Override
public int getItemCount() {
        return data.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView place, scoreText;
    LinearLayout background;

    public ViewHolder(View itemView) {
        super(itemView);
        scoreText = (TextView) itemView.findViewById(R.id.score_score);
        //personName = (TextView)itemView.findViewById(R.id.coupons_name);
        //durationTime = (TextView)itemView.findViewById(R.id.coupons_duration);
        //background = (LinearLayout) itemView.findViewById(R.id.background_layout);
    }
}

    public void add(Object item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Object item) {
        int position = data.indexOf(item);
        data.remove(position);
        notifyItemRemoved(position);
    }

}
