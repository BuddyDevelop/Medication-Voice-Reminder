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
import com.medreminder.app.Adapters.RecyclerViewMedicationsAdapter;
import com.medreminder.app.Database.FirebaseDBHelper;
import com.medreminder.app.Models.Medication;
import com.medreminder.app.Models.User;
import com.medreminder.app.R;
import com.medreminder.app.registerAndLogin.CustomToast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MedicationsFragment extends Fragment {
    private FirebaseDBHelper mFirebaseDBHelper;
    private RecyclerView mRecyclerView;
    private List<Medication> medications = new ArrayList<>(  );
    private FirebaseUser mUser;
    private String userId;

    public MedicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView( LayoutInflater inflater, final ViewGroup container,
                              Bundle savedInstanceState ) {
        mFirebaseDBHelper = new FirebaseDBHelper();

        final View view = inflater.inflate( R.layout.fragment_medications, container, false );
        final TextView mNoData = view.findViewById( R.id.medications_no_data_txtView );
        mRecyclerView = (RecyclerView) view.findViewById( R.id.medications_recyclerview );

        mUser = mFirebaseDBHelper.mAuth.getCurrentUser();
        if( mUser == null  )
            return view;

        userId = mUser.getUid();

        fetchMedicationsFromFirebase( view, container, mNoData);

        // Inflate the layout for this fragment
        return view;
    }

    private void fetchMedicationsFromFirebase( final View view, final ViewGroup container, final TextView mNoData ) {
        mFirebaseDBHelper.mFirebaseUsersReference.child( userId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                if( dataSnapshot.exists()){
                    User user = dataSnapshot.getValue( User.class);
                    String userPesel = user.getPesel();

                    mFirebaseDBHelper.mFirebaseMedicationReference.child( userPesel ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                            medications.clear();
                            List<String> keys = new ArrayList<>(  );

                            for( DataSnapshot keyNode : dataSnapshot.getChildren()){
                                keys.add( keyNode.getKey() );
                                Medication medication = keyNode.getValue(Medication.class);
                                medications.add( medication );
                            }
                            view.findViewById( R.id.loading_medications_progressBar ).setVisibility( View.GONE );
                            new RecyclerViewMedicationsAdapter().initialize( mRecyclerView, container.getContext(), medications, keys  );

                            if( medications.size() == 0 )
                                mNoData.setVisibility( View.VISIBLE );
                            else
                                mNoData.setVisibility( View.GONE );
                        }

                        @Override
                        public void onCancelled( @NonNull DatabaseError databaseError ) {
                            //executed if user has no permissions to read data
                            new CustomToast().showToast( container.getContext(), view, "Failed to load medications." );
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

    @Override
    public void onStart() {
        super.onStart();
    }
}
