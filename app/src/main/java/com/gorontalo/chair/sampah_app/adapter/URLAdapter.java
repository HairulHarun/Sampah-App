package com.gorontalo.chair.sampah_app.adapter;

public class URLAdapter {
    private String URL = "http://192.168.0.104/sim-sampah/webservices/";
    private String URL_PHOTO = "http://192.168.0.104/admin-control/assets/images/photo/";

    public String getLogin(){
        return URL = URL+"ws-login-petugas.php";
    }

    public String getPekerjaan(){
        return URL = URL+"ws-get-pekerjaan.php";
    }

    public String updateLokasiPetugas(){
        return URL = URL+"ws-update-lokasi-petugas.php";
    }
}
