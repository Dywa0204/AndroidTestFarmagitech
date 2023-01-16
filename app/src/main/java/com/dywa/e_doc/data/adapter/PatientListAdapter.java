package com.dywa.e_doc.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dywa.e_doc.R;
import com.dywa.e_doc.data.response.model.PatientData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    private List<PatientData> results = new ArrayList<>();
    private OnAdapterListener listener;

    public PatientListAdapter(List<PatientData> results, OnAdapterListener listener) {
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder (
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_patient_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PatientListAdapter.ViewHolder holder, int position) {
        PatientData result = results.get(position);
        holder.patient_name.setText(result.getPatient_name());
        holder.patient_id.setText("No. RM : " + result.getPatient_id());
        holder.address.setText("Alamat : " + result.getAddress());

        String gender = Objects.equals(result.getGender(), "L") ? "male" : "female";
        Picasso.get()
                .load( "https://xsgames.co/randomusers/avatar.php?g=" + gender + "&id=" + result.getPatient_id() )
                .placeholder(R.drawable.img_profile)
                .error(R.drawable.img_error)
                .fit(). centerCrop()
                .into(holder.userImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(result);
            }
        });
    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView patient_name;
        TextView patient_id;
        TextView address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_image);
            patient_name = itemView.findViewById(R.id.patient_name);
            patient_id = itemView.findViewById(R.id.patient_id);
            address = itemView.findViewById(R.id.address);

        }
    }

    public void setData(List<PatientData> data) {
        results.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        results.clear();
    }

    public interface OnAdapterListener {
        void onClick(PatientData patientListData);
    }
}
