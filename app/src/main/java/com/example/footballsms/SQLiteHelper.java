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
    private static final int DATABASE_VERSION = 2;

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
    private static final String COLUMN_MATCH_RESULT = "result";
    private static final String COLUMN_MATCH_CREATEDATE = "create_date";

    private static final String CREATE_TABLE_TEAMS =
            "create table " + TABLE_TEAMS + "(" +
            COLUMN_TEAM_ID + " integer primary key, " +
            COLUMN_TEAM_NAME + " text not null);";

    private static final String CREATE_TABLE_MATCHES =
            "create table " + TABLE_MATCHES + "(" +
            COLUMN_MATCH_ID + " integer primary key autoincrement, " +
            COLUMN_MATCH_TEAM1_ID + " integer references " + TABLE_TEAMS + "(" + COLUMN_TEAM_ID + ")," +
            COLUMN_MATCH_TEAM2_ID + " integer references " + TABLE_TEAMS + "(" + COLUMN_TEAM_ID + ")," +
            COLUMN_MATCH_RESULT + " text not null," +
            COLUMN_MATCH_CREATEDATE + " datetime default (datetime('now','localtime')));";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_TEAMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_MATCHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
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
        Team newTeam = cursorToTeam(cursor);
        cursor.close();
        db.close();
        return newTeam;
    }

    public void createMatch(int team1_id, int team2_id, String result){
        Log.d(TAG, "createMatch: " + team1_id + " " + result + " " + team2_id);

        ContentValues values = new ContentValues();
        values.put(COLUMN_MATCH_TEAM1_ID, team1_id);
        values.put(COLUMN_MATCH_TEAM2_ID, team2_id);
        values.put(COLUMN_MATCH_RESULT, result);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MATCHES, null, values);
        db.close();
    }

    public List<Match> getAllMatches(){
        List<Match> matches = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_MATCH_TEAM1_ID + ", " +
                COLUMN_MATCH_TEAM2_ID + ", " + COLUMN_MATCH_RESULT + ", " + COLUMN_MATCH_CREATEDATE +
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


    private static Team cursorToTeam(Cursor cursor){
        return new Team(cursor.getInt(0), cursor.getString(1));
    }

    private static Match cursorToMatch(Cursor cursor){
        // TODO: indexes and LocalDateTime
        Team team1 = new Team(cursor.getInt(0), cursor.getString(4));
        Team team2 = new Team(cursor.getInt(1), cursor.getString(5));
        // TODO: change this TEMP date
        return new Match(team1, team2, cursor.getString(2));
    }

}
