package com.medreminder.app.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.medreminder.app.Models.Reminder;

import java.sql.SQLException;
import java.util.ArrayList;

public class DBManager {

    private MyDbHelper myDbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager( Context c ) {
        context = c;
    }

    public DBManager open() throws SQLException {
        myDbHelper = new MyDbHelper( context );
        database = myDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDbHelper.close();
    }

    public void delete( long _id ) {
        database.delete( MyDbHelper.TABLE_NAME, MyDbHelper._ID + "=" + _id, null );
    }

    public long insert( String medName, String medTime, String medDose, String medDoseUnit, String days ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put( MyDbHelper.MEDICATION_NAME, medName );
        contentValues.put( MyDbHelper.MEDICATION_TIME, medTime );
        contentValues.put( MyDbHelper.MEDICATION_DOSE, medDose );
        contentValues.put( MyDbHelper.MEDICATION_DOSE_UNIT, medDoseUnit );
        contentValues.put( MyDbHelper.MEDICATION_DAYS, days );

        //if -1 then error occured
        return database.insert( MyDbHelper.TABLE_NAME, null, contentValues );
    }

    public boolean reminderExists( String medName, String medTime, String medDoseUnit, String medDays ) {
        String[] columnNames = new String[]{ MyDbHelper.MEDICATION_NAME,
                MyDbHelper.MEDICATION_TIME, MyDbHelper.MEDICATION_DOSE_UNIT, MyDbHelper.MEDICATION_DAYS };

        String whereClause = MyDbHelper.MEDICATION_NAME + " = ? AND " + MyDbHelper.MEDICATION_TIME + " = ? AND " +
                MyDbHelper.MEDICATION_DOSE_UNIT + " = ? AND " + MyDbHelper.MEDICATION_DAYS + " like '%" + medDays + "%';";
//        MyDbHelper.MEDICATION_DAYS + " = ? ";

        String[] whereArgs = { medName, medTime, medDoseUnit };

        Cursor cursor = database.query( MyDbHelper.TABLE_NAME, columnNames, whereClause, whereArgs,
                null,
                null,
                null,
                "1" );


        if ( cursor.getCount() <= 0 )
            return false;

        return true;
    }

    public int countRecords() {
//        String sql = "SELECT COUNT( * ) FROM " + MyDbHelper.TABLE_NAME + ";";

        Long numRows = DatabaseUtils.queryNumEntries( database, MyDbHelper.TABLE_NAME );
        return numRows.intValue();
    }

    public ArrayList<Reminder> fetchAll() {
        String[] columnNames = new String[]{ MyDbHelper._ID, MyDbHelper.MEDICATION_NAME,
                MyDbHelper.MEDICATION_TIME, MyDbHelper.MEDICATION_DOSE,
                MyDbHelper.MEDICATION_DOSE_UNIT, MyDbHelper.MEDICATION_DAYS, MyDbHelper.MEDICATION_ALARM_ID };

        Cursor cursor = database.query( MyDbHelper.TABLE_NAME, columnNames, null, null, null, null, MyDbHelper.MEDICATION_TIME );

        ArrayList<Reminder> reminderList = new ArrayList<>();

        if ( cursor.moveToFirst() ) {
            do {
                reminderList.add( new Reminder(
                        cursor.getInt( 0 ),
                        cursor.getString( 1 ),
                        cursor.getString( 2 ),
                        cursor.getString( 3 ),
                        cursor.getString( 4 ),
                        cursor.getString( 5 ),
                        cursor.getString( 6 )

                ) );
            } while ( cursor.moveToNext() );
        }

        return reminderList;
    }

    public Cursor fetchById( int id ) {
        String[] selectionArgs = { String.valueOf( id ) };

        String[] columns = new String[]{ MyDbHelper._ID, MyDbHelper.MEDICATION_NAME,
                MyDbHelper.MEDICATION_TIME, MyDbHelper.MEDICATION_DOSE,
                MyDbHelper.MEDICATION_DOSE_UNIT, MyDbHelper.MEDICATION_DAYS };

        Cursor cursor = database.query( MyDbHelper.TABLE_NAME, columns,
                " _id = ? ",
                selectionArgs,
                null,
                null,
                null,
                null );

        if ( cursor != null )
            cursor.moveToFirst();

        return cursor;
    }

