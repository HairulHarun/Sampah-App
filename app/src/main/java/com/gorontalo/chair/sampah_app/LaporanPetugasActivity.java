package com.gorontalo.chair.sampah_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gorontalo.chair.sampah_app.adapter.HttpsTrustManagerAdapter;
import com.gorontalo.chair.sampah_app.adapter.RecycleViewLaporanAdapter;
import com.gorontalo.chair.sampah_app.adapter.RecycleViewPengumumanAdapter;
import com.gorontalo.chair.sampah_app.adapter.SessionAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.adapter.VolleyAdapter;
import com.gorontalo.chair.sampah_app.model.LaporanPetugasModel;
import com.gorontalo.chair.sampah_app.model.PengumumanModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaporanPetugasActivity extends AppCompatActivity {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_HASIL = "hasil";

    List<LaporanPetugasModel> laporanPetugasModelList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    View ChildView ;
    int GetItemPosition ;
    ArrayList<String> Laporan;

    SessionAdapter sessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_petugas);

        sessionAdapter = new SessionAdapter(getApplicationContext());

        laporanPetugasModelList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        Laporan = new ArrayList<>();

        getData();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(LaporanPetugasActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    GetItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    Toast.makeText(LaporanPetugasActivity.this, Laporan.get(GetItemPosition), Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(LaporanPetugasActivity.this);
        progressDialog.setMessage("Mengambil Data Laporan ...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getLaporanPetugas(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    int success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("hasil");
                        for(int i = 0; i<jsonArray.length(); i++) {
                            LaporanPetugasModel laporanPetugasModel = new LaporanPetugasModel();
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
//                                Toast.makeText(getApplicationContext(), json.getString("tanggal_laporan"), Toast.LENGTH_LONG).show();
                                laporanPetugasModel.setTanggal(json.getString("tanggal_laporan"));
                                laporanPetugasModel.setNama(json.getString("nama_laporan"));
                                laporanPetugasModel.setIsi(json.getString("isi_laporan"));
                                laporanPetugasModel.setPhoto(json.getString("photo_laporan"));

                                Laporan.add(json.getString("tanggal_laporan"));
                                Laporan.add(json.getString("nama_laporan"));
                                Laporan.add(json.getString("isi_laporan"));
                                Laporan.add(json.getString("photo_laporan"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            laporanPetugasModelList.add(laporanPetugasModel);
                        }

                        recyclerViewadapter = new RecycleViewLaporanAdapter(laporanPetugasModelList, getApplicationContext());
                        recyclerView.setAdapter(recyclerViewadapter);

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_HASIL), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_petugas", sessionAdapter.getId());
                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(stringRequest, "json_pekerjaan");
    }
}