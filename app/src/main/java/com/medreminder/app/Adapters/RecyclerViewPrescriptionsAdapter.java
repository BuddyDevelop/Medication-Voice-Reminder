package com.medreminder.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medreminder.app.Models.Prescription;
import com.medreminder.app.Models.PrescriptionMedications;
import com.medreminder.app.R;

import java.util.List;

public class RecyclerViewPrescriptionsAdapter {
    private Context mContext;
    private PrescriptionAdapter mPrescriptionAdapter;

    public void initialize( RecyclerView recyclerView, Context context, List<Prescription> prescriptions, List<String> keys){
        mContext = context;
        mPrescriptionAdapter = new PrescriptionAdapter( prescriptions, keys );
        recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
        recyclerView.setAdapter( mPrescriptionAdapter );
    }

    class PrescriptionItemView extends RecyclerView.ViewHolder{
        private TextView mPrescriptionId;
        private TextView mRealizeTo;
        private TextView mDoctorName;
        private TextView mPrescriptionMeds;

        private String key;

        public PrescriptionItemView( @NonNull ViewGroup parent ) {
            super( LayoutInflater.from( mContext ).inflate( R.layout.prescription_list_item, parent, false ) );

            mPrescriptionId = (TextView) itemView.findViewById( R.id.prescription_id_txtView );
            mRealizeTo = (TextView) itemView.findViewById( R.id.prescription_realizeTo_txtView );
            mDoctorName = (TextView) itemView.findViewById( R.id.prescription_doctor_name_txtView );
            mPrescriptionMeds = (TextView) itemView.findViewById( R.id.prescription_meds_txtView );
        }

        public void bind( Prescription prescription, String key ){
            mPrescriptionId.setText( "Prescription: " + prescription.getPrescriptionId() );
            mRealizeTo.setText( "Realize to: " + prescription.getRealizeTo() );
            mDoctorName.setText( "Set by: " + prescription.getDoctorName() );
            mPrescriptionMeds.setText( "Medications:" );
            List<PrescriptionMedications> medications = prescription.getPrescriptionMedications();
            for ( PrescriptionMedications med: medications )
            {
                mPrescriptionMeds.append( "\n" );
                mPrescriptionMeds.append( med.getMedication() );
            }

            this.key = key;
        }
    }

    class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionItemView>{
        private List<Prescription> mPrescriptionList;
        private List<String> mKeys;

        public PrescriptionAdapter( List<Prescription> mPrescriptionList, List<String> mKeys ) {
            this.mPrescriptionList = mPrescriptionList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public PrescriptionItemView onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
            return new PrescriptionItemView( parent );
        }

        @Override
        public void onBindViewHolder( @NonNull PrescriptionItemView holder, int position ) {
            holder.bind( mPrescriptionList.get( position ), mKeys.get( position ) );
        }

        @Override
        public int getItemCount() {
            return mPrescriptionList.size();
        }
    }
}
