package com.medreminder.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.medreminder.app.Models.Reminder;
import com.medreminder.app.R;

/**
 *  Shows Activity on notification click
 */

public class NotificationActivity extends AppCompatActivity {
    private TextView notificationActivityContent;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_notification );

        Intent intent = getIntent();
        if( intent == null )
            return;

        if( !intent.hasExtra( "notificationContent" ))
            return;

        if( intent.getExtras().getSerializable( "notificationContent" ) != null ){
            notificationActivityContent = ( TextView ) findViewById( R.id.notificationActivityContent );
            Reminder reminder = ( Reminder ) intent.getExtras().getSerializable( "notificationContent" );

            notificationActivityContent.setText( getString( R.string.take ) + " " + reminder.getMedName() + " " +
                    getString( R.string.notification_dosage ) + " " + reminder.getMedDose() +
                    " " + reminder.getMedDoseUnit() );
        }

    }
}
