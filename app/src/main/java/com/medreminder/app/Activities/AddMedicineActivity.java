package com.medreminder.app.Activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.medreminder.app.Database.DBManager;
import com.medreminder.app.Models.Reminder;
import com.medreminder.app.R;
import com.medreminder.app.RemindersManager;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.antonious.materialdaypicker.MaterialDayPicker;


public class AddMedicineActivity extends AppCompatActivity {

    private DBManager dbManager;
    private Toolbar toolbar;

    private int hour, minute;
    private TextView medicationTime;
    private Button saveMedBtn;
    private EditText medName;
    private EditText medQuantity;
    private AppCompatSpinner medDoseUnit;
    private MaterialDayPicker materialDayPicker;
    private TextInputLayout dayPickerLayout, medNameLayout, medDoseLayout;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_medication );

        dbManager = new DBManager( this );

        //initialize variables
        toolbar = ( Toolbar ) findViewById( R.id.toolbar_add_medicine );
        saveMedBtn = ( Button ) findViewById( R.id.save_med_btn );
        medName = ( EditText ) findViewById( R.id.med_name );
        medQuantity = ( EditText ) findViewById( R.id.dose_quantity );
        medDoseUnit = ( AppCompatSpinner ) findViewById( R.id.spinner_dose_units );
        materialDayPicker = ( MaterialDayPicker ) findViewById( R.id.dayPicker );
        medicationTime = ( TextView ) findViewById( R.id.medicine_time );
        //for handling errors
        dayPickerLayout = ( TextInputLayout ) findViewById( R.id.dayPicker_layout );
        medNameLayout = ( TextInputLayout ) findViewById( R.id.med_name_layout );
        medDoseLayout = ( TextInputLayout ) findViewById( R.id.med_dose_layout );

        //on text changed errors handling
        medName.addTextChangedListener( new myTextWatcher( medName ) );
        medQuantity.addTextChangedListener( new myTextWatcher( medQuantity ) );

        //set current time in textView
        setCurrentTime( null, null );

        //set toolbar
        toolbar.setTitle( getString( R.string.add_medication_title ) );
        toolbar.setNavigationIcon( R.drawable.ic_arrow_back_white_24dp );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                finish();
            }
        } );


        //set every_day checkbox functionality
        AppCompatCheckBox appCompatCheckBox = ( AppCompatCheckBox ) findViewById( R.id.every_day );
        appCompatCheckBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton compoundButton, boolean isChecked ) {
                if ( compoundButton.isChecked() )
                    materialDayPicker.setSelectedDays( MaterialDayPicker.Weekday.getAllDays() );
                else
                    materialDayPicker.clearSelection();
            }
        } );


        //set time of reminder
        medicationTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                showTimePicker();
            }
        } );

        loadDataToEditReminder();

        //save med button action
        saveMedBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if ( formValidation() ) {
                    if ( getIntent().getExtras() == null )
                        addMed();
                    else
                        editMed();
                }
            }
        } );
    }


    //    Create notification reminders
    private void addReminder( long medRecordId, String stringMedDays, String time ) {
        String[] medDays = stringMedDays.trim().split( " " );
        String alarmIdString = "";
        int alarmId;

        for ( String weekday : medDays ) {
            alarmId = ( int ) System.currentTimeMillis();
            alarmIdString += alarmId + " ";
            RemindersManager.addReminder( getApplicationContext(), alarmId, weekday, time );
        }

        try {
            dbManager.open();
            dbManager.insertAlarmId( medRecordId, alarmIdString );
            dbManager.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private Reminder getData() {
        String stringMedName = medName.getText().toString().trim();
        String stringMedQuantity = medQuantity.getText().toString();
        String time = medicationTime.getText().toString();
        List<MaterialDayPicker.Weekday> weekdayList = materialDayPicker.getSelectedDays();
        String stringMedUnit = medDoseUnit.getSelectedItem().toString();

        String stringMedDays = "";
        if ( weekdayList.size() == 7 )
            stringMedDays = "Daily";
        else
            for ( int i = 0; i < weekdayList.size(); ++i )
                stringMedDays += weekdayList.get( i ).toString() + " ";

        return new Reminder( stringMedName, time, stringMedQuantity, stringMedUnit, stringMedDays );
    }

    private void editMed() {
        Reminder addReminder = getData();
        String stringMedName = addReminder.getMedName();
        String time = addReminder.getReminderTime();
        String stringMedQuantity = addReminder.getMedDose();
        String stringMedUnit = addReminder.getMedDoseUnit();
        String stringMedDays = addReminder.getReminderDays();
        long id;
        String reminderAlarmId;

        if ( getIntent().getExtras() == null || getIntent().getExtras().getSerializable( "reminder" ) == null )
            return;

//        get id and alarmIds of reminder
        Bundle extras = getIntent().getExtras();
        Reminder editReminder = ( Reminder ) extras.getSerializable( "reminder" );
        id = editReminder.getId();
        reminderAlarmId = editReminder.getReminderAlarmId();


        try {
            dbManager.open();
            if ( !dbManager.reminderExists( stringMedName, time, stringMedUnit, stringMedDays ) && id != -1 ) {
                dbManager.update( id, stringMedName, time, stringMedQuantity, stringMedUnit, stringMedDays );
                RemindersManager.cancelReminders( getApplicationContext(), reminderAlarmId );
                addReminder( id, stringMedDays, time );


                //show msg and back to main view
                Intent intent = new Intent( this, MainActivity.class );
                Toast.makeText( this, R.string.medication_edited, Toast.LENGTH_SHORT ).show();
                startActivity( intent );
                finish();
            } else {
                Toast.makeText( this, R.string.medication_exists, Toast.LENGTH_SHORT ).show();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void addMed() {
        Reminder addReminder = getData();
        String stringMedName = addReminder.getMedName();
        String time = addReminder.getReminderTime();
        String stringMedQuantity = addReminder.getMedDose();
        String stringMedUnit = addReminder.getMedDoseUnit();
        String stringMedDays = addReminder.getReminderDays();

        //variable to check if row has been added to DB
        long rowInserted = -1;

        try {
            dbManager.open();

            if ( !dbManager.reminderExists( stringMedName, time, stringMedUnit, stringMedDays ) ) {
                rowInserted = dbManager.insert( stringMedName, time, stringMedQuantity, stringMedUnit, stringMedDays );

                if ( rowInserted != -1 ) {
                    Toast.makeText( this, getString( R.string.medication ) +  " " + stringMedName + " " + getString( R.string.medication_saved ), Toast.LENGTH_SHORT ).show();
                    //create notification reminders for this medication
                    addReminder( rowInserted, stringMedDays, time );
                    Intent intent = new Intent( this, MainActivity.class );
                    startActivity( intent );
                    finish();
                } else
                    Toast.makeText( this, R.string.medication_exists, Toast.LENGTH_LONG ).show();

                dbManager.close();
            }
        } catch ( Exception ex ) {
            Log.d( "Insert err ", ex.toString() );
            ex.printStackTrace();
            Toast.makeText( this, R.string.medication_unidentified_err, Toast.LENGTH_SHORT ).show();
        }
    }


    //SETTING SENT VALUES IN FIELDS
    private void loadDataToEditReminder() {
        try {
            if ( getIntent().getExtras() == null )
                return;

            final Bundle extras = getIntent().getExtras();

            if ( extras.getSerializable( "reminder" ) == null )
                return;

            final Reminder editReminder = ( Reminder ) extras.getSerializable( "reminder" );


            //add toolbar icons and behaviour
            toolbar.setTitle( getString( R.string.edit_medication_title ) );
            ImageView deleteItem = new ImageView( this );
            deleteItem.setImageResource( android.R.drawable.ic_menu_delete );
            Toolbar.LayoutParams params = new Toolbar.LayoutParams( Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT );
            params.setMarginEnd( 24 );
            params.gravity = Gravity.END;
            deleteItem.setLayoutParams( params );
            toolbar.addView( deleteItem );

            //delete record behaviour
            deleteItem.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( AddMedicineActivity.this );
                    builder.setTitle( R.string.delete_med_dialog_title );

                    builder
                            .setPositiveButton( R.string.delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick( DialogInterface dialog, int which ) {
                                    long id = editReminder.getId();
                                    String alarmIds = editReminder.getReminderAlarmId();

                                    try {
                                        dbManager.open();
                                        dbManager.delete( id );
                                        RemindersManager.cancelReminders( getApplicationContext(), alarmIds ); // cancel reminder alarms
                                        Intent intent = new Intent( AddMedicineActivity.this, MainActivity.class );
                                        startActivity( intent );
                                    } catch ( SQLException e ) {
                                        e.printStackTrace();
                                    }
                                }
                            } )
                            .setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick( DialogInterface dialogInterface, int i ) {
                                    dialogInterface.dismiss();
                                }
                            } );
                    builder.show();
                }
            } );


            //get reminder time
            String[] remTime = editReminder.getReminderTime().split( ":" );
            Integer hourInt = null, minuteInt = null;
            if ( !remTime[ 0 ].isEmpty() )
                hourInt = Integer.parseInt( remTime[ 0 ] );
            if ( !remTime[ 1 ].isEmpty() )
                minuteInt = Integer.parseInt( remTime[ 1 ] );

            //get reminder days
            String[] remDays = editReminder.getReminderDays().trim().split( " " );

            //set fields with values
            medName.setText( editReminder.getMedName() );
            setCurrentTime( hourInt, minuteInt );
            medQuantity.setText( editReminder.getMedDose() );
            medDoseUnit.setSelection( ( ( ArrayAdapter<String> ) medDoseUnit.getAdapter() ).getPosition( editReminder.getMedDoseUnit() ) );
            setSelectedDays( remDays );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void setSelectedDays( String[] remDays ) {
        if ( remDays == null )
            return;

        if ( remDays[ 0 ].equals( "Daily" ) ) {
            materialDayPicker.setSelectedDays( MaterialDayPicker.Weekday.getAllDays() );
        } else {
            for ( int i = 0; i < remDays.length; ++i ) {
                if ( remDays[ i ].contains( "MONDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.MONDAY );
                if ( remDays[ i ].contains( "TUESDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.TUESDAY );
                if ( remDays[ i ].contains( "WEDNESDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.WEDNESDAY );
                if ( remDays[ i ].contains( "THURSDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.THURSDAY );
                if ( remDays[ i ].contains( "FRIDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.FRIDAY );
                if ( remDays[ i ].contains( "SATURDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.SATURDAY );
                if ( remDays[ i ].contains( "SUNDAY" ) )
                    materialDayPicker.selectDay( MaterialDayPicker.Weekday.SUNDAY );
            }
        }
    }

    private boolean formValidation() {
        if ( !validateMedName() )
            return false;

        if ( !validateMedDays() )
            return false;

        if ( !validateMedDose() )
            return false;

        return true;
    }

    private boolean validateMedName() {
        if ( medName.getText().toString().trim().isEmpty() ) {
            medNameLayout.setError( getString( R.string.err_msg_medication_name ) );
            return false;
        } else {
            medNameLayout.setErrorEnabled( false );
        }

        return true;
    }

    private boolean validateMedDays() {
        if ( materialDayPicker.getSelectedDays().isEmpty() ) {
            dayPickerLayout.setError( getString( R.string.err_msg_medication_schedule ) );
            return false;
        } else {
            dayPickerLayout.setErrorEnabled( false );
        }

        return true;
    }

    private boolean validateMedDose() {
        String medQuant = medQuantity.getText().toString();

        if ( medQuant.trim().isEmpty() ) {
            medDoseLayout.setError( getString( R.string.err_msg_medication_dose ) );
            return false;
        } else if ( Float.parseFloat( medQuant ) == 0 ) {
            medDoseLayout.setError( getString( R.string.err_msg_medication_dose ) );
            return false;
        } else {
            medDoseLayout.setErrorEnabled( false );
        }

        return true;
    }

    private void showTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get( Calendar.HOUR_OF_DAY );
        minute = mCurrentTime.get( Calendar.MINUTE );
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog( AddMedicineActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet( TimePicker timePicker, int selectedHour, int selectedMinute ) {
                hour = selectedHour;
                minute = selectedMinute;
                medicationTime.setText( String.format( Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute ) );
            }
        }, hour, minute, true );

        mTimePicker.show();
    }

    private void setCurrentTime( @Nullable Integer hour, @Nullable Integer minute ) {
        if ( hour == null || minute == null ) {
            Calendar currTime = Calendar.getInstance();
            hour = currTime.get( Calendar.HOUR_OF_DAY );
            minute = currTime.get( Calendar.MINUTE );
        }

        medicationTime.setText( String.format( Locale.getDefault(), "%02d:%02d", hour, minute ) );
    }

    private class myTextWatcher implements TextWatcher {
        private View view;

        private myTextWatcher( View view ) {
            this.view = view;
        }

        public void beforeTextChanged( CharSequence charSequence, int start, int count, int after ) {
        }

        public void onTextChanged( CharSequence charSequence, int start, int before, int count ) {
        }

        public void afterTextChanged( Editable editable ) {
            switch ( view.getId() ) {
                case R.id.med_name:
                    validateMedName();
                    break;
                case R.id.dose_quantity:
                    validateMedDose();
                    break;
            }
        }
    }
}