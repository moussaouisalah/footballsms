package com.example.footballsms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

public class RankingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);
        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        List<Team> teams = sqLiteHelper.getAllTeams();
        for(int i=0; i<teams.size(); i++){
            
        }
    }
}