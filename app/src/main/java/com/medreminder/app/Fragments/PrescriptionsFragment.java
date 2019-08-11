package com.medreminder.app.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medreminder.app.Adapters.RecyclerViewReceiptsAdapter;
import com.medreminder.app.Database.FirebaseDBHelper;
import com.medreminder.app.Models.Receipt;
import com.medreminder.app.Models.ReceiptMedication;
import com.medreminder.app.Models.User;
import com.medreminder.app.R;
import com.medreminder.app.registerAndLogin.CustomToast;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment fetching receipts from firebase and displaying them in recycler view
 */
public class PrescriptionsFragment extends Fragment {
    private FirebaseDBHelper mFirebaseDBHelper;
    private RecyclerView mRecyclerView;
    private List<Receipt> receipts = new ArrayList<>(  );
    private FirebaseUser mUser;
    private String userId;

    public PrescriptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.fragment_prescriptions, container, false );
        mRecyclerView = view.findViewById( R.id.receipts_recyclerview );

        mFirebaseDBHelper = new FirebaseDBHelper();
        mUser = mFirebaseDBHelper.mAuth.getCurrentUser();
        if( mUser == null  )
            return view;

        userId = mUser.getUid();

        fetchReceiptsFromFirebase( view, container );

        // Inflate the layout for this fragment
        return view;
    }

    private void fetchReceiptsFromFirebase( final View view, final ViewGroup container ) {
        mFirebaseDBHelper.mFirebaseUsersReference.child( userId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                if( dataSnapshot.exists()){
                    User user = dataSnapshot.getValue( User.class);
                    String userPesel = user.getPesel();

                    mFirebaseDBHelper.mFirebaseReceiptsReference.child( userPesel ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                            receipts.clear();
                            List<String> keys = new ArrayList<>(  );
                            //get receipt
                            for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                                keys.add( snapshot.getKey() );
                                Receipt receipt = snapshot.getValue(Receipt.class);


                                List<ReceiptMedication> receiptMedications = new ArrayList<>(  );

                                //get medications node from receipt
                                DataSnapshot medications = snapshot.child( "medications" );
                                Iterable<DataSnapshot> medicationChildren = medications.getChildren();

                                for( DataSnapshot med : medicationChildren ){
                                    ReceiptMedication receiptMedication = med.getValue(ReceiptMedication.class);
                                    receiptMedications.add( receiptMedication );
                                }

                                receipt.setReceiptMedications( receiptMedications );
                                receipt.setReceiptId( snapshot.getKey() );
                                receipts.add( receipt );
                            }
                            receipts.size();


                            view.findViewById( R.id.loading_receipts_progressBar ).setVisibility( View.GONE );
                            new RecyclerViewReceiptsAdapter().initialize( mRecyclerView, container.getContext(), receipts, keys  );

//                            if( receipts.size() == 0 )
//                                mNoData.setVisibility( View.VISIBLE );
//                            else
//                                mNoData.setVisibility( View.GONE );
                        }

                        @Override
                        public void onCancelled( @NonNull DatabaseError databaseError ) {
                            new CustomToast().showToast( container.getContext(), view, "Failed to load receipts." );
                        }
                    } );
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                new CustomToast().showToast( container.getContext(), view, "Failed to load user." );
            }
        } );
    }

}
