package com.medreminder.app;

import android.content.Context;

import com.medreminder.app.broadcast_reciver.NotificationEventReceiver;

import java.util.Calendar;

/**
 * Class to manage reminders: create them, modify and cancel
 */

public class RemindersManager {
    public static int myAlarmDayOfTheWeek;

    public static void addReminder( Context context, int alarmId, String stringMedDay, String time ) {
//           get hour and minute of reminder
        String[] remTime = time.split( ":" );
        int hour = Integer.parseInt( remTime[ 0 ] );
        int minute = Integer.parseInt( remTime[ 1 ] );

        final Calendar day = Calendar.getInstance();
        myAlarmDayOfTheWeek = getDayAsInt( stringMedDay );

//      reminder for specific day of week
        if ( myAlarmDayOfTheWeek != -1 ) {
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
//      daily notification
        } else {
            //if time is past reminder's time schedule for next day
            if ( ( hour < day.get( Calendar.HOUR_OF_DAY ) ) || ( ( hour == day.get( Calendar.HOUR_OF_DAY ) ) && ( minute < day.get( Calendar.MINUTE ) ) ) ) {
                day.add( Calendar.DAY_OF_YEAR, 1 );
            }
        }

        day.set( Calendar.HOUR_OF_DAY, hour );
        day.set( Calendar.MINUTE, minute );
        day.set( Calendar.SECOND, 0 );

        if ( stringMedDay.contains( "Daily" ) ) {
            NotificationEventReceiver.setupEverydayAlarm( context, alarmId, day.getTimeInMillis() );
            return;
        }

        NotificationEventReceiver.setupWeeklyAlarm( context, alarmId, day.getTimeInMillis() );
    }

    public static void cancelReminders( Context context, String alarmIds ) {
        String[] alarmId = alarmIds.trim().split( " " );

        for ( String id : alarmId ) {
            NotificationEventReceiver.cancelAlarm( context, Integer.parseInt( id ) );
        }
    }

    public static int getDayAsInt( String weekday ) {
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
