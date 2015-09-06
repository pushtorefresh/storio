package com.pushtorefresh.storio.sample.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.db.entities.Ant;
import com.pushtorefresh.storio.sample.db.entities.Queen;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QueenAntsAdapter extends RecyclerView.Adapter<QueenAntsAdapter.ViewHolder> {

    public StorIOSQLite storIOSQLite;

    private List<Queen> queens;

    public void setQueens(@Nullable List<Queen> queens) {
        this.queens = queens;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return queens == null ? 0 : queens.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_queen_ants, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Queen queen = queens.get(position);

        holder.queenTextView.setText("Queen: " + queen.getName());

        String allAnts = "Ants: ";
        List<Ant> ants = queen.getAnts(storIOSQLite);
        for (Ant ant : ants) {
            allAnts += ", " + ant.getName();
        }
        allAnts = allAnts.substring(0, allAnts.length()-2);
        holder.antsTextView.setText(allAnts);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.list_item_queen_data)
        TextView queenTextView;

        @InjectView(R.id.list_item_ants)
        TextView antsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
