package com.example.grzeiek.pracainzynierska;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.grzeiek.pracainzynierska.Database.DBManager;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.antonious.materialdaypicker.MaterialDayPicker;


public class AddMedicineActivity extends AppCompatActivity{

    private DBManager dbManager;

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
        final Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar_add_medicine );
        saveMedBtn = ( Button ) findViewById( R.id.save_med_btn );
        medName = ( EditText ) findViewById( R.id.med_name );
        medQuantity = ( EditText ) findViewById( R.id.dose_quantity );
        medDoseUnit = ( AppCompatSpinner ) findViewById( R.id.spinner_dose_units );
        materialDayPicker = ( MaterialDayPicker )findViewById( R.id.dayPicker );
        medicationTime = ( TextView ) findViewById( R.id.medicine_time );
        //for handling errors
        dayPickerLayout = ( TextInputLayout ) findViewById( R.id.dayPicker_layout );
        medNameLayout = ( TextInputLayout ) findViewById( R.id.med_name_layout );
        medDoseLayout = ( TextInputLayout ) findViewById( R.id.med_dose_layout );


        medName.addTextChangedListener( new myTextWatcher( medName ) );
        medQuantity.addTextChangedListener( new myTextWatcher( medQuantity ) );

        //set current time in textView
        setCurrentTime();

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
                if( compoundButton.isChecked() )
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


        //save med button action
        saveMedBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
            if( formValidation() ){
                addMed();
            }
            }
        } );
    }

    private void addMed() {
        String stringMedName = medName.getText().toString().trim();
        String stringMedQuantity = medQuantity.getText().toString();
        String time = hour + ":" + minute;
        List<MaterialDayPicker.Weekday> weekdayList =  materialDayPicker.getSelectedDays();
        String stringMedUnit = medDoseUnit.getSelectedItem().toString();

        String stringMedDays = "";
        for( int i = 0; i < weekdayList.size(); ++i )
            stringMedDays += weekdayList.get( i ).toString() + " ";

//        Toast.makeText( this, "" + stringMedDays, Toast.LENGTH_SHORT ).show();

        long rowInserted = -1;

        try {
            dbManager.open();

            if( !dbManager.reminderExists( stringMedName, time, stringMedUnit, stringMedDays ) ){
                rowInserted = dbManager.insert( stringMedName, time, stringMedQuantity, stringMedUnit, stringMedDays );

                if( rowInserted != -1 ) {
                    Toast.makeText( this, "Medication " + stringMedName + " has been saved", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( this, MainActivity.class );
                    startActivity( intent );
                }

            } else
                Toast.makeText( this, "There is reminder with such name and remind time", Toast.LENGTH_LONG ).show();

            dbManager.close();
        } catch ( Exception ex  ){
            Log.d( "Insert err ", ex.toString() );
            ex.printStackTrace();
            Toast.makeText( this, "Something went wrong in saving", Toast.LENGTH_SHORT ).show();
        }
    }

    private boolean formValidation() {
        if( !validateMedName() )
            return false;

        if( !validateMedDays() )
            return false;

        if( !validateMedDose() )
            return false;

        return true;
    }

    private boolean validateMedName() {
        if( medName.getText().toString().trim().isEmpty() ){
            medNameLayout.setError( getString( R.string.err_msg_medication_name ) );
            return false;
        } else {
            medNameLayout.setErrorEnabled( false );
        }

        return true;
    }

    private boolean validateMedDays() {
        if( materialDayPicker.getSelectedDays().isEmpty() ){
            dayPickerLayout.setError( getString( R.string.err_msg_medication_schedule ) );
            return false;
        } else {
            dayPickerLayout.setErrorEnabled( false );
        }

        return true;
    }

    private boolean validateMedDose() {
        String medQuant = medQuantity.getText().toString();

        if( medQuant.trim().isEmpty() ){
            medDoseLayout.setError( getString( R.string.err_msg_medication_dose ) );
            return false;
        } else if( Float.parseFloat( medQuant ) == 0 ) {
            medDoseLayout.setError( getString( R.string.err_msg_medication_dose ) );
            return false;
        } else {
            medDoseLayout.setErrorEnabled( false );
        }

        return true;
    }

    private void showTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog( AddMedicineActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet( TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                medicationTime.setText(String.format( Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);

        mTimePicker.show();
    }

    private void setCurrentTime(){
        Calendar currTime = Calendar.getInstance();
        hour = currTime.get( Calendar.HOUR_OF_DAY );
        minute = currTime.get( Calendar.MINUTE );

        medicationTime.setText( String.format( Locale.getDefault(), "%02d:%02d", hour, minute ) );
    }


    private class myTextWatcher implements TextWatcher{
        private View view;

        private myTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

        public void afterTextChanged(Editable editable) {
            switch ( view.getId() ){
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

