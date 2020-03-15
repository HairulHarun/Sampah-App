package com.gorontalo.chair.sampah_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gorontalo.chair.sampah_app.adapter.HttpsTrustManagerAdapter;
import com.gorontalo.chair.sampah_app.adapter.KoneksiAdapter;
import com.gorontalo.chair.sampah_app.adapter.SessionAdapter;
import com.gorontalo.chair.sampah_app.adapter.URLAdapter;
import com.gorontalo.chair.sampah_app.adapter.VolleyAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaporActivity extends AppCompatActivity {
    private static final String TAG = LaporActivity.class.getSimpleName();
    private EditText txtIsi;
    private ImageView imgPhoto;
    private TextView txtNamaPetugas;
    private Button btnPilihPhoto;
    private FloatingActionButton fab;
    private Intent intent;

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    private KoneksiAdapter koneksiAdapter;
    private Boolean isInternetPresent = false;

    private String ID, KENDERAAN, SOPIR, PHOTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        intent = getIntent();
        ID = intent.getStringExtra("id_petugas");
        KENDERAAN = intent.getStringExtra("kenderaan");
        SOPIR = intent.getStringExtra("sopir");
        PHOTO = intent.getStringExtra("photo");

        imgPhoto = (ImageView) findViewById(R.id.imgPilihFoto);
        txtIsi = (EditText) findViewById(R.id.editTextIsiLaporan);
        btnPilihPhoto = (Button) findViewById(R.id.btnPilihPhoto);
        txtNamaPetugas = (TextView) findViewById(R.id.txtLaporanNamaPetugas);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        txtNamaPetugas.setText(KENDERAAN+" "+SOPIR);

        btnPilihPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(LaporActivity.this)
                        .withPermissions(
                                android.Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                        simpanLaporan(ID, txtIsi.getText().toString(), getStringImage(bitmap));
                                    }else{
                                        SnackbarManager.show(
                                                com.nispok.snackbar.Snackbar.with(LaporActivity.this)
                                                        .text("No Connection !")
                                                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                        .actionLabel("Refresh")
                                                        .actionListener(new ActionClickListener() {
                                                            @Override
                                                            public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                                refresh();
                                                            }
                                                        })
                                                , LaporActivity.this);
                                    }

                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void simpanLaporan(final String id_petugas, final String isi, final String photo) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().simpanLaporan(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        startActivity(new Intent(LaporActivity.this, UserActivity.class));
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
                params.put("id", id_petugas);
                params.put("isi", isi);
                params.put("photo", photo);

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "getPekerjaan");
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}
