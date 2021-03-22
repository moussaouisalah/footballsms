package com.example.footballsms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class RankingsActivity extends AppCompatActivity {
    private static final String TAG = "RankingsActivity";
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    private BroadcastReceiver broadcastReceiver;

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        tableLayout = findViewById(R.id.tableLayout);

        updateTable();
    }


    private TableRow createTableRow(Team team, int index){
        TableRow tableRow = new TableRow(getApplicationContext());
        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView textViewName = createTextView(team.name, 6);
        TextView textViewMJ = createTextView(String.valueOf(team.matchesPlayed), 2);
        TextView textViewG = createTextView(String.valueOf(team.matchesWon), 2);
        TextView textViewN = createTextView(String.valueOf(team.matchesDrawn), 2);
        TextView textViewD = createTextView(String.valueOf(team.matchesLost), 2);
        TextView textViewAVG = createTextView(String.valueOf(team.goalAverage), 2);
        TextView textViewPoints = createTextView(String.valueOf(team.matchesWon * 3 + team.matchesDrawn), 2);

        tableRow.addView(textViewName);
        tableRow.addView(textViewMJ);
        tableRow.addView(textViewG);
        tableRow.addView(textViewN);
        tableRow.addView(textViewD);
        tableRow.addView(textViewAVG);
        tableRow.addView(textViewPoints);
        tableRow.setVisibility(View.VISIBLE);
        if(index % 2 == 0)
            tableRow.setBackgroundColor(getResources().getColor(R.color.grey));

        Log.d(TAG, "createTableRow: " + tableRow.toString());
        return tableRow;
    }

    private TextView createTextView(String text, float weight){
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setPadding(10, 10, 10, 10);
        textView.setTextSize(14);
        TableRow.LayoutParams tableRownParams = new TableRow.LayoutParams();
        tableRownParams.weight = weight;
        textView.setLayoutParams(tableRownParams);
        return textView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION ))
                {
                    updateTable();
                }
            }
        };
        IntentFilter filter = new IntentFilter( NOTIFY_ACTIVITY_ACTION );
        registerReceiver(broadcastReceiver, filter);
    }

    private void updateTable(){
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        List<Team> teams = sqLiteHelper.getAllTeams();
        teams.sort((team, t1) -> {
            int teamPoints = team.matchesWon * 3 + team.matchesDrawn;
            int t1Points = t1.matchesWon * 3 + t1.matchesDrawn;
            if(teamPoints > t1Points)
                return -1;
            else if(teamPoints == t1Points && team.goalAverage > t1.goalAverage)
                return -1;
            else
                return 1;
        });
        for(int i=0; i<teams.size(); i++){
            Log.d(TAG, "onCreate: " + teams.get(i));
            TableRow tableRow = createTableRow(teams.get(i), i);
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));
            Log.d(TAG, "onCreate: " + tableLayout.getChildCount());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}