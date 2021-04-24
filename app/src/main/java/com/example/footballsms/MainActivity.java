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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    private BroadcastReceiver broadcastReceiver;

    private static final int PERMISSIONS_RECEIVE_SMS = 0;
    private static final int PERMISSIONS_READ_PHONE = 1;

    RecyclerView recyclerViewMatches;
    MatchesAdapter matchesAdapter;
    Button buttonRankings, buttonLogin;
    TextView textViewPhoneNumber, textViewIMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewIMEI = findViewById(R.id.textViewIMEI);

        buttonRankings = findViewById(R.id.buttonRankings);
        buttonRankings.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), RankingsActivity.class);
            startActivity(intent);
        });

        buttonLogin = findViewById(R.id.buttonLoginRedirect);
        buttonLogin.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        List<Team> teams = sqLiteHelper.getAllTeams();
        for (Team team : teams)
            Log.d("MainActivity", "onCreate: " + team);
        if (sqLiteHelper.getAllTeams().size() != 16) {
            sqLiteHelper.deleteAllTeams();
            createTeams();
            // test
            createTestMatches();
        }

        assessReceiveSmsPermissions();
        recyclerViewMatches = findViewById(R.id.recyclerViewMatches);
        updateRecycler();

        // check user details
        if (!sqLiteHelper.isUserAvailable()) {
            // create user if not already
            sqLiteHelper.createUser("test", "test");
        }

        assessReadPhonePermissions();

        textViewPhoneNumber.setText(sqLiteHelper.getPhone());
        textViewIMEI.setText(getDeviceId(getApplicationContext()));
    }

    private void assessReadPhonePermissions() {
        if (isReadPhonePermissionRequired()) {
            requestReadPhonePermission();
        } else {
            readPhone();
        }
    }

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    private void readPhone() {
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String mPhoneNumber = tMgr.getLine1Number();
        SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
        String oldPhone = sqLiteHelper.getPhone();
        if(mPhoneNumber.equals(oldPhone))
            return;
        sqLiteHelper.updatePhone(mPhoneNumber);
        textViewPhoneNumber.setText(mPhoneNumber);
    }

    private void requestReadPhonePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            showIntentForReadPhonePermission();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS
            }, PERMISSIONS_READ_PHONE);
        }
    }

    private void showIntentForReadPhonePermission() {
        requestReadPhonePermission();
    }

    private boolean isReadPhonePermissionRequired() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED;
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
                    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE
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
            case PERMISSIONS_READ_PHONE:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    showIntentForSmsPermission();
                }
                else {
                    readPhone();
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