package com.gorontalo.chair.sampah_app.adapter;

public class URLAdapter {
    /*private String URL = "http://192.168.0.104/sim-sampah/webservices/";
    private String URL_PHOTO = "http://192.168.0.104/admin-control/assets/images/photo/";*/

    private String URL = "https://hairulharun.000webhostapp.com/sampah/webservices/";
    private String URL_PHOTO = "https://hairulharun.000webhostapp.com/sampah/assets/images/photo/";

    public String getLogin(){
        return URL = URL+"ws-login-petugas.php";
    }

    public String getPekerjaan(){
        return URL = URL+"ws-get-pekerjaan.php";
    }

    public String getRute(){
        return URL = URL+"ws-get-rute.php";
    }

    public String getTPSByID(){
        return URL = URL+"ws-get-tps-id.php";
    }

    public String updateLokasiPetugas(){
        return URL = URL+"ws-update-lokasi-petugas.php";
    }

    public String simpanPekerjaan(){
        return URL = URL+"ws-simpan-logpekerjaan.php";
    }

    public String getPhotoPetugas(String photo){
        return URL = URL_PHOTO+"/petugas/"+photo;
    }

    public String getPhotoTps(String photo){
        return URL = URL_PHOTO+"/tps/"+photo;
    }
}
