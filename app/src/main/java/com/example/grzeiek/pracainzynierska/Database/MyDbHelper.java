package com.example.grzeiek.pracainzynierska.Database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class MyDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicationreminder.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_NAME = "medreminder";
    public static final String _ID = "_id";
    public static final String MEDICATION_NAME = "medication_name";
    public static final String MEDICATION_TIME = "time";
    public static final String MEDICATION_DOSE = "medication_dose";
    public static final String MEDICATION_DOSE_UNIT = "medication_unit";
    public static final String MEDICATION_DAYS = "medication_days";


    public MyDbHelper( Context context ){
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }


    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {
        String DATABASE_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MEDICATION_NAME + " TEXT NOT NULL, " +
                MEDICATION_TIME + " TEXT NOT NULL, " +
                MEDICATION_DOSE + " TEXT NOT NULL, " +
                MEDICATION_DOSE_UNIT + " TEXT NOT NULL, " +
                MEDICATION_DAYS + " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL( DATABASE_CREATE_TABLE );

    }

    @Override
    public void onUpgrade( SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion ) {
        String SQL_QUERY = "DROP TABLE IF EXISTS " + DATABASE_NAME;
        sqLiteDatabase.execSQL( SQL_QUERY );
        onCreate( sqLiteDatabase );
    }
}
