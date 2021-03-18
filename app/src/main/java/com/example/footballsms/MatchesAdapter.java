package com.example.footballsms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {

    List<Match> matchesList;

    public MatchesAdapter(List<Match> matches){
        this.matchesList = matches;
    }

    @NonNull
    @Override
    public MatchesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.ViewHolder holder, int position) {
        Match match = matchesList.get(position);
        holder.textViewTeam1Name.setText(match.team1.name);
        holder.textViewTeam2Name.setText(match.team2.name);
        holder.textViewResult.setText(match.result);
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTeam1Name, textViewTeam2Name, textViewResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTeam1Name = itemView.findViewById(R.id.textViewTeam1Name);
            textViewTeam2Name = itemView.findViewById(R.id.textViewTeam2Name);
            textViewResult = itemView.findViewById(R.id.textViewResult);

        }
    }
}
