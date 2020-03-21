package com.gorontalo.chair.sampah_app.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import com.gorontalo.chair.sampah_app.LoginActivity;
import com.gorontalo.chair.sampah_app.R;
import com.gorontalo.chair.sampah_app.UserActivity;
import com.gorontalo.chair.sampah_app.adapter.SessionAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class DashboardFragment extends Fragment {
    private TextView txtSopir, txtKondektur1, txtKondektur2, txtKenderaan, txtNoPolisi, txtHp, txtTanki;
    private ImageView btnLogout;
    private CircleImageView photoKenderaan, photoSopir, photoKondektur1, photoKondektur2;
    private SessionAdapter sessionAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtSopir = (TextView) root.findViewById(R.id.txtProfileSopir);
        txtKondektur1 = (TextView) root.findViewById(R.id.txtProfileKondektur1);
        txtKondektur2 = (TextView) root.findViewById(R.id.txtProfileKondektur2);
        txtKenderaan = (TextView) root.findViewById(R.id.txtProfileKenderaan);
        txtNoPolisi = (TextView) root.findViewById(R.id.txtProfileNoPolisi);
        txtHp = (TextView) root.findViewById(R.id.txtProfileHp);
        txtTanki = (TextView) root.findViewById(R.id.txtProfileTanki);

        btnLogout = (ImageView) root.findViewById(R.id.btnLogout);

        photoKenderaan = root.findViewById(R.id.user_profile_photo);
        photoSopir = root.findViewById(R.id.user_profile_photo2);
        photoKondektur1 = root.findViewById(R.id.user_profile_photo3);
        photoKondektur2 = root.findViewById(R.id.user_profile_photo4);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());

        txtSopir.setText(sessionAdapter.getSopir());
        txtKondektur1.setText(sessionAdapter.getKondektur1());
        txtKondektur2.setText(sessionAdapter.getKondektur2());
        txtKenderaan.setText(sessionAdapter.getKenderaan());
        txtNoPolisi.setText(sessionAdapter.getNoPolisi());
        txtHp.setText(sessionAdapter.getHp());
        txtTanki.setText(sessionAdapter.getTanki());

        Picasso.with(getActivity().getApplicationContext())
                .load(new URLAdapter().getPhotoPetugas(sessionAdapter.getPhotoKenderaan()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(photoKenderaan);

        Picasso.with(getActivity().getApplicationContext())
                .load(new URLAdapter().getPhotoPetugas(sessionAdapter.getPhotoSopir()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(photoSopir);

        Picasso.with(getActivity().getApplicationContext())
                .load(new URLAdapter().getPhotoPetugas(sessionAdapter.getPhotoKondektur1()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(photoKondektur1);

        Picasso.with(getActivity().getApplicationContext())
                .load(new URLAdapter().getPhotoPetugas(sessionAdapter.getPhotoKondektur2()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(photoKondektur2);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionAdapter.logoutUser();
            }
        });

        return root;
    }
}