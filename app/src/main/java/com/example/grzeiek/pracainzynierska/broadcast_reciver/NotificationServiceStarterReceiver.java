package com.example.grzeiek.pracainzynierska.broadcast_reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.grzeiek.pracainzynierska.Database.DBManager;
import com.example.grzeiek.pracainzynierska.Reminder;
import com.example.grzeiek.pracainzynierska.RemindersManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Broadcast receiver for: BOOT_COMPLETED, TIMEZONE_CHANGED, and TIME_SET events. Sets Alarm Manager for notification;
 */

public class NotificationServiceStarterReceiver extends BroadcastReceiver {

    private DBManager dbManager;
    private ArrayList<Reminder> reminderArrayList;

    @Override
    public void onReceive( Context context, Intent intent ) {
//        NotificationEventReceiver.setupWeeklyAlarm( context );
        if ( intent == null )
            return;

        //create alarms for all medications
        switch ( intent.getAction() ) {
            case Intent.ACTION_TIMEZONE_CHANGED:
            case Intent.ACTION_TIME_CHANGED:
            case Intent.ACTION_BOOT_COMPLETED:
                recreateAllAlarms( context );
                break;
        }
    }

    public void recreateAllAlarms( Context context ) {
        int myAlarmDayOfTheWeek;
        dbManager = new DBManager( context );

        try {
            dbManager.open();
            reminderArrayList = dbManager.fetchAll();

            if ( reminderArrayList == null || reminderArrayList.isEmpty() )
                return;

            //initialize variables
            int medRecordId, alarmId;
            String alarmIdString;
            String reminderDays;
            String time;
            final Calendar day = Calendar.getInstance();

            // go through every record
            for ( Reminder reminder : reminderArrayList ) {
                medRecordId = reminder.getId();
                reminderDays = reminder.getReminderDays();
                time = reminder.getReminderTime();
                alarmIdString = "";

                String[] remTime = time.split( ":" );
                int hour = Integer.parseInt( remTime[ 0 ] );
                int minute = Integer.parseInt( remTime[ 1 ] );
                String[] reminderDayArr = reminderDays.trim().split( " " );

                // for each week day set alarm
                for ( String weekday : reminderDayArr ) {
                    alarmId = ( int ) System.currentTimeMillis();
                    alarmIdString += Integer.toString( alarmId ) + " ";


                    if ( weekday.contains( "Daily" ) ) {
                        day.set( Calendar.HOUR_OF_DAY, hour );
                        day.set( Calendar.MINUTE, minute );
                        day.set( Calendar.SECOND, 0 );
                        NotificationEventReceiver.setupEverydayAlarm( context, alarmId, day.getTimeInMillis() );
                        continue;
                    }

                    myAlarmDayOfTheWeek = getDayAsInt( weekday );

                    //Check whether the day of the week was earlier in the week:
                    if ( myAlarmDayOfTheWeek > day.get( Calendar.DAY_OF_WEEK ) ) {
                        day.add( Calendar.DAY_OF_YEAR, ( myAlarmDayOfTheWeek - day.get( Calendar.DAY_OF_WEEK ) ) );
                    } else {
                        if ( myAlarmDayOfTheWeek < day.get( Calendar.DAY_OF_WEEK ) ) {
                            //Set the day of the AlarmManager:
                            day.add( Calendar.DAY_OF_YEAR, ( 7 - ( day.get( Calendar.DAY_OF_WEEK ) - myAlarmDayOfTheWeek ) ) );
                        } else {  // myAlarmDayOfTheWeek == time.get(Calendar.DAY_OF_WEEK)
                            //Check whether the time has already gone:
                            if ( ( hour < day.get( Calendar.HOUR_OF_DAY ) ) || ( ( hour == day.get( Calendar.HOUR_OF_DAY ) ) && ( minute < day.get( Calendar.MINUTE ) ) ) ) {
                                //Set the day of the AlarmManager:
                                day.add( Calendar.DAY_OF_YEAR, 7 );
                            }
                        }
                    }

                    day.set( Calendar.HOUR_OF_DAY, hour );
                    day.set( Calendar.MINUTE, minute );
                    day.set( Calendar.SECOND, 0 );
                    NotificationEventReceiver.setupWeeklyAlarm( context, alarmId, day.getTimeInMillis() );
//                    RemindersManager.addReminder( context, alarmId, weekday, time );
                }
                dbManager.insertAlarmId( medRecordId, alarmIdString );
            }
            dbManager.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public int getDayAsInt( String weekday ) {
        int myAlarmDayOfTheWeek = -1;

        if ( weekday.contains( "SUNDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.SUNDAY;
        } else if ( weekday.contains( "MONDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.MONDAY;
        } else if ( weekday.contains( "TUESDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.TUESDAY;
        } else if ( weekday.contains( "WEDNESDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.WEDNESDAY;
        } else if ( weekday.contains( "THURSDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.THURSDAY;
        } else if ( weekday.contains( "FRIDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.FRIDAY;
        } else if ( weekday.contains( "SATURDAY" ) ) {
            myAlarmDayOfTheWeek = Calendar.SATURDAY;
        }

        return myAlarmDayOfTheWeek;
    }
}
