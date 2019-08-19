package com.medreminder.app.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medreminder.app.Adapters.RecyclerViewPrescriptionsAdapter;
import com.medreminder.app.Database.FirebaseDBHelper;
import com.medreminder.app.Models.Prescription;
import com.medreminder.app.Models.PrescriptionMedications;
import com.medreminder.app.Models.User;
import com.medreminder.app.R;
import com.medreminder.app.registerAndLogin.CustomToast;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment fetching prescriptions from firebase and displaying them in recycler view
 */
public class PrescriptionsFragment extends Fragment {
    private FirebaseDBHelper mFirebaseDBHelper;
    private RecyclerView mRecyclerView;
    private List<Prescription> prescriptions = new ArrayList<>(  );
    private FirebaseUser mUser;
    private String userId;

    public PrescriptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.fragment_prescriptions, container, false );
        final TextView mNoData = view.findViewById( R.id.prescriptions_no_data_txtView );
        mRecyclerView = view.findViewById( R.id.prescriptions_recyclerview );

        mFirebaseDBHelper = new FirebaseDBHelper();
        mUser = mFirebaseDBHelper.mAuth.getCurrentUser();
        if( mUser == null  )
            return view;

        userId = mUser.getUid();

        fetchPrescriptionsFromFirebase( view, container, mNoData );

        // Inflate the layout for this fragment
        return view;
    }

    private void fetchPrescriptionsFromFirebase( final View view, final ViewGroup container, final TextView mNoData ) {
        mFirebaseDBHelper.mFirebaseUsersReference.child( userId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                if( dataSnapshot.exists()){
                    User user = dataSnapshot.getValue( User.class);
                    String userPesel = user.getPesel();

                    mFirebaseDBHelper.mFirebasePrescriptionsReference.child( userPesel ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                            prescriptions.clear();
                            List<String> keys = new ArrayList<>(  );
                            //get prescription
                            for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                                keys.add( snapshot.getKey() );
                                Prescription prescriptions = snapshot.getValue( Prescription.class);


                                List<PrescriptionMedications> prescriptionMedications = new ArrayList<>(  );

                                //get medications node from prescriptions
                                DataSnapshot medications = snapshot.child( "medications" );
                                Iterable<DataSnapshot> medicationChildren = medications.getChildren();

                                for( DataSnapshot med : medicationChildren ){
                                    PrescriptionMedications prescriptionMedication = med.getValue( PrescriptionMedications.class);
                                    prescriptionMedications.add( prescriptionMedication );
                                }

                                prescriptions.setPrescriptionMedications( prescriptionMedications );
                                prescriptions.setPrescriptionId( snapshot.getKey() );
                                PrescriptionsFragment.this.prescriptions.add( prescriptions );
                            }
                            prescriptions.size();


                            view.findViewById( R.id.loading_prescriptions_progressBar ).setVisibility( View.GONE );
                            new RecyclerViewPrescriptionsAdapter().initialize( mRecyclerView, container.getContext(), prescriptions, keys  );

                            if( prescriptions.size() == 0 )
                                mNoData.setVisibility( View.VISIBLE );
                            else
                                mNoData.setVisibility( View.GONE );
                        }

                        @Override
                        public void onCancelled( @NonNull DatabaseError databaseError ) {
                            new CustomToast().showToast( container.getContext(), view, "Failed to load prescriptions." );
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
