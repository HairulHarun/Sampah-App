package com.gorontalo.chair.sampah_app.ui.home;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gorontalo.chair.sampah_app.MainActivity;
import com.gorontalo.chair.sampah_app.R;
import com.gorontalo.chair.sampah_app.adapter.HttpsTrustManagerAdapter;
import com.gorontalo.chair.sampah_app.adapter.SessionAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.adapter.VolleyAdapter;
import com.gorontalo.chair.sampah_app.model.LocationModel;
import com.gorontalo.chair.sampah_app.model.PekerjaanModel;
import com.gorontalo.chair.sampah_app.service.TrackingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private SessionAdapter sessionAdapter;

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mProfileRef;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());
        sessionAdapter.checkLoginMain();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (!sessionAdapter.getId().equals("")) {
                gMap = googleMap;
                gMap.setMaxZoomPreference(16);
                LatLng wollongong = new LatLng(0.57395177, 123.07756159);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wollongong, 15));
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
//                gMap.setMyLocationEnabled(true);

                loginToFirebase();
                getPekerjaan(gMap);
            }
        }catch (NullPointerException e){

        }
    }

    @Override
    public void onStop(){
        if(mChildEventListener != null) {
            mProfileRef.removeEventListener(mChildEventListener);
        }
        super.onStop();
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        addMarkersToMap();
                    }catch (IllegalStateException | NullPointerException e){
                        Log.d("Main Activity", "Error Fragment");
                    }
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void addMarkersToMap(){
        mProfileRef = FirebaseDatabase.getInstance().getReference("location_petugas/");
        mChildEventListener = mProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
        String latitude = locationModel.getLatitude();
        String longitude = locationModel.getLongitude();
        LatLng location = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, gMap.addMarker(new MarkerOptions().position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }

    }

    private void getPekerjaan(final GoogleMap googleMap) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getPekerjaan(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("hasil");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                PekerjaanModel pekerjaanModel = new PekerjaanModel();
                                pekerjaanModel.setId(obj.getString("id"));
                                pekerjaanModel.setIdpetugas(obj.getString("id_petugas"));
                                pekerjaanModel.setIdtps(obj.getString("id_tps"));
                                pekerjaanModel.setNamatps(obj.getString("nama_tps"));
                                pekerjaanModel.setLattps(Double.valueOf(obj.getString("lat_tps")));
                                pekerjaanModel.setLongtps(Double.valueOf(obj.getString("long_tps")));
                                pekerjaanModel.setPhototps(obj.getString("photo_tps"));
                                pekerjaanModel.setDeskripsitps(obj.getString("deskripsi_tps"));
                                pekerjaanModel.setStatus(obj.getString("status_pekerjaan"));

                                Toast.makeText(getActivity().getApplicationContext(), obj.getString("status_pekerjaan"), Toast.LENGTH_SHORT).show();

                                addMarker(googleMap, Double.valueOf(obj.getString("lat_tps")), Double.valueOf(obj.getString("long_tps")), obj.getString("status_pekerjaan"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
                Log.e(TAG, "Get Data Errorrrr: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_petugas", sessionAdapter.getId());

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }

    private void addMarker(GoogleMap googleMap, double latitude, double longitude, String nama_tps) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(nama_tps)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

}