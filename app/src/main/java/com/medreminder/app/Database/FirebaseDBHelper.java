package com.medreminder.app.Database;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medreminder.app.Models.Medication;

import java.util.List;

public class FirebaseDBHelper {
    public FirebaseDatabase mFirebaseDB;
    public DatabaseReference mFirebaseUsersReference;
    public DatabaseReference mFirebaseMedicationReference;
    public DatabaseReference mFirebaseReceiptsReference;
    public FirebaseAuth mAuth;

    public interface DataStatus{
        void onDataLoaded( List<Medication> medications, List<String> keys);
        void onDataInserted();
        void onDataUpdated();
        void onDataDeleted();
    }

    public FirebaseDBHelper(  ) {
        mFirebaseDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUsersReference = mFirebaseDB.getReference("Users");
        mFirebaseMedicationReference = mFirebaseDB.getReference("medications" );
        mFirebaseReceiptsReference = mFirebaseDB.getReference("receipts" );
    }


//    public void getMedications( final DataStatus dataStatus){
//        DatabaseReference usersRef = mFirebaseDB.getReference("Users" );
//
//        //get user data to retrieve pesel
//        usersRef.child( userId ).addValueEventListener( new ValueEventListener() {
//            @Override
//            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                if( dataSnapshot.exists()){
//                    user = dataSnapshot.getValue(User.class);
//                    userPesel = user.getPesel();
//
//                    //user has been found in db so look for ReceiptMedication
//                    mFirebaseMedicationReference.child( userPesel ).addValueEventListener( new ValueEventListener() {
//                        @Override
//                        public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                            ReceiptMedication.clear();
//                            List<String> keys = new ArrayList<>(  );
//
//                            for( DataSnapshot keyNode : dataSnapshot.getChildren()){
//                                keys.add( keyNode.getKey() );
//                                Medication medication = keyNode.getValue(Medication.class);
//                                ReceiptMedication.add( medication );
//                            }
//                            dataStatus.onDataLoaded( ReceiptMedication, keys );
//                        }
//
//                        @Override
//                        public void onCancelled( @NonNull DatabaseError databaseError ) {
//                            Log.w("getMeds:onCancelled", databaseError.toException());
//                        }
//
//                    } );
//                }
//            }
//
//            @Override
//            public void onCancelled( @NonNull DatabaseError databaseError ) {
//                Log.w("getUser:onCancelled", databaseError.toException());
//
//            }
//        } );
//    }
}
