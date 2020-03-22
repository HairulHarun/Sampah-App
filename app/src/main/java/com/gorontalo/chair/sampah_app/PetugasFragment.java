package com.gorontalo.chair.sampah_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.gorontalo.chair.sampah_app.adapter.HttpsTrustManagerAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.adapter.VolleyAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PetugasFragment extends BottomSheetDialogFragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtSopir, txtKondektur1, txtKondektur2, txtKenderaan, txtTanki;
    private Button btnLapor;
    private CircleImageView photoKenderaan;
    private String ID;

    public PetugasFragment() {
        // Required empty public constructor
    }

    public PetugasFragment(String id){
        this.ID = id;
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

        getPetugasById(ID);

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

    private void getPetugasById(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getPetugasByID(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        final String sopir = jObj.getString("sopir");
                        String kondektur1 = jObj.getString("kondektur1");
                        String kondektur2 = jObj.getString("kondektur2");
                        final String kenderaan = jObj.getString("kenderaan");
                        String tanki = jObj.getString("tanki");
                        final String photo = jObj.getString("photokenderaan");

                        txtSopir.setText(sopir);
                        txtKondektur1.setText(kondektur1);
                        txtKondektur2.setText(kondektur2);
                        txtKenderaan.setText(kenderaan);
                        txtTanki.setText(String.valueOf(tanki));

                        Picasso.with(getActivity().getApplicationContext())
                                .load(new URLAdapter().getPhotoPetugas(photo))
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
                                intent.putExtra("kenderaan", kenderaan);
                                intent.putExtra("sopir", sopir);
                                intent.putExtra("photo", photo);
                                startActivity(intent);
                            }
                        });

                    } else {
                        Log.e(TAG, jObj.getString("hasil"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Error rute: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }
}
