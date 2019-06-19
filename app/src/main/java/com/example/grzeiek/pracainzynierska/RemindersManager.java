package com.example.grzeiek.pracainzynierska;

import android.content.Context;

import com.example.grzeiek.pracainzynierska.broadcast_reciver.NotificationEventReceiver;

import java.util.Calendar;

/**
 * Class to manage reminders: create them, modify and cancel
 */

public class RemindersManager {

    public static void addReminder( Context context, int alarmId, String stringMedDays, String time ) {
//           get hour and minute of reminder
        String[] remTime = time.split( ":" );
        int hour = Integer.parseInt( remTime[ 0 ] );
        int minute = Integer.parseInt( remTime[ 1 ] );

        final Calendar day = Calendar.getInstance();

        day.set( Calendar.HOUR_OF_DAY, hour );
        day.set( Calendar.MINUTE, minute );
        day.set( Calendar.SECOND, 0 );

        if ( stringMedDays.contains( "Daily" ) ) {
            NotificationEventReceiver.setupEverydayAlarm( context, alarmId, day.getTimeInMillis() );
            return;
        }

        if ( stringMedDays.contains( "SUNDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
        } else if ( stringMedDays.contains( "MONDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        } else if ( stringMedDays.contains( "TUESDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
        } else if ( stringMedDays.contains( "WEDNESDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
        } else if ( stringMedDays.contains( "THURSDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
        } else if ( stringMedDays.contains( "FRIDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
        } else if ( stringMedDays.contains( "SATURDAY" ) ) {
            day.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
        }

        NotificationEventReceiver.setupWeeklyAlarm( context, alarmId, day.getTimeInMillis() );
    }

    public static void cancelReminders( Context context, String alarmIds ) {
        String[] alarmId = alarmIds.trim().split( " " );

        for ( String id : alarmId ) {
            NotificationEventReceiver.cancelAlarm( context, Integer.parseInt( id ) );
        }
    }
}
