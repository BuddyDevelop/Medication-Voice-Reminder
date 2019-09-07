package com.medreminder.app.broadcast_reciver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.medreminder.app.Services.NotificationIntentService;

/**
 * WakefulBroadcastReceiver used to receive intents fired from the AlarmManager for showing notifications
 * and from the notification itself if it is deleted.
 */

public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_WORKING_NOTIFICATION_SERVICE = "ACTION_WORKING_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";

    private static final int NOTIFICATIONS_INTERVAL_WEEK = 7;

    private PowerManager.WakeLock screenWakeLock;


    public static void setupWeeklyAlarm( Context context, int alarmId, long when ) {
        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent alarmIntent = getWorkingPendingIntent( context, alarmId );

        alarmManager.setRepeating( AlarmManager.RTC_WAKEUP,
                when,
                AlarmManager.INTERVAL_DAY * NOTIFICATIONS_INTERVAL_WEEK,
                alarmIntent );
    }

    public static void setupEverydayAlarm( Context context, int alarmId, long when ) {
        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent alarmIntent = getWorkingPendingIntent( context, alarmId );
        alarmManager.cancel( alarmIntent );

        alarmManager.setRepeating( AlarmManager.RTC_WAKEUP,
                when,
                AlarmManager.INTERVAL_DAY,
                alarmIntent );
    }

    public static void cancelAlarm( Context context, int alarmId ) {
        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent alarmIntent = getWorkingPendingIntent( context, alarmId );
        alarmManager.cancel( alarmIntent );
    }

    private static PendingIntent getWorkingPendingIntent( Context context, int alarmId ) {
        Intent intent = new Intent( context, NotificationEventReceiver.class );
        intent.putExtra( "alarmId", alarmId );
        intent.setAction( ACTION_WORKING_NOTIFICATION_SERVICE );
        return PendingIntent.getBroadcast( context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }


    @Override
    public void onReceive( Context context, Intent intent ) {
        String action = intent.getAction();
        Intent serviceIntent = null;

        if( screenWakeLock == null ){
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.PARTIAL_WAKE_LOCK,
                    "ScreenLock tag:from AlarmListener");
            screenWakeLock.acquire();
        }

        if ( ACTION_DELETE_NOTIFICATION.equals( action ) ) {
            //implement action if user delete notification from notification's panel
        } else if ( ACTION_WORKING_NOTIFICATION_SERVICE.equals( action ) ) {
            Bundle extras = intent.getExtras();
            int alarmId = extras.getInt( "alarmId" );
            serviceIntent = NotificationIntentService.createWorkingNotificationService( context, alarmId );
        }

        if ( serviceIntent != null ) {
            // Start the service, keeping the device awake while it is working
            startWakefulService( context, serviceIntent );
        }

        if( screenWakeLock != null )
            screenWakeLock.release();
    }
}