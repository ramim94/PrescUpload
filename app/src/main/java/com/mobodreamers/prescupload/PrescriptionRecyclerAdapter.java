package com.mobodreamers.prescupload;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobodreamers.prescupload.model.Prescription;

import java.util.List;

/**
 * Created by Ramim on 12/3/2016.
 */

public class PrescriptionRecyclerAdapter extends RecyclerView.Adapter<EachPrescription> {
   List<Prescription> prescriptions;
    Activity activity;

    public PrescriptionRecyclerAdapter(List<Prescription> prescriptions, Activity activity) {
        this.prescriptions = prescriptions;
        this.activity = activity;
    }

    @Override
    public EachPrescription onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prescription_each_prescription_view,parent,false);

        return new EachPrescription(itemView);
    }

    @Override
    public void onBindViewHolder(EachPrescription holder, int position) {
        Toast.makeText(activity, prescriptions.get(position).getImage(), Toast.LENGTH_SHORT).show();
        Glide.with(activity).load(prescriptions.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnailPresc);
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }
}

class EachPrescription extends RecyclerView.ViewHolder{

    ImageView thumbnailPresc;

    public EachPrescription(View itemView) {
        super(itemView);
        thumbnailPresc= (ImageView) itemView.findViewById(R.id.imageview_thumbnail);
    }
}
