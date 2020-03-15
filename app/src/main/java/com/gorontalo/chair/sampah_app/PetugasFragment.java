package com.gorontalo.chair.sampah_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PetugasFragment extends BottomSheetDialogFragment {
    private TextView txtSopir, txtKondektur1, txtKondektur2, txtKenderaan, txtTanki;
    private Button btnLapor;
    private CircleImageView photoKenderaan;
    private String ID, SOPIR, KONDEKTUR1, KONDEKTUR2, KENDERAAN, PHOTO;
    private int TANKI;

    public PetugasFragment() {
        // Required empty public constructor
    }

    public PetugasFragment(String id, String sopir, String kondektur1, String kondektur2, String kenderaan, int tanki, String photo){
        this.ID = id;
        this.SOPIR = sopir;
        this.KONDEKTUR1 = kondektur1;
        this.KONDEKTUR2 = kondektur2;
        this.KENDERAAN = kenderaan;
        this.TANKI = tanki;
        this.PHOTO = photo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_petugas, container, false);

        txtSopir = (TextView) root.findViewById(R.id.txtPetugasSopir);
        txtKondektur1 = (TextView) root.findViewById(R.id.txtPetugasKondektur1);
        txtKondektur2 = (TextView) root.findViewById(R.id.txtPetugasKondektur2);
        txtKenderaan = (TextView) root.findViewById(R.id.txtPetugasKenderaan);
        txtTanki = (TextView) root.findViewById(R.id.txtPetugasTanki);
        btnLapor = (Button) root.findViewById(R.id.btnLapor);

        photoKenderaan = root.findViewById(R.id.photo_petugas);

        txtSopir.setText(SOPIR);
        txtKondektur1.setText(KONDEKTUR1);
        txtKondektur2.setText(KONDEKTUR2);
        txtKenderaan.setText(KENDERAAN);
        txtTanki.setText(String.valueOf(TANKI));

        Picasso.with(getActivity().getApplicationContext())
                .load(new URLAdapter().getPhotoPetugas(PHOTO))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(photoKenderaan);

        btnLapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), LaporActivity.class);
                intent.putExtra("id_petugas", ID);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
