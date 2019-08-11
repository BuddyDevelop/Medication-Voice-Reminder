package com.medreminder.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medreminder.app.Models.Medication;
import com.medreminder.app.R;

import java.util.List;

public class RecyclerViewMedicationsAdapter {
    private Context mContext;
    private MedicationAdapter mMedicationAdapter;

    public void initialize( RecyclerView recyclerView, Context context, List<Medication> medications, List<String> keys){
        mContext = context;
        mMedicationAdapter = new MedicationAdapter( medications, keys );
        recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
        recyclerView.setAdapter( mMedicationAdapter );
    }

    class MedicationItemView extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mDose;
        private TextView mDoseUnit;
        private TextView mStart;
        private TextView mEnds;

        private String key;

        public MedicationItemView( @NonNull ViewGroup parent ) {
            super( LayoutInflater.from( mContext ).inflate( R.layout.medication_list_item, parent, false ) );

            mName = (TextView) itemView.findViewById( R.id.receipt_id_txtView );
            mDose = (TextView) itemView.findViewById( R.id.receipt_meds_txtView );
            mDoseUnit = (TextView) itemView.findViewById( R.id.med_doseUnit_txtView );
            mStart = (TextView) itemView.findViewById( R.id.receipt_realizeTo_txtView );
            mEnds = (TextView) itemView.findViewById( R.id.med_ends_txtView );
        }

        public void bind( Medication medication, String key ){
            mName.setText( medication.getName() );
            mDose.setText( medication.getDose() );
            mDoseUnit.setText( medication.getDoseUnit() );
            mStart.setText( medication.getStart() );
            mEnds.setText( medication.getEnds() );
            this.key = key;
        }
    }

    class MedicationAdapter extends RecyclerView.Adapter<MedicationItemView>{
        private List<Medication> mMedicationList;
        private List<String> mKeys;

        public MedicationAdapter( List<Medication> mMedicationList, List<String> mKeys ) {
            this.mMedicationList = mMedicationList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public MedicationItemView onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
            return new MedicationItemView( parent );
        }

        @Override
        public void onBindViewHolder( @NonNull MedicationItemView holder, int position ) {
            holder.bind( mMedicationList.get( position ), mKeys.get( position ) );
        }

        @Override
        public int getItemCount() {
            return mMedicationList.size();
        }
    }
}
