package com.gorontalo.chair.sampah_app.ui.home;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private SessionAdapter sessionAdapter;

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;
    private MarkerOptions markerOptions = new MarkerOptions();

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
                getRute(gMap);
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
//                        addMarkersToMap();
                        subscribeToUpdates();
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

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location_petugas/");
        ref.child(sessionAdapter.getId().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    LocationModel locations = dataSnapshot.getValue(LocationModel.class);

                    String key = dataSnapshot.getKey();
                    double lat = Double.parseDouble(locations.getLatitude());
                    double lng = Double.parseDouble(locations.getLongitude());

                    LatLng location = new LatLng(lat, lng);
                    if (!mMarkers.containsKey(key)) {
                        int tanki = Integer.parseInt(locations.getTanki());
                        if (tanki >= 80 ){
                            mMarkers.put(key, gMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.car))));
                        }else{
                            mMarkers.put(key, gMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.car2))));
                        }
                    } else {
                        mMarkers.get(key).setPosition(location);
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : mMarkers.values()) {
                        builder.include(marker.getPosition());
                    }
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));

                }catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tmz", "Failed to read value.", error.toException());
            }
        });
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
                        String lat_tpa = jObj.getString("lat_tpa");
                        String long_tpa = jObj.getString("long_tpa");
                        String nama_tpa = jObj.getString("nama_tpa");

                        addMarkerTPA(googleMap, new LatLng(Double.valueOf(lat_tpa), Double.valueOf(long_tpa)), nama_tpa);

                        JSONArray jsonArray = jObj.getJSONArray("hasil");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id_pekerjaan = jsonObject.getString("id");
                            String id_tps = jsonObject.getString("id_tps");
                            String lat_tps = jsonObject.getString("lat_tps");
                            String long_tps = jsonObject.getString("long_tps");
                            String nama_tps = jsonObject.getString("nama_tps");
                            String photo_tps = jsonObject.getString("photo_tps");
                            String deskripsi_tps = jsonObject.getString("deskripsi_tps");
                            String status_pekerjaan = jsonObject.getString("status_pekerjaan");

                            addMarker(googleMap, new LatLng(Double.valueOf(jsonObject.getString("lat_tps")), Double.valueOf(jsonObject.getString("long_tps"))), id_tps, status_pekerjaan);
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
                Log.e(TAG, "Get Data Error Pekerjaan: " + error.getMessage());
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

    private void addMarker(GoogleMap googleMap, LatLng latlng, final String id, String status) {
        markerOptions.position(latlng);
        markerOptions.title(id);

        if (status.equals("1")){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tps));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tpsfull));
        }

        googleMap.addMarker(markerOptions);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                TpsFragment bottomSheetFragment = new TpsFragment(marker.getTitle().toString());
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }

    private void addMarkerTPA(GoogleMap googleMap, LatLng latlng, final String nama) {
        markerOptions.position(latlng);
        markerOptions.title(nama);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        googleMap.addMarker(markerOptions);
    }

    private void getRute(final GoogleMap googleMap) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getRute(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("hasil");
                        List<LatLng> position = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                String id_rute = obj.getString("id");
                                String id_petugas = obj.getString("id_petugas");
                                double lat_rute = Double.valueOf(obj.getString("lat_rute"));
                                double long_rute = Double.valueOf(obj.getString("long_rute"));

                                position.add(new LatLng(lat_rute, long_rute));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        drawPolyLineOnMap(googleMap, position);
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
                params.put("id_petugas", sessionAdapter.getId());

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }

    public void drawPolyLineOnMap(GoogleMap googleMap, List<LatLng> list) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(color);
        polyOptions.width(8);
        polyOptions.addAll(list);

//        googleMap.clear();
        googleMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        googleMap.animateCamera(cu);
    }
}