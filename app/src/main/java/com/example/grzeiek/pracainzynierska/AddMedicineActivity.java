package com.example.grzeiek.pracainzynierska;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.antonious.materialdaypicker.MaterialDayPicker;


public class AddMedicineActivity extends AppCompatActivity{

    private int hour, minute;
    private TextView medicationTime;
    private Button saveMedBtn;
    private EditText medName;
    private EditText medQuantity;
    private AppCompatSpinner medDoseUnit;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_medicine );


        final Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar_add_medicine );
        final MaterialDayPicker materialDayPicker = findViewById( R.id.dayPicker );
        medicationTime = ( TextView ) findViewById( R.id.medicine_time );
        saveMedBtn = ( Button ) findViewById( R.id.save_med_btn );
        medName = ( EditText ) findViewById( R.id.med_name );
        medQuantity = ( EditText ) findViewById( R.id.dose_quantity );
        medDoseUnit = ( AppCompatSpinner ) findViewById( R.id.spinner_dose_units );



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
                List<MaterialDayPicker.Weekday> weekdayList =  materialDayPicker.getSelectedDays();
                String pillName = medName.getText().toString();
                String dose = medQuantity.getText().toString();
                String medUnit = medDoseUnit.getSelectedItem().toString();
                for( int i = 0; i < weekdayList.size(); ++i ){
                    Toast.makeText( AddMedicineActivity.this, "" + weekdayList.get( i ), Toast.LENGTH_SHORT ).show();
                }
                Toast.makeText( AddMedicineActivity.this, "" + pillName, Toast.LENGTH_SHORT ).show();
                Toast.makeText( AddMedicineActivity.this, "" + dose, Toast.LENGTH_SHORT ).show();
                Toast.makeText( AddMedicineActivity.this, "" + medUnit, Toast.LENGTH_SHORT ).show();
            }
        } );

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


}
