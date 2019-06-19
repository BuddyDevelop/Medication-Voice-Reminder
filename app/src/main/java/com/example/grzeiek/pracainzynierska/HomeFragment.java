package com.example.grzeiek.pracainzynierska;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.grzeiek.pracainzynierska.Adapter.MedReminderAdapter;
import com.example.grzeiek.pracainzynierska.Database.DBManager;

import java.sql.SQLException;
import java.util.ArrayList;



public class HomeFragment extends Fragment {

    private DBManager dbManager;
    private ArrayList<Reminder> arrayList;
    private MedReminderAdapter medReminderAdapter;
    private ListView listView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view;
        dbManager = new DBManager( getContext() );
        arrayList = new ArrayList<>();

        int layout = 0;

        try {
            dbManager.open();
            layout = dbManager.countRecords();
            dbManager.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        if ( layout == 0 ) {
            view = inflater.inflate( R.layout.fragment_home, container, false );

            //button add med
            Button addMedicine = ( Button ) view.findViewById( R.id.add_med_btn );
            addMedicine.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    Intent intent = new Intent( getContext(), AddMedicineActivity.class );
                    startActivityForResult( intent, 1 );
                }
            } );
        } else {
            view = inflater.inflate( R.layout.fragment_medications_reminders, container, false );

            try {
                dbManager.open();
                arrayList = dbManager.fetchAll();
                dbManager.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }

            if ( arrayList == null || arrayList.isEmpty() )
                return view;

            //put existing reminders into home view
            listView = ( ListView ) view.findViewById( R.id.med_reminders );
            medReminderAdapter = new MedReminderAdapter( getContext(), R.layout.list_item, arrayList );
            listView.setAdapter( medReminderAdapter );

            //edit reminder
            listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
                    //id in db of clicked elem
                    // arrayList.get( i ).getId();

                    Reminder editReminder = new Reminder(
                            arrayList.get( i ).getId(),
                            arrayList.get( i ).getMedName(),
                            arrayList.get( i ).getReminderTime(),
                            arrayList.get( i ).getMedDose(),
                            arrayList.get( i ).getMedDoseUnit(),
                            arrayList.get( i ).getReminderDays()
                    );


                    Intent editIntent = new Intent( getContext(), AddMedicineActivity.class );
                    editIntent.putExtra( "reminder", editReminder );
                    startActivity( editIntent );
//                    startActivityForResult( editIntent, 2 );

                }
            } );


            //fab add med
            FloatingActionButton addMedication = ( FloatingActionButton ) view.findViewById( R.id.fab_add_med );

            addMedication.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    Intent intent = new Intent( getActivity(), AddMedicineActivity.class );
//                    startActivityForResult( intent, 1 );
                    startActivity( intent );
                }
            } );
        }

        return view;
    }
}
