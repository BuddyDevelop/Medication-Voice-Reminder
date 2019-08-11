package com.medreminder.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medreminder.app.Models.Receipt;
import com.medreminder.app.Models.ReceiptMedication;
import com.medreminder.app.R;

import java.util.List;

public class RecyclerViewReceiptsAdapter {
    private Context mContext;
    private ReceiptAdapter mReceiptAdapter;

    public void initialize( RecyclerView recyclerView, Context context, List<Receipt> receipts, List<String> keys){
        mContext = context;
        mReceiptAdapter = new RecyclerViewReceiptsAdapter.ReceiptAdapter( receipts, keys );
        recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
        recyclerView.setAdapter( mReceiptAdapter );
    }

    class ReceiptItemView extends RecyclerView.ViewHolder{
        private TextView mReceiptId;
        private TextView mRealizeTo;
        private TextView mDoctorName;
        private TextView mReceiptMeds;

        private String key;

        public ReceiptItemView( @NonNull ViewGroup parent ) {
            super( LayoutInflater.from( mContext ).inflate( R.layout.receipt_list_item, parent, false ) );

            mReceiptId = (TextView) itemView.findViewById( R.id.receipt_id_txtView );
            mRealizeTo = (TextView) itemView.findViewById( R.id.receipt_realizeTo_txtView );
            mDoctorName = (TextView) itemView.findViewById( R.id.receipt_doctor_name_txtView );
            mReceiptMeds = (TextView) itemView.findViewById( R.id.receipt_meds_txtView );
        }

        public void bind( Receipt receipt, String key ){
            mReceiptId.setText( "Receipt: " + receipt.getReceiptId() );
            mRealizeTo.setText( "Realize to: " + receipt.getRealizeTo() );
            mDoctorName.setText( "Set by: " + receipt.getDoctorName() );
            mReceiptMeds.setText( "Medications:" );
            List<ReceiptMedication> medications = receipt.getReceiptMedications();
            for (ReceiptMedication med: medications )
            {
                mReceiptMeds.append( "\n" );
                mReceiptMeds.append( med.getMedication() );
            }

            this.key = key;
        }
    }

    class ReceiptAdapter extends RecyclerView.Adapter<RecyclerViewReceiptsAdapter.ReceiptItemView>{
        private List<Receipt> mReceiptList;
        private List<String> mKeys;

        public ReceiptAdapter( List<Receipt> mReceiptList, List<String> mKeys ) {
            this.mReceiptList = mReceiptList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public RecyclerViewReceiptsAdapter.ReceiptItemView onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
            return new RecyclerViewReceiptsAdapter.ReceiptItemView( parent );
        }

        @Override
        public void onBindViewHolder( @NonNull RecyclerViewReceiptsAdapter.ReceiptItemView holder, int position ) {
            holder.bind( mReceiptList.get( position ), mKeys.get( position ) );
        }

        @Override
        public int getItemCount() {
            return mReceiptList.size();
        }
    }
}
