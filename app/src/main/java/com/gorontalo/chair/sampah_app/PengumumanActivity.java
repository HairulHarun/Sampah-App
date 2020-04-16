package com.gorontalo.chair.sampah_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gorontalo.chair.sampah_app.adapter.RecycleViewPengumumanAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.model.PengumumanModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PengumumanActivity extends AppCompatActivity {
    List<PengumumanModel> pengumumanModelList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    ProgressBar progressBar;
    JsonArrayRequest jsonArrayRequest ;
    RequestQueue requestQueue ;
    View ChildView ;
    int GetItemPosition ;
    ArrayList<String> Pengumuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengumuman);

        pengumumanModelList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        Pengumuman = new ArrayList<>();

        JSON_DATA_WEB_CALL();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(PengumumanActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    GetItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    Toast.makeText(PengumumanActivity.this, Pengumuman.get(GetItemPosition), Toast.LENGTH_LONG).show();
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

    public void JSON_DATA_WEB_CALL(){
        jsonArrayRequest = new JsonArrayRequest(new URLAdapter().getPengumuman(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            PengumumanModel pengumumanModel = new PengumumanModel();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                pengumumanModel.setIsiPengumuman(json.getString("isi_pengumuman"));
                Pengumuman.add(json.getString("isi_pengumuman"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pengumumanModelList.add(pengumumanModel);
        }

        recyclerViewadapter = new RecycleViewPengumumanAdapter(pengumumanModelList, this);
        recyclerView.setAdapter(recyclerViewadapter);

    }
}
