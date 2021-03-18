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
        try {
            sqLiteHelper.createTeam(1, "MAS");
            sqLiteHelper.createTeam(2, "RCA");
            // sqLiteHelper.createMatch(1, 2, "2-1");
        } catch (Exception ignored){}
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


}