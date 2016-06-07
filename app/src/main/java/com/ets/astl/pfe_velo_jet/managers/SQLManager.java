package com.ets.astl.pfe_velo_jet.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "velojet";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "USERS";
    private static final String TABLE_PATHS = "PATHS";

    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_TYPE = "TYPE";
    private static final String COLUMN_USER_ID = "USER_ID";
    private static final String COLUMN_LENGTH = "LENGTH";
    private static final String COLUMN_SPEED = "SPEED";
    private static final String COLUMN_TIME = "TIME";
    private static final String COLUMN_POINTS = "POINTS";

    //Queries
    private static final String QUERY_USERS_CREATE = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " VARCHAR(255)," +
            COLUMN_TYPE + " CHAR);";
    private static final String QUERY_PATHS_CREATE = "CREATE TABLE " + TABLE_PATHS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_ID + " INTEGER," +
            COLUMN_LENGTH + " DECIMAL," + COLUMN_SPEED + " DECIMAL," + COLUMN_TIME + " LONG," +
            COLUMN_POINTS + " TEXT);";
    private static final String QUERY_USERS_DELETE = "DROP TABLE " + TABLE_USERS + ";";
    private static final String QUERY_PATHS_DELETE = "DROP TABLE " + TABLE_PATHS + ";";

    SQLManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_USERS_CREATE);
        db.execSQL(QUERY_PATHS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QUERY_PATHS_DELETE);
        db.execSQL(QUERY_USERS_DELETE);

        this.onCreate(db);
    }
}
