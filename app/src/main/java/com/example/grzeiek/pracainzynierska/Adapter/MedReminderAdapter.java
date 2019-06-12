package com.example.grzeiek.pracainzynierska.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.grzeiek.pracainzynierska.R;
import com.example.grzeiek.pracainzynierska.Reminder;

import java.util.ArrayList;


public class MedReminderAdapter extends ArrayAdapter {

    private ArrayList<Reminder> reminders;

    public MedReminderAdapter( Context context, int resource, ArrayList<Reminder> list ) {
        super( context, resource );
        reminders = list;
    }

    static class LayoutHandler {
        TextView medName, reminderTime, reminderDays;
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Object getItem( int position ) {
        return reminders.get( position );
    }

    public ArrayList<Reminder> getData(){
        return reminders;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        View mview = convertView;
        LayoutHandler layoutHandler;

        if ( mview == null ) {
            LayoutInflater layoutInflater = ( LayoutInflater ) this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            mview = layoutInflater.inflate( R.layout.list_item, parent, false );

            layoutHandler = new LayoutHandler();
            layoutHandler.medName = ( TextView ) mview.findViewById( R.id.reminder_med_name );
            layoutHandler.reminderDays = ( TextView ) mview.findViewById( R.id.reminder_days );
            layoutHandler.reminderTime = ( TextView ) mview.findViewById( R.id.reminder_time );

            mview.setTag( layoutHandler );
        } else {
            layoutHandler = ( LayoutHandler ) mview.getTag();
        }

        Reminder reminder = ( Reminder ) this.getItem( position );
        layoutHandler.medName.setText( reminder.getMedName() );
        layoutHandler.reminderDays.setText( reminder.getReminderDays() );
        layoutHandler.reminderTime.setText( reminder.getReminderTime() );

        return mview;
    }

}
