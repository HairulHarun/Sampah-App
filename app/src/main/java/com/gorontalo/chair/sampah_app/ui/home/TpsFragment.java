package com.gorontalo.chair.sampah_app.ui.home;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.gorontalo.chair.sampah_app.MainActivity;
import com.gorontalo.chair.sampah_app.PekerjaanActivity;
import com.gorontalo.chair.sampah_app.R;
import com.gorontalo.chair.sampah_app.adapter.HttpsTrustManagerAdapter;
import com.gorontalo.chair.sampah_app.adapter.SessionAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.adapter.VolleyAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TpsFragment extends BottomSheetDialogFragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String ID;
    private TextView txtTpsNama, txtTpsDeskripsi;
    private Button btnAngkut;
    private CircleImageView photoTps;
    private SessionAdapter sessionAdapter;

    public TpsFragment() {
        // Required empty public constructor
    }

    public TpsFragment(String id_tps){
        this.ID = id_tps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tps, container, false);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());

        String[] items = ID.split("/");
        String pisah_id = items[0];
        String pisah_jenis = items[1];
        String pisah_status = items[2];

        txtTpsNama = (TextView) view.findViewById(R.id.txtTpsNama);
        txtTpsDeskripsi = (TextView) view.findViewById(R.id.txtTpsDeskripsi);
        btnAngkut = (Button) view.findViewById(R.id.btnTpsAngkut);
        photoTps = view.findViewById(R.id.tps_photo);

        if (pisah_jenis.equals("TPA")){
            getTPAById(pisah_id);
            btnAngkut.setBackgroundResource(R.color.colorRed);
            btnAngkut.setVisibility(View.VISIBLE);
            btnAngkut.setText("BUANG ?");
        }else if (pisah_jenis.equals("DINAS")){
            btnAngkut.setVisibility(View.INVISIBLE);
            txtTpsNama.setText("Dinas Lingkugan Hidup dan SDA");
            txtTpsDeskripsi.setText("Alamat : Kayubulan, Limboto, Gorontalo, 96181");
            Picasso.with(getActivity().getApplicationContext())
                    .load("https://i0.wp.com/hulondalo.id/wp-content/uploads/2019/11/Hulondalo.id-Stuban-DLH-Boltim.jpeg?resize=720%2C405&ssl=1")
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(photoTps);
        }else{
            getTPSById(pisah_id);
            if (pisah_status.equals("1")){
                btnAngkut.setVisibility(View.INVISIBLE);
            }else {
                btnAngkut.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private void getTPSById(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getTPSByID(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        final String nama_tps = jObj.getString("nama");
                        String photo_tps = jObj.getString("photo");
                        String deskripsi_tps = jObj.getString("deskripsi");

                        txtTpsNama.setText(nama_tps);
                        txtTpsDeskripsi.setText(deskripsi_tps);

                        Picasso.with(getActivity().getApplicationContext())
                                .load(new URLAdapter().getPhotoTps(photo_tps))
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(photoTps);

                        btnAngkut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity().getApplicationContext(), PekerjaanActivity.class);
                                intent.putExtra("id_tps", ID);
                                intent.putExtra("nama_tps", nama_tps);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void getTPAById(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getTPAByID(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        final String nama_tpa = jObj.getString("nama");
                        String photo_tpa = jObj.getString("photo");
                        String deskripsi_tpa = jObj.getString("deskripsi");

                        txtTpsNama.setText(nama_tpa);
                        txtTpsDeskripsi.setText(deskripsi_tpa);

                        Picasso.with(getActivity().getApplicationContext())
                                .load(new URLAdapter().getPhotoTps(photo_tpa))
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(photoTps);

                        btnAngkut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buangSampah();
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

    private void buangSampah() {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().buangSampah(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString("hasil").toString(), Toast.LENGTH_LONG).show();
                        refresh();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString("hasil").toString(), Toast.LENGTH_LONG).show();
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
                params.put("id", sessionAdapter.getId());
                params.put("latitude", sessionAdapter.getLatitude());
                params.put("longitude", sessionAdapter.getLongitude());

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }

    private void refresh(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }
}
