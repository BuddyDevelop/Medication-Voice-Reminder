package com.medreminder.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.medreminder.app.Models.Reminder;
import com.medreminder.app.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter to show all medication reminders
 */
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

    public ArrayList<Reminder> getData() {
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
        //if device's lang is polish then change day of week names to polish,
        if ( Locale.getDefault().getDisplayLanguage().equals( "polski" ) ) {
            layoutHandler.reminderDays.setText( polishReminderDaysNames( reminder.getReminderDays() ) );
        } else {
            layoutHandler.reminderDays.setText( reminder.getReminderDays() );
        }
        layoutHandler.reminderTime.setText( reminder.getReminderTime() );

        return mview;
    }

    //polish names of days
    public String polishReminderDaysNames( String reminderDays ) {
        String reminderDaysInPolish = "";

        if ( reminderDays.contains( "SUNDAY" ) )
            reminderDaysInPolish += "Niedziela ";
        if ( reminderDays.contains( "MONDAY" ) )
            reminderDaysInPolish += "Poniedziałek ";
        if ( reminderDays.contains( "TUESDAY" ) )
            reminderDaysInPolish += "Wtorek ";
        if ( reminderDays.contains( "WEDNESDAY" ) )
            reminderDaysInPolish += "Środa ";
        if ( reminderDays.contains( "THURSDAY" ) )
            reminderDaysInPolish += "Czwartek ";
        if ( reminderDays.contains( "FRIDAY" ) )
            reminderDaysInPolish += "Piątek ";
        if ( reminderDays.contains( "SATURDAY" ) )
            reminderDaysInPolish += "Sobota ";
        if ( reminderDays.contains( "Daily" ) )
            reminderDaysInPolish += "Codziennie";

        return reminderDaysInPolish;
    }

}
