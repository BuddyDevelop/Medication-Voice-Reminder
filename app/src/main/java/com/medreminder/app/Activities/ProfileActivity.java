package com.medreminder.app.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medreminder.app.Database.FirebaseDBHelper;
import com.medreminder.app.Models.User;
import com.medreminder.app.R;

public class ProfileActivity extends AppCompatActivity {
    private TextView mProfileName;
    private TextView mProfileEmail;
    private TextView mProfilePesel;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );

        setViews();
        fetchUserData();
    }

    private void setViews(){
        mProfileName = findViewById( R.id.user_profile_name );
        mProfileEmail = findViewById( R.id.user_profile_email );
        mProfilePesel = findViewById( R.id.user_profile_pesel );
    }

    //fetch user's data and set text views with data
    private void fetchUserData() {
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
                        mProfileName.setText( mUser.getName() );
                        mProfileEmail.setText( mUser.getEmail() );
                        mProfilePesel.setText( mUser.getPesel() );
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError databaseError ) {

                    }
                } );
            }
        } );
    }

}
