package com.example.grzeiek.pracainzynierska;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rupins.drawercardbehaviour.CardDrawerLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private CardDrawerLayout drawer;

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //loading the default fragment
        loadFragment( new HomeFragment() );

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById( R.id.navigation );
        navigation.setOnNavigationItemSelectedListener( this );


        //Settings toolbar menu
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        //Drawer with animations
        drawer = ( CardDrawerLayout ) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
                switch ( item.getItemId() ) {
                    case R.id.nav_text_to_speech:
                        //Open Android Text-To-Speech Settings
                        if ( Build.VERSION.SDK_INT >= 14 ) {
                            Intent intent = new Intent();
                            intent.setAction( "com.android.settings.TTS_SETTINGS" );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity( intent );
                        } else {
                            Intent intent = new Intent();
                            intent.addCategory( Intent.CATEGORY_LAUNCHER );
                            intent.setComponent( new ComponentName( "com.android.settings", "com.android.settings.TextToSpeechSettings" ) );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity( intent );
                        }
                        break;
                    case R.id.nav_speak_notification_settings:
                        speakNotificationSettingsDialog();
                        break;

                }
                return false;
            }
        } );
//        navigationView.bringToFront();


        drawer.setViewScale( Gravity.START, 0.8f );
        drawer.setRadius( Gravity.START, 25 );
        drawer.setViewElevation( Gravity.START, 30 );
    }


    private boolean loadFragment( Fragment fragment ) {
        //switching fragment
        if ( fragment != null ) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.fragment_to_slide, fragment )  //.replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
        Fragment fragment = null;

        switch ( item.getItemId() ) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_medications:
                fragment = new MedicationsFragment();
                break;

            case R.id.navigation_prescriptions:
                fragment = new PrescriptionsFragment();
                break;

            case R.id.navigation_history:
                fragment = new HistoryFragment();
                break;

            default:
                fragment = new HomeFragment();
                break;
        }

        return loadFragment( fragment );
    }

    //back to fragment
    @Override
    public void onBackPressed() {
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    private void speakNotificationSettingsDialog() {
        final SharedPreferences userPreferences = getSharedPreferences( "speak_notification_settings", MODE_PRIVATE );
        final String[] speakNotificationSettings = getResources().getStringArray( R.array.speak_notifications_settings_dialog );
        final boolean[] selectedSettings = new boolean[ speakNotificationSettings.length ];

        // get user notification preferences
        for ( int i = 0; i < speakNotificationSettings.length; ++i ) {
            selectedSettings[ i ] = userPreferences.getBoolean( speakNotificationSettings[ i ], true );
        }

        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( R.string.speak_notification_settings_title );

        builder
                .setMultiChoiceItems( speakNotificationSettings, selectedSettings, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick( DialogInterface dialogInterface, int elem, boolean isChecked ) {
                        selectedSettings[ elem ] = isChecked;
                    }
                } )
                .setPositiveButton( R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        // edit user notification preferences
                        SharedPreferences.Editor preEditor = userPreferences.edit();
                        for ( int i = 0; i < speakNotificationSettings.length; ++i ) {
                            preEditor.putBoolean( speakNotificationSettings[ i ], selectedSettings[ i ] );
                        }
                        preEditor.apply();
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
}
