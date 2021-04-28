package com.example.footballsms;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String PREF_SESSION_ID = "session_id";
    private static final String PREF_IS_LOGGED_IN = "is_logged_in";
    private static final String PREF_USERNAME = "username";

    public PreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String sessionId, String username){
        editor.putBoolean(PREF_IS_LOGGED_IN, true);
        editor.putString(PREF_SESSION_ID, sessionId);
        editor.putString(PREF_USERNAME, username);
        editor.apply();
    }

    public void login(String username){
        editor.putBoolean(PREF_IS_LOGGED_IN, true);
        editor.putString(PREF_USERNAME, username);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }

    public String getUsername(){
        return sharedPreferences.getString(PREF_USERNAME, "");
    }

}
