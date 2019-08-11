package com.medreminder.app.broadcast_reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.medreminder.app.Database.DBManager;
import com.medreminder.app.Models.Reminder;
import com.medreminder.app.RemindersManager;

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
        if ( intent == null )
            return;

//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
//        wl.acquire();

        //create alarms for all ReceiptMedication
        switch ( intent.getAction() ) {
            case Intent.ACTION_TIMEZONE_CHANGED:
            case Intent.ACTION_TIME_CHANGED:
            case Intent.ACTION_BOOT_COMPLETED:
                recreateAllAlarms( context );
                break;
        }

//        wl.release();
    }

    public void recreateAllAlarms( Context context ) {
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

                    RemindersManager.addReminder( context, alarmId, weekday, time );
                }
                dbManager.insertAlarmId( medRecordId, alarmIdString );
            }
            dbManager.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
}