    public int update( long id, String medName, String medTime, String medDose, String medDoseUnit, String days ) {
        String[] selectionArgs = { String.valueOf( id ) };

        ContentValues contentValues = new ContentValues();
        contentValues.put( MyDbHelper.MEDICATION_NAME, medName );
        contentValues.put( MyDbHelper.MEDICATION_TIME, medTime );
        contentValues.put( MyDbHelper.MEDICATION_DOSE, medDose );
        contentValues.put( MyDbHelper.MEDICATION_DOSE_UNIT, medDoseUnit );
        contentValues.put( MyDbHelper.MEDICATION_DAYS, days );

        int i = database.update( MyDbHelper.TABLE_NAME, contentValues, " _id = ? ", selectionArgs );
        return i;
    }

    public void deleteAll() {
        database.delete( MyDbHelper.TABLE_NAME, null, null );
    }

    public void insertAlarmId( long id, String alarmIds ) {
        String[] selectionArgs = { String.valueOf( id ) };

        ContentValues contentValues = new ContentValues();
        contentValues.put( MyDbHelper.MEDICATION_ALARM_ID, alarmIds );

        database.update( MyDbHelper.TABLE_NAME, contentValues, " _id = ? ", selectionArgs );
    }

    public String getAlarmId( long id ) {
        String[] selectionArgs = { String.valueOf( id ) };
        String[] columns = new String[]{ MyDbHelper.MEDICATION_ALARM_ID };
        String result = "";

        Cursor cursor = database.query( MyDbHelper.TABLE_NAME, columns,
                " _id = ? ",
                selectionArgs,
                null,
                null,
                null,
                null );

        if ( cursor.moveToFirst() ) {
            result = cursor.getString( 0 );
        }

        return result;
    }

    public Reminder getReminderByAlarmId( int alarmId ) {
        String[] columns = new String[]{ MyDbHelper.MEDICATION_NAME, MyDbHelper.MEDICATION_TIME, MyDbHelper.MEDICATION_DOSE,
                MyDbHelper.MEDICATION_DOSE_UNIT, MyDbHelper.MEDICATION_DAYS };
        String whereClause = MyDbHelper.MEDICATION_ALARM_ID + " LIKE '%" + alarmId + "%';";

        Reminder reminder = new Reminder();

        Cursor cursor = database.query( MyDbHelper.TABLE_NAME, columns,
                whereClause,
                null,
                null,
                null,
                null,
                null );

        if ( cursor.moveToFirst() ) {
            reminder = new Reminder(
                    cursor.getString( 0 ),
                    cursor.getString( 1 ),
                    cursor.getString( 2 ),
                    cursor.getString( 3 ),
                    cursor.getString( 4 )
            );
        }
        return reminder;
    }

    public ArrayList<Reminder> getRemindersByTime( String medName, String medTime ) {
        String[] columns = new String[]{ MyDbHelper.MEDICATION_NAME, MyDbHelper.MEDICATION_TIME, MyDbHelper.MEDICATION_DOSE,
                MyDbHelper.MEDICATION_DOSE_UNIT, MyDbHelper.MEDICATION_DAYS };
        String whereClause = MyDbHelper.MEDICATION_NAME + " != ? AND " + MyDbHelper.MEDICATION_TIME + " = ?;";
        String[] whereArgs = new String[]{ medName, medTime };
        ArrayList<Reminder> reminderList = new ArrayList<>();

        Cursor cursor = database.query( MyDbHelper.TABLE_NAME, columns,
                whereClause,
                whereArgs,
                null,
                null,
                null,
                null );

        if ( cursor.moveToFirst() ) {
            do {
                reminderList.add( new Reminder(
                        cursor.getString( 0 ),
                        cursor.getString( 1 ),
                        cursor.getString( 2 ),
                        cursor.getString( 3 ),
                        cursor.getString( 4 )
                ) );
            } while ( cursor.moveToNext() );
        }
        return reminderList;
    }
}


