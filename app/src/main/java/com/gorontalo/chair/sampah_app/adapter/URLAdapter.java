package com.gorontalo.chair.sampah_app.adapter;

public class URLAdapter {
    /*private String URL = "http://192.168.0.104/sim-sampah/webservices/";
    private String URL_PHOTO = "http://192.168.0.104/admin-control/assets/images/photo/";*/

    private String URL = "https://sim-sampah.web.id/webservices/";
    private String URL_PHOTO = "https://sim-sampah.web.id/assets/images/photo/";

    public String getLogin(){
        return URL = URL+"ws-login-petugas.php";
    }

    public String getPekerjaan(){
        return URL = URL+"ws-get-pekerjaan.php";
    }

    public String getAllPekerjaan(){
        return URL = URL+"ws-get-allpekerjaan.php";
    }

    public String getRute(){
        return URL = URL+"ws-get-rute.php";
    }

    public String getPengumuman(){
        return URL = URL+"ws-get-pengumuman.php";
    }

    public String getLaporanPetugas(){
        return URL = URL+"ws-get-laporan-petugas.php";
    }

    public String getLastPengumuman(){
        return URL = URL+"ws-get-last-pengumuman.php";
    }

    public String getJumlahLaporan(){
        return URL = URL+"ws-get-jumlah-laporan.php";
    }

    public String getTPSByID(){
        return URL = URL+"ws-get-tps-id.php";
    }

    public String getTPAByID(){
        return URL = URL+"ws-get-tpa-id.php";
    }

    public String getPetugasByID(){
        return URL = URL+"ws-get-petugas-id.php";
    }

    public String updateLokasiPetugas(){
        return URL = URL+"ws-update-lokasi-petugas.php";
    }

    public String simpanPekerjaan(){
        return URL = URL+"ws-simpan-logpekerjaan.php";
    }

    public String simpanLaporan(){
        return URL = URL+"ws-simpan-laporan.php";
    }

    public String simpanLaporanTps(){
        return URL = URL+"ws-simpan-laporan-tps.php";
    }

    public String buangSampah(){
        return URL = URL+"ws-buang-sampah.php";
    }

    public String getPhotoPetugas(String photo){
        return URL = URL_PHOTO+"/petugas/"+photo;
    }

    public String getPhotoLaporan(String photo){
        return URL = URL_PHOTO+"/laporan/"+photo;
    }

    public String getPhotoTps(String photo){
        return URL = URL_PHOTO+"/tps/"+photo;
    }
}
