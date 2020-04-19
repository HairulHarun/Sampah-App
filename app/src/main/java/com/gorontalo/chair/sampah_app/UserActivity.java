package com.gorontalo.chair.sampah_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import com.gorontalo.chair.sampah_app.adapter.HttpsTrustManagerAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.adapter.VolleyAdapter;
import com.gorontalo.chair.sampah_app.ui.home.TpsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = UserActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private SupportMapFragment mapFragment;
    private Marker markerPetugas, markerTps, markerTPA;
    private TextView txtPengumuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtPengumuman = (TextView) findViewById(R.id.txtPengumuman);
        getPengumuman();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_login) {
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
        }else if (id == R.id.action_pengumuman) {
            startActivity(new Intent(UserActivity.this, PengumumanActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setMaxZoomPreference(16);
            LatLng wollongong = new LatLng(0.57395177, 123.07756159);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wollongong, 15));

            loginToFirebase(googleMap);
            getPekerjaan(googleMap);

            String[] dataPetugas = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

            for (int i = 0; i < dataPetugas.length; i++){
                getRute(googleMap, dataPetugas[i]);
            }

        }catch (NullPointerException e){

        }
    }

    public void onBackPressed(){
        moveTaskToBack(true);
    }

    private void getPekerjaan(final GoogleMap googleMap) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getAllPekerjaan(), new Response.Listener<String>() {

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

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }

    private void loginToFirebase(final GoogleMap googleMap) {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        subscribeToUpdates(googleMap);
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

    private void subscribeToUpdates(final GoogleMap googleMap) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location_petugas/");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot, googleMap);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot, googleMap);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot, GoogleMap googleMap) {
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        final int tanki = Integer.valueOf(value.get("tanki").toString());
        final String id = value.get("id").toString();
        final String sopir = value.get("sopir").toString();
        final String kenderaan = value.get("kenderaan").toString();
        final String kondektur1 = value.get("kondektur1").toString();
        final String kondektur2 = value.get("kondektur2").toString();
        final String photo = value.get("photo").toString();
        final String statusmarker = "Petugas"+"/"+id;

        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            if (tanki >= 80){
                mMarkers.put(key, googleMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).title(statusmarker)));
            }else{
                mMarkers.put(key, googleMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).title(statusmarker)));
            }
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker: mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        googleMap.setOnMarkerClickListener(this);
    }

    private void addMarker(GoogleMap googleMap, LatLng latlng, final String id, String status) {
        final String statusmarker = "TPS"+"/"+id;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(statusmarker);

        if (status.equals("1")){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tps));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tpsfull));
        }

        googleMap.addMarker(markerOptions);
        googleMap.setOnMarkerClickListener(this);
    }

    private void addMarkerTPA(GoogleMap googleMap, LatLng latlng, final String nama) {
        final String statusmarker = "TPA"+"/"+nama;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(statusmarker);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        googleMap.addMarker(markerOptions);
        googleMap.setOnMarkerClickListener(this);
    }

    private void getRute(final GoogleMap googleMap, final String id_petugas) {
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
                params.put("id_petugas", id_petugas);
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
    }

    private void getPengumuman() {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLastPengumuman(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        String isi = jObj.getString("hasil");
                        txtPengumuman.setText(isi);
                        txtPengumuman.setSelected(true);
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
                Log.e(TAG, "Get Data Error Pengumuman: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String[] items = marker.getTitle().split("/");
        String status = items[0];
        String id = items[1];

        if (status.equals("Petugas")){
            PetugasFragment bottomSheetFragment = new PetugasFragment(id);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }else if (status.equals("TPS")){
            TpsFragmentUser bottomSheetFragment = new TpsFragmentUser(id);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }else{
            Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
