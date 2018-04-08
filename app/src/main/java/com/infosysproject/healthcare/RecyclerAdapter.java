package com.infosysproject.healthcare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList<PatientData> arrayList;
    Context context;

    public RecyclerAdapter(ArrayList<PatientData> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, arrayList, context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        PatientData patientData = arrayList.get(position);
        holder.info.setText(patientData.getName()+ " " + patientData.getPatient_id());
        holder.time.setText(patientData.getTime());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView info, time;
        ArrayList<PatientData> arrayList;
        Context context;

        public RecyclerViewHolder(View itemView, ArrayList<PatientData> arrayList, Context context) {
            super(itemView);

            this.context = context;
            this.arrayList = arrayList;
            itemView.setOnClickListener(this);
            info = itemView.findViewById(R.id.info);
            time = itemView.findViewById(R.id.time);
        }

        @Override
        public void onClick(View view) {
            String patient_id = arrayList.get(getAdapterPosition()).getPatient_id();

            Intent intent = new Intent(this.context, DoctorPage.class);
            intent.putExtra("Patient_Id", patient_id);
            this.context.startActivity(intent);
        }
    }
}
