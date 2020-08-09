package com.gorontalo.chair.sampah_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gorontalo.chair.sampah_app.R;
import com.gorontalo.chair.sampah_app.model.LaporanPetugasModel;
import com.gorontalo.chair.sampah_app.model.PengumumanModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecycleViewLaporanAdapter extends RecyclerView.Adapter<RecycleViewLaporanAdapter.ViewHolder> {

    Context context;
    List<LaporanPetugasModel> laporanPetugasModels;

    public RecycleViewLaporanAdapter(List<LaporanPetugasModel> getDataAdapter, Context context){
        super();
        this.laporanPetugasModels = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_laporan, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LaporanPetugasModel getDataAdapter1 =  laporanPetugasModels.get(position);
        holder.txtTanggal.setText(getDataAdapter1.getTanggal());
        holder.txtNama.setText(getDataAdapter1.getNama());
        holder.txtIsi.setText(getDataAdapter1.getIsi());

        Picasso.with(context.getApplicationContext())
                .load(new URLAdapter().getPhotoLaporan(getDataAdapter1.getPhoto()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.imgLaporan);
    }

    @Override
    public int getItemCount() {
        return laporanPetugasModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTanggal;
        public TextView txtNama;
        public TextView txtIsi;
        public ImageView imgLaporan;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTanggal = (TextView) itemView.findViewById(R.id.txtLaporanTanggal) ;
            txtNama = (TextView) itemView.findViewById(R.id.txtLaporanNama) ;
            txtIsi = (TextView) itemView.findViewById(R.id.txtLaporanIsi) ;

            imgLaporan = (ImageView) itemView.findViewById(R.id.imgLaporan) ;
        }
    }
}
