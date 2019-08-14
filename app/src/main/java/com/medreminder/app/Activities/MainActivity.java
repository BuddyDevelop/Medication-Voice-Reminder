package com.medreminder.app.Activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medreminder.app.Database.FirebaseDBHelper;
import com.medreminder.app.Fragments.HomeFragment;
import com.medreminder.app.Fragments.MedicationsFragment;
import com.medreminder.app.Fragments.PrescriptionsFragment;
import com.medreminder.app.Models.User;
import com.medreminder.app.MyFirebaseMessagingService;
import com.medreminder.app.R;
import com.rupins.drawercardbehaviour.CardDrawerLayout;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private CardDrawerLayout drawer;


    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //loading the default fragment
        loadFragment( new HomeFragment() );
        logout();

        //put username in drawer's header, next to logout btn
        getUserName();


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


        //drawer menu item click actions
        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
                switch ( item.getItemId() ) {
                    case R.id.profile_settings:
                        Intent profileActivityIntent = new Intent( getApplicationContext(), ProfileActivity.class);
                        startActivity( profileActivityIntent );
                        break;
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
                    case R.id.nav_enable_speak_notifications:
                        enableSpeakNotificationDialog();
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

    private void logout() {
        //control views inside NavigationView header
        NavigationView navigationView = findViewById( R.id.nav_view );
        View headerLayout = navigationView.getHeaderView( 0 );

        Button logout = ( Button ) headerLayout.findViewById( R.id.logout );

        logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                try {
                    //on logout click delete app token so this device does not receive notifications from FCM
                    MyFirebaseMessagingService.deleteToken( getApplicationContext() );
//                    Log.e( "LogoutAction:", "Logout try" );
//                    FirebaseAuth.getInstance().signOut();
//                    Intent intent = new Intent( getApplicationContext(), RegisterActivity.class );
//                    startActivity( intent );
//                    finish();  // because we do not want come here on back pressed
                } catch ( Exception e ) {
                    Log.e( "LogoutAction:", "Logout failure" );
                    e.printStackTrace();
                }
            }
        } );
    }

    private void getUserName(){
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                FirebaseDBHelper mFirebaseDBHelper = new FirebaseDBHelper();
                FirebaseUser user = mFirebaseDBHelper.mAuth.getCurrentUser();
                if( user == null )
                    return;

                String userId = user.getUid();
                mFirebaseDBHelper.mFirebaseUsersReference.child( userId ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                        User mUser = dataSnapshot.getValue(User.class);
                        NavigationView navigationView = findViewById( R.id.nav_view );
                        View headerLayout = navigationView.getHeaderView( 0 );

                        TextView mUserName = headerLayout.findViewById( R.id.drawer_user_profile_name );
                        mUserName.setText( mUser.getName() );
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError databaseError ) {

                    }
                } );
            }
        } );
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

//            case R.id.navigation_history:
//                fragment = new HistoryFragment();
//                break;

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
    //dialog in drawer to check if user want to have spoken notifications
    private void speakNotificationSettingsDialog() {
        final SharedPreferences userPreferences = getSharedPreferences( "speak_notification_settings", MODE_PRIVATE );
        final String[] speakNotificationSettings = getResources().getStringArray( R.array.speak_notifications_settings_dialog );
        final boolean[] selectedSettings = new boolean[ speakNotificationSettings.length ];

        // get user notification preferences
        for ( int i = 0; i < selectedSettings.length; ++i ) {
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
    //dialog in drawer for notification settings
    private void enableSpeakNotificationDialog() {
        final SharedPreferences userPreferences = getSharedPreferences( "enable_speak_notification", MODE_PRIVATE );
        final boolean[] checked = new boolean[ 1 ];

        checked[ 0 ] = userPreferences.getBoolean( "enabled", true );

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( R.string.enable_speak_notifications );

        String[] dialogTitle = { getResources().getString( R.string.enable_speak_notifications_dialog ) };

        builder
                .setMultiChoiceItems( dialogTitle, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick( DialogInterface dialogInterface, int elem, boolean isChecked ) {
                        checked[ elem ] = isChecked;
                    }
                } )
                .setPositiveButton( R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        SharedPreferences.Editor editor = userPreferences.edit();
                        editor.putBoolean( "enabled", checked[ 0 ] );
                        editor.apply();
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

    // To prevent crash on resuming activity  : interaction with fragments allowed only after Fragments Resumed or in OnCreate
    // http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }
}
