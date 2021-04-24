package com.example.footballsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "football.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TEAMS = "teams";
    private static final String COLUMN_TEAM_ID = "_id";
    private static final String COLUMN_TEAM_NAME = "name";

    private static final String[] allTeamColumns = {
            COLUMN_TEAM_ID,
            COLUMN_TEAM_NAME
    };

    private static final String TABLE_MATCHES = "matches";
    private static final String COLUMN_MATCH_ID = "_id";
    private static final String COLUMN_MATCH_TEAM1_ID = "team1_id";
    private static final String COLUMN_MATCH_TEAM2_ID = "team2_id";
    private static final String COLUMN_MATCH_TEAM1_GOALS = "team1_goals";
    private static final String COLUMN_MATCH_TEAM2_GOALS = "team2_goals";
    private static final String COLUMN_MATCH_CREATEDATE = "create_date";

    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_PHONE = "phone";

    private static final String[] allUserColumns = {
            COLUMN_USER_USERNAME,
            COLUMN_USER_PASSWORD,
            COLUMN_USER_PHONE
    };

    private static final String CREATE_TABLE_TEAMS =
            "create table " + TABLE_TEAMS + "(" +
            COLUMN_TEAM_ID + " integer primary key, " +
            COLUMN_TEAM_NAME + " text not null);";

    private static final String CREATE_TABLE_MATCHES =
            "create table " + TABLE_MATCHES + "(" +
            COLUMN_MATCH_ID + " integer primary key autoincrement, " +
            COLUMN_MATCH_TEAM1_ID + " integer references " + TABLE_TEAMS + "(" + COLUMN_TEAM_ID + ")," +
            COLUMN_MATCH_TEAM2_ID + " integer references " + TABLE_TEAMS + "(" + COLUMN_TEAM_ID + ")," +
            COLUMN_MATCH_TEAM1_GOALS + " integer default 0," +
            COLUMN_MATCH_TEAM2_GOALS + " integer default 0," +
            COLUMN_MATCH_CREATEDATE + " datetime default (datetime('now','localtime')));";

    private static final String CREATE_TABLE_USER =
            "create table " + TABLE_USER + "(" +
                    COLUMN_USER_USERNAME + " text not null, " +
                    COLUMN_USER_PASSWORD + " text not null, " +
                    COLUMN_USER_PHONE + " text not null);";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_TEAMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_MATCHES);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Team createTeam(int id, String name){
        Log.d(TAG, "createTeam: " + id + " " + name);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TEAM_ID, id);
        values.put(COLUMN_TEAM_NAME, name);

        SQLiteDatabase db = getWritableDatabase();
        long insertId = db.insert(TABLE_TEAMS, null, values);
        if(insertId == -1)
            return null;
        Cursor cursor = db.query(TABLE_TEAMS,
                allTeamColumns, COLUMN_TEAM_ID + " = " + insertId, null,
                null, null, null);
        Log.d(TAG, "createTeam: " + insertId);
        cursor.moveToFirst();
        Team newTeam = new Team(cursor.getInt(0), cursor.getString(1));
        cursor.close();
        db.close();
        return newTeam;
    }

    public void createMatch(int team1_id, int team2_id, int team1Goals, int team2Goals){
        Log.d(TAG, "createMatch: " + team1_id + " " + team2_id);

        ContentValues values = new ContentValues();
        values.put(COLUMN_MATCH_TEAM1_ID, team1_id);
        values.put(COLUMN_MATCH_TEAM2_ID, team2_id);
        values.put(COLUMN_MATCH_TEAM1_GOALS, team1Goals);
        values.put(COLUMN_MATCH_TEAM2_GOALS, team2Goals);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MATCHES, null, values);
        db.close();
    }

    public List<Team> getAllTeams(){
        List<Team> teams = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT teams._id, teams.name FROM teams;", null);

        Cursor teamCursor;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);

            // goalaverage
            teamCursor = db.rawQuery(
                    "SELECT SUM(team1_goals) - SUM(team2_goals) " +
                            "FROM matches " +
                            "WHERE team1_id=?;",
                    new String[] {String.valueOf(id)});
            teamCursor.moveToFirst();
            int goalAverage = teamCursor.getInt(0);
            teamCursor.close();
            teamCursor = db.rawQuery(
                    "SELECT SUM(team2_goals) - SUM(team1_goals) " +
                            "FROM matches " +
                            "WHERE team2_id=?;",
                    new String[] {String.valueOf(id)});
            teamCursor.moveToFirst();
            goalAverage += teamCursor.getInt(0);
            teamCursor.close();

            // matches won
            teamCursor = db.rawQuery(
                    "SELECT COUNT(*) " +
                    "FROM matches " +
                    "WHERE (team1_id=? AND team1_goals > team2_goals) " +
                    "OR (team2_id=? AND team1_goals < team2_goals);",
                    new String[] {String.valueOf(id), String.valueOf(id)});
            teamCursor.moveToFirst();
            int matchesWon = teamCursor.getInt(0);
            teamCursor.close();

            // matches lost
            teamCursor = db.rawQuery(
                    "SELECT COUNT(*) " +
                            "FROM matches " +
                            "WHERE (team1_id=? AND team1_goals < team2_goals) " +
                            "OR (team2_id=? and team1_goals > team2_goals);",
                    new String[] {String.valueOf(id), String.valueOf(id)});
            teamCursor.moveToFirst();
            int matchesLost = teamCursor.getInt(0);
            teamCursor.close();

            // matches drawn
            teamCursor = db.rawQuery(
                    "SELECT COUNT(*) " +
                            "FROM matches " +
                            "WHERE (team1_id=? OR team2_id=?) " +
                            "AND team1_goals=team2_goals;",
                    new String[] {String.valueOf(id), String.valueOf(id)});
            teamCursor.moveToFirst();
            int matchesDrawn = teamCursor.getInt(0);
            teamCursor.close();

            Team team = new Team(id, name, matchesWon, matchesLost, matchesDrawn, goalAverage);
            teams.add(team);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return teams;
    }

    public void deleteAllTeams(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL(CREATE_TABLE_TEAMS);
        db.execSQL(CREATE_TABLE_MATCHES);
    }

    public List<Match> getAllMatches(){
        List<Match> matches = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_MATCH_TEAM1_ID + ", " +
                COLUMN_MATCH_TEAM2_ID + ", " + COLUMN_MATCH_TEAM1_GOALS + ", " + COLUMN_MATCH_TEAM2_GOALS + ", " + COLUMN_MATCH_CREATEDATE +
                ", t1." + COLUMN_TEAM_NAME + ", t2." + COLUMN_TEAM_NAME + ", " + TABLE_MATCHES + "." +
                COLUMN_MATCH_ID + " FROM " + TABLE_MATCHES + " LEFT JOIN " + TABLE_TEAMS + " AS t1 ON " +
                COLUMN_MATCH_TEAM1_ID + "=t1._id LEFT JOIN " + TABLE_TEAMS + " AS t2 ON " + COLUMN_MATCH_TEAM2_ID +
                "=t2._id ORDER BY " + TABLE_MATCHES + "." +  COLUMN_MATCH_ID + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Match match = cursorToMatch(cursor);
            matches.add(match);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return matches;
    }

    private static Match cursorToMatch(Cursor cursor){
        // TODO: indexes and LocalDateTime
        Team team1 = new Team(cursor.getInt(0), cursor.getString(5));
        Team team2 = new Team(cursor.getInt(1), cursor.getString(6));
        // TODO: change this TEMP date
        return new Match(team1, team2, cursor.getInt(2), cursor.getInt(3));
    }

    public boolean loginUser(String username, String password){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER, allUserColumns,
                COLUMN_USER_USERNAME+"=? AND "+COLUMN_USER_PASSWORD+"=?",
                new String[]{username, password}, null,null, null);

        boolean isCorrectLogin = cursor.moveToFirst();

        cursor.close();
        db.close();
        return isCorrectLogin;
    }

    public boolean isUserAvailable(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER, allUserColumns,null,
                null, null,null, null);

        boolean isAvailable = cursor.moveToFirst();

        cursor.close();
        db.close();
        return isAvailable;
    }

    public String getPhone(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER, allUserColumns,null,
                null, null,null, null);

        cursor.moveToFirst();

        String phoneNumber = cursor.getString(2);

        cursor.close();
        db.close();
        return phoneNumber;
    }

    public void createUser(String username, String password){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_PHONE, "");

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void updatePhone(String newPhone){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PHONE, newPhone);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_USER, values, null, null);
        db.close();
    }

}
