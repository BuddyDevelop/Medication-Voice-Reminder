package com.medreminder.app.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.medreminder.app.Activities.NotificationActivity;
import com.medreminder.app.Database.DBManager;
import com.medreminder.app.Models.Reminder;
import com.medreminder.app.R;
import com.medreminder.app.TTS;

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

    @Override
    protected void onHandleIntent( Intent intent ) {
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

        notificationContentText = getString( R.string.take ) + " " + reminder.getMedName() + " " +
                getString( R.string.notification_dosage ) + " " + reminder.getMedDose() +
                " " + reminder.getMedDoseUnit();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setContentTitle( reminder.getMedName() )
                .setAutoCancel( true )
                .setColor( getResources().getColor( R.color.colorPrimaryDark ) )
                .setStyle( new NotificationCompat.BigTextStyle()
                        .bigText( notificationContentText ) )
                .setContentText( notificationContentText )
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setSmallIcon( R.mipmap.ic_notification_filled_round );

        //Vibration
        builder.setVibrate( new long[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 } );
        //LED
        builder.setLights( Color.BLUE, 1500, 1500 );


        Intent mainIntent = new Intent( this, NotificationActivity.class );
        mainIntent.putExtra( "notificationContent", reminder );
        PendingIntent pendingIntent = PendingIntent.getActivity( this,
                alarmId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent( pendingIntent );

        final NotificationManager manager = ( NotificationManager ) this.getSystemService( Context.NOTIFICATION_SERVICE );
        manager.notify( alarmId, builder.build() );

//        check if user want to have notifications spoken loud
        speakNotificationLoud( getApplicationContext(), reminder );
    }

    private void speakNotificationLoud( Context context, Reminder reminder ) {
        final SharedPreferences userPreferences = getSharedPreferences( "enable_speak_notification", MODE_PRIVATE );
        if ( userPreferences.getBoolean( "enabled", true ) ) {

            //speak notifications
            if ( !TTS.isIntentServiceRunning ) {
                ArrayList<String> notificationsContentArr = new ArrayList<>();
                ArrayList<String> reminderWithSameTime = findReminderWithSameTimeAndDate( reminder.getMedName(), reminder.getReminderTime(), reminder.getReminderDays() );

                notificationsContentArr.add( returnNotificationContent( reminder.getMedName(), reminder.getMedDose(), reminder.getMedDoseUnit() ) );
                if ( reminderWithSameTime != null )
                    notificationsContentArr.addAll( reminderWithSameTime );

                // speak notification content aloud
                speakNotificationService( context, notificationsContentArr );

                TTS.isIntentServiceRunning = true;
            }
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
                notificationContent.add( returnNotificationContent( reminder.getMedName(),
                        reminder.getMedDose(), reminder.getMedDoseUnit() ) );
        }

        return notificationContent;
    }

    private void speakNotificationService( Context context, ArrayList<String> notificationsContentArr ) {
        Intent ttsIntent = new Intent( context, TTS.class );
        ttsIntent.putStringArrayListExtra( "ttsContent", notificationsContentArr );
        context.startService( ttsIntent );
    }

    private String returnNotificationContent( String medName, String medDose, String medDoseUnit ) {
        return getString( R.string.notification_from ) + getString( R.string.app_name ) + ". " +
                getString( R.string.notification_it_says ) + " " + medName +
                getString( R.string.notification_dosage ) + " " + medDose + " " + medDoseUnit;
    }
}
