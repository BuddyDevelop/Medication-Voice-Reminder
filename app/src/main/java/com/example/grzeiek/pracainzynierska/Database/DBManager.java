package com.example.grzeiek.pracainzynierska.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.grzeiek.pracainzynierska.Reminder;

import java.sql.SQLException;
import java.util.ArrayList;

public class DBManager {

    private MyDbHelper myDbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager( Context c ){
        context = c;
    }

    public DBManager open() throws SQLException{
        myDbHelper = new MyDbHelper( context );
        database = myDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        myDbHelper.close();
    }

    public void delete( long _id ){
        database.delete( myDbHelper.TABLE_NAME, myDbHelper._ID + "=" + _id, null );
    }

    public long insert( String medName, String medTime, String medDose, String medDoseUnit, String days ){
        ContentValues contentValues = new ContentValues(  );
        contentValues.put( myDbHelper.MEDICATION_NAME, medName );
        contentValues.put( myDbHelper.MEDICATION_TIME, medTime );
        contentValues.put( myDbHelper.MEDICATION_DOSE, medDose );
        contentValues.put( myDbHelper.MEDICATION_DOSE_UNIT, medDoseUnit );
        contentValues.put( myDbHelper.MEDICATION_DAYS, days );

        //if -1 then error occured
        long insertedRowid;

        insertedRowid = database.insert( myDbHelper.TABLE_NAME, null, contentValues );
        return insertedRowid;
    }

    public boolean reminderExists( String medName, String medTime, String medDoseUnit, String medDays ){
        String[] columnNames = new String[] { myDbHelper.MEDICATION_NAME,
                myDbHelper.MEDICATION_TIME, myDbHelper.MEDICATION_DOSE_UNIT, myDbHelper.MEDICATION_DAYS  };

        String whereClause = myDbHelper.MEDICATION_NAME + " = ? AND " + myDbHelper.MEDICATION_TIME + " = ? AND " +
                  myDbHelper.MEDICATION_DOSE_UNIT + " = ? AND " + myDbHelper.MEDICATION_DAYS + " like '%" + medDays + "%';";
//        myDbHelper.MEDICATION_DAYS + " = ? ";

        String[] whereArgs = { medName, medTime, medDoseUnit };

        Cursor cursor = database.query( myDbHelper.TABLE_NAME, columnNames, whereClause, whereArgs,
                null,
                null,
                null,
                "1" );


        if( cursor.getCount() <= 0 )
            return false;

        return true;
    }

    public int countRecords(){
//        String sql = "SELECT COUNT( * ) FROM " + myDbHelper.TABLE_NAME + ";";

        Long numRows = DatabaseUtils.queryNumEntries( database, myDbHelper.TABLE_NAME );
        return numRows.intValue();
    }

    public ArrayList<Reminder> fetchAll() {
        String[] columnNames = new String[] { myDbHelper._ID, myDbHelper.MEDICATION_NAME,
                myDbHelper.MEDICATION_TIME, myDbHelper.MEDICATION_DOSE,
                myDbHelper.MEDICATION_DOSE_UNIT, myDbHelper.MEDICATION_DAYS  };

        Cursor cursor = database.query(myDbHelper.TABLE_NAME, columnNames, null, null, null, null, null);

        ArrayList<Reminder> reminderList = new ArrayList<Reminder>();

        if( cursor.moveToFirst() ){
            do {
                reminderList.add( new Reminder(
                    cursor.getInt( 0 ),
                    cursor.getString( 1 ),
                    cursor.getString( 2 ),
                    cursor.getString( 3 ),
                    cursor.getString( 4 ),
                    cursor.getString( 5 )

                ) );
            } while ( cursor.moveToNext() );
        }

        return reminderList;
    }

    public Cursor fetchById( int id ){
        String[] selectionArgs = { String.valueOf( id ) };

        String[] columns = new String[] { myDbHelper._ID, myDbHelper.MEDICATION_NAME,
                myDbHelper.MEDICATION_TIME, myDbHelper.MEDICATION_DOSE,
                myDbHelper.MEDICATION_DOSE_UNIT, myDbHelper.MEDICATION_DAYS  };

        Cursor cursor = database.query( myDbHelper.TABLE_NAME, columns,
                " _id = ? ",
                selectionArgs,
                null,
                null,
                null,
                null );

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public int update( long id, String medName, String medTime, String medDose, String medDoseUnit, String days ){
        String[] selectionArgs = { String.valueOf( id ) };

        ContentValues contentValues = new ContentValues(  );
        contentValues.put( myDbHelper.MEDICATION_NAME, medName );
        contentValues.put( myDbHelper.MEDICATION_TIME, medTime );
        contentValues.put( myDbHelper.MEDICATION_DOSE, medDose );
        contentValues.put( myDbHelper.MEDICATION_DOSE_UNIT, medDoseUnit );
        contentValues.put( myDbHelper.MEDICATION_DAYS, days );

        int i = database.update( myDbHelper.TABLE_NAME, contentValues, " _id = ? ", selectionArgs );
        return i;
    }
}


