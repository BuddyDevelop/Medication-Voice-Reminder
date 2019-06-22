package com.example.grzeiek.pracainzynierska.Notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.grzeiek.pracainzynierska.Database.DBManager;
import com.example.grzeiek.pracainzynierska.NotificationActivity;
import com.example.grzeiek.pracainzynierska.R;
import com.example.grzeiek.pracainzynierska.Reminder;
import com.example.grzeiek.pracainzynierska.TTS;
import com.example.grzeiek.pracainzynierska.broadcast_reciver.NotificationEventReceiver;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Create notification, show it to users and speak them using TTS
 */
public class NotificationIntentService extends IntentService {

    private DBManager dbManager = new DBManager( this );
    private ArrayList<Reminder> reminderArrayList;


    private static final String ACTION_WORKING = "ACTION_WORKING";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super( NotificationIntentService.class.getSimpleName() );
    }

    public static Intent createWorkingNotificationService( Context context, int alarmId ) {
        Intent intent = new Intent( context, NotificationIntentService.class );
        intent.setAction( ACTION_WORKING );
        intent.putExtra( "alarmId", alarmId );
        return intent;
    }

    public static Intent createIntentDeleteNotification( Context context ) {
        Intent intent = new Intent( context, NotificationIntentService.class );
        intent.setAction( ACTION_DELETE );
        return intent;
    }

    private void processDeleteNotification( Intent intent ) {
        // Log something?
    }

    @Override
    protected void onHandleIntent( Intent intent ) {
        Log.d( getClass().getSimpleName(), "onHandleIntent, started handling a notification event" );
        try {
            String action = intent.getAction();
            if ( ACTION_WORKING.equals( action ) ) {
                Bundle extras = intent.getExtras();
                int alarmId = extras.getInt( "alarmId" );
                processWorkingNotification( alarmId );
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent( intent );
        }
    }

    private void processWorkingNotification( int alarmId ) {
        Reminder reminder = new Reminder();
        String notificationContentText = "";
        try {
            dbManager.open();
            reminder = dbManager.getReminderByAlarmId( alarmId );
            dbManager.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }

        if ( reminder.getMedName() == null || reminder.getMedDose() == null || reminder.getMedDoseUnit() == null )
            return;

        notificationContentText = "Take " + reminder.getMedName() + " dosage is " +
                reminder.getMedDose() + " " + reminder.getMedDoseUnit();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setContentTitle( reminder.getMedName() )
                .setAutoCancel( true )
                .setColor( getResources().getColor( R.color.colorPrimaryDark ) )
                .setContentText( notificationContentText )
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setSmallIcon( R.drawable.ic_notifications_black_24dp );

        //Vibration
        builder.setVibrate( new long[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 } );
        //LED
        builder.setLights( Color.BLUE, 5000, 5000 );


        Intent mainIntent = new Intent( this, NotificationActivity.class );
        PendingIntent pendingIntent = PendingIntent.getActivity( this,
                alarmId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent( pendingIntent );
        builder.setDeleteIntent( NotificationEventReceiver.getDeleteIntent( this ) );

        final NotificationManager manager = ( NotificationManager ) this.getSystemService( Context.NOTIFICATION_SERVICE );
        manager.notify( alarmId, builder.build() );


        if ( !TTS.isIntentServiceRunning ) {
            ArrayList<String> notificationsContentArr = new ArrayList<>();
            ArrayList<String> reminderWithSameTime = findReminderWithSameTimeAndDate( reminder.getMedName(), reminder.getReminderTime(), reminder.getReminderDays() );

            notificationsContentArr.add( notificationContentText );
            if ( reminderWithSameTime != null )
                notificationsContentArr.addAll( reminderWithSameTime );
            // speak notification content aloud
            speakNotificationService( getApplicationContext(), notificationsContentArr );


            TTS.isIntentServiceRunning = true;
        }
    }

    //find reminders with same alarm time to speak them using tts
    @Nullable
    private ArrayList<String> findReminderWithSameTimeAndDate( String medName, String medTime, String medDay ) {
        try {
            dbManager.open();
            reminderArrayList = dbManager.getRemindersByTime( medName, medTime );
            dbManager.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        if ( reminderArrayList != null )
            if ( reminderArrayList.isEmpty() )
                return null;

        String remDays;
        ArrayList<String> notificationContent = new ArrayList<>();

        for ( Reminder reminder : reminderArrayList ) {
            remDays = reminder.getReminderDays();

            if ( remDays.contains( medDay ) || remDays.contains( "Daily" ) )
                notificationContent.add( "Take " + reminder.getMedName() + " dosage is " +
                        reminder.getMedDose() + " " + reminder.getMedDoseUnit() );
        }

        return notificationContent;
    }

    private void speakNotificationService( Context context, ArrayList<String> notificationsContentArr ) {
        Intent ttsIntent = new Intent( context, TTS.class );
        ttsIntent.putStringArrayListExtra( "ttsContent", notificationsContentArr );
        getApplicationContext().startService( ttsIntent );
    }
}
