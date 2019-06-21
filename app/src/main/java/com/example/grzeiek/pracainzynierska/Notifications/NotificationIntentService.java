package com.example.grzeiek.pracainzynierska.Notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.grzeiek.pracainzynierska.Database.DBManager;
import com.example.grzeiek.pracainzynierska.NotificationActivity;
import com.example.grzeiek.pracainzynierska.R;
import com.example.grzeiek.pracainzynierska.broadcast_reciver.NotificationEventReceiver;

import java.sql.SQLException;


public class NotificationIntentService extends IntentService {

    private DBManager dbManager = new DBManager( this );

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_WORKING = "ACTION_WORKING";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    static final int NOTIFICATION_ID = 1;

    public NotificationIntentService() {
        super( NotificationIntentService.class.getSimpleName() );
    }

    public static Intent createIntentStartNotificationService( Context context ) {
        Intent intent = new Intent( context, NotificationIntentService.class );
        intent.setAction( ACTION_START );
        return intent;
    }

    public static Intent createWorkingStartNotificationService( Context context, int alarmId ) {
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

    @Override
    protected void onHandleIntent( Intent intent ) {
        Log.d( getClass().getSimpleName(), "onHandleIntent, started handling a notification event" );
        try {
            String action = intent.getAction();
            if ( ACTION_START.equals( action ) ) {
                processStartNotification();
            } else if ( ACTION_WORKING.equals( action ) ) {
                Bundle extras = intent.getExtras();
                int alarmId = extras.getInt( "alarmId" );
                processWorkingNotification( alarmId );
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent( intent );
        }
    }

    private void processWorkingNotification( int alarmId ) {
        String[] med = new String[ 3 ];
        try {
            dbManager.open();
            med = dbManager.getDataByAlarmId( alarmId );
            dbManager.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }

        if( med == null && med.length == 0 )
            return;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setContentTitle( med[ 0 ] )
                .setAutoCancel( true )
                .setColor( getResources().getColor( R.color.colorPrimaryDark ) )
                .setContentText( "Take " + med[ 0 ] + " the dose is " + med[ 1 ] + " " + med[ 2 ] )
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
    }

    private void processDeleteNotification( Intent intent ) {
        // Log something?
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        final NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setContentTitle( "Scheduled Notification yeah" )
                .setAutoCancel( true )
                .setColor( getResources().getColor( R.color.colorPrimaryDark ) )
                .setContentText( "This notification has been triggered by Notification Service" )
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setSmallIcon( R.drawable.ic_notifications_black_24dp );

        //Vibration
        builder.setVibrate( new long[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 } );

        //LED
        builder.setLights( Color.BLUE, 5000, 5000 );

        Intent mainIntent = new Intent( this, NotificationActivity.class );
        PendingIntent pendingIntent = PendingIntent.getActivity( this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent( pendingIntent );
        builder.setDeleteIntent( NotificationEventReceiver.getDeleteIntent( this ) );

        final NotificationManager manager = ( NotificationManager ) this.getSystemService( Context.NOTIFICATION_SERVICE );
        manager.notify( NOTIFICATION_ID, builder.build() );
    }
}
