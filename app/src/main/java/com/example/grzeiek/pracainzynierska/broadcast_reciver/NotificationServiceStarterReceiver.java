package com.example.grzeiek.pracainzynierska.broadcast_reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Grześiek on 15.06.2019.
 * Broadcast receiver for: BOOT_COMPLETED, TIMEZONE_CHANGED, and TIME_SET events. Sets Alarm Manager for notification;
 */

public class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
//        NotificationEventReceiver.setupAlarm( context );
        if ( intent == null )
            return;

        switch ( intent.getAction() ) {
            case Intent.ACTION_TIMEZONE_CHANGED:
                break;
            case Intent.ACTION_TIME_CHANGED:
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                break;
        }
    }
}
