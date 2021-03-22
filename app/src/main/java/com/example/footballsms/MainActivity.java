package com.example.footballsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    private BroadcastReceiver broadcastReceiver;

    private static final int PERMISSIONS_RECEIVE_SMS = 0;

    RecyclerView recyclerViewMatches;
    MatchesAdapter matchesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        List<Team> teams = sqLiteHelper.getAllTeams();
        for(Team team: teams)
            Log.d("MainActivity", "onCreate: " + team);
        if(sqLiteHelper.getAllTeams().size() != 16){
            sqLiteHelper.deleteAllTeams();
            createTeams();
            // test
            createTestMatches();
        }

        assessReceiveSmsPermissions();
        recyclerViewMatches = findViewById(R.id.recyclerViewMatches);
        updateRecycler();
    }


    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION ))
                {
                    updateRecycler();
                }
            }
        };
        IntentFilter filter = new IntentFilter( NOTIFY_ACTIVITY_ACTION );
        registerReceiver(broadcastReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    private void assessReceiveSmsPermissions(){
        if(isReceiveSmsPermissionRequired()){
            requestSmsPermission();
        }
    }


    private boolean isReceiveSmsPermissionRequired(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                PackageManager.PERMISSION_GRANTED;
    }


    private void requestSmsPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
            showIntentForSmsPermission();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, PERMISSIONS_RECEIVE_SMS);
        }
    }


    private void showIntentForSmsPermission(){
        // TODO
        requestSmsPermission();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_RECEIVE_SMS:
            {
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    showIntentForSmsPermission();
                }
            }
        }
    }


    public void updateRecycler(){
        Log.d("MainActivity", "updateRecycler: getting new matchesList");
        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        List<Match> matches = sqLiteHelper.getAllMatches();

        Log.d("MainActivity", "updateRecycler: creating new adapter");
        matchesAdapter = new MatchesAdapter(matches);
        recyclerViewMatches.setAdapter(matchesAdapter);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Log.d("MainActivity", "updateRecycler: updated recycler");
    }

    // TODO: change by shipped db
    private void createTeams(){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        sqLiteHelper.createTeam(1, "Raja");
        sqLiteHelper.createTeam(2, "Wydad AC");
        sqLiteHelper.createTeam(3, "Olympic Safi");
        sqLiteHelper.createTeam(4, "FAR Rabat");
        sqLiteHelper.createTeam(5, "IR Tanger");
        sqLiteHelper.createTeam(6, "Berkane");
        sqLiteHelper.createTeam(7, "Maghreb Fès");
        sqLiteHelper.createTeam(8, "CAYB");
        sqLiteHelper.createTeam(9, "HUSA");
        sqLiteHelper.createTeam(10, "Chabab Moham.");
        sqLiteHelper.createTeam(11, "MC Oujda");
        sqLiteHelper.createTeam(12, "FUS Rabat");
        sqLiteHelper.createTeam(13, "Tétouan");
        sqLiteHelper.createTeam(14, "El Jadida");
        sqLiteHelper.createTeam(15, "Rapide OZ");
        sqLiteHelper.createTeam(16, "RCA Zemamra");
    }

    private void createTestMatches(){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        sqLiteHelper.createMatch(1, 2, 2, 1);
        sqLiteHelper.createMatch(1, 2, 2, 1);
        sqLiteHelper.createMatch(5, 3, 4, 2);
        sqLiteHelper.createMatch(10, 15, 3, 7);
        sqLiteHelper.createMatch(5, 7, 0, 0);
        sqLiteHelper.createMatch(1, 2, 0, 5);
    }


}