package com.gorontalo.chair.sampah_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gorontalo.chair.sampah_app.R;
import com.gorontalo.chair.sampah_app.model.PengumumanModel;

import java.util.List;

public class RecycleViewPengumumanAdapter extends RecyclerView.Adapter<RecycleViewPengumumanAdapter.ViewHolder> {

    Context context;
    List<PengumumanModel> pengumumanModels;

    public RecycleViewPengumumanAdapter(List<PengumumanModel> getDataAdapter, Context context){
        super();
        this.pengumumanModels = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pengumuman, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PengumumanModel getDataAdapter1 =  pengumumanModels.get(position);
        holder.txtIsi.setText(getDataAdapter1.getIsiPengumuman());
    }

    @Override
    public int getItemCount() {
        return pengumumanModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtIsi;
        public ViewHolder(View itemView) {
            super(itemView);
            txtIsi = (TextView) itemView.findViewById(R.id.txtIsiPengumuman) ;
        }
    }
}
