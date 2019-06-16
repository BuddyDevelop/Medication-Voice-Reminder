package com.example.grzeiek.pracainzynierska.Notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.grzeiek.pracainzynierska.NotificationActivity;
import com.example.grzeiek.pracainzynierska.R;
import com.example.grzeiek.pracainzynierska.broadcast_reciver.NotificationEventReceiver;


public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
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
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent( intent );
        }
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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
