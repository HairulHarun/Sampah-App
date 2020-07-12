package com.gorontalo.chair.sampah_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gorontalo.chair.sampah_app.adapter.SessionAdapter;

public class BerandaActivity extends AppCompatActivity {
    private SessionAdapter sessionAdapter;
    private Button btnMasyarakat, btnPetugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        btnMasyarakat = (Button) findViewById(R.id.btnMasyarakat);
        btnPetugas = (Button) findViewById(R.id.btnPetugas);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.checkLogin();

        btnMasyarakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BerandaActivity.this, UserActivity.class));
            }
        });

        btnPetugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BerandaActivity.this, LoginActivity.class));
            }
        });
    }

    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
