package com.medreminder.app.Database;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDBHelper {
    public FirebaseDatabase mFirebaseDB;
    public DatabaseReference mFirebaseUsersReference;
    public DatabaseReference mFirebaseMedicationReference;
    public DatabaseReference mFirebasePrescriptionsReference;
    public FirebaseAuth mAuth;


    public FirebaseDBHelper(  ) {
        mFirebaseDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUsersReference = mFirebaseDB.getReference("Users");
        mFirebaseMedicationReference = mFirebaseDB.getReference("medications" );
        mFirebasePrescriptionsReference = mFirebaseDB.getReference("prescriptions" );
    }

    //update user's app token in database
    public static void updateUserToken( String token ){
        FirebaseDBHelper mFirebaseDBHelper = new FirebaseDBHelper();
        FirebaseUser mAuthCurrentUser = mFirebaseDBHelper.mAuth.getCurrentUser();
        if( mAuthCurrentUser == null ){
            return;
        }

        String userId = mAuthCurrentUser.getUid();
        DatabaseReference userRef = mFirebaseDBHelper.mFirebaseUsersReference.child( userId );
        Map<String, Object> updateToken = new HashMap<>( );
        updateToken.put( "token", token);

        userRef.updateChildren(updateToken);
    }
}
