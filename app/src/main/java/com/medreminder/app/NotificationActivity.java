package com.medreminder.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Activity shows on notification click
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

        if( intent.getExtras().getSerializable( "notificationContent" ) != null ){
            notificationActivityContent = ( TextView ) findViewById( R.id.notificationActivityContent );
            Reminder reminder = ( Reminder ) intent.getExtras().getSerializable( "notificationContent" );

            notificationActivityContent.setText( "Take " + reminder.getMedName() + " the dosage is " + reminder.getMedDose() + " " + reminder.getMedDoseUnit() );
        }

    }
}
