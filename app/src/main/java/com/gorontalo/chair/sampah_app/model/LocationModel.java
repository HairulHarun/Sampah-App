package com.gorontalo.chair.sampah_app.model;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private String id, sopir, kondektur1, kondektur2, kenderaan, latitude, longitude, tanki, photo;

    public LocationModel() {
    }

    public LocationModel(String id, String sopir, String kondektur1, String kondektur2, String kenderaan, String latitude, String longitude, String tanki, String photo) {
        this.id = id;
        this.sopir = sopir;
        this.kondektur1 = kondektur1;
        this.kondektur2 = kondektur2;
        this.kenderaan = kenderaan;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tanki = tanki;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSopir() {
        return sopir;
    }

    public void setSopir(String sopir) {
        this.sopir = sopir;
    }

    public String getKondektur1() {
        return kondektur1;
    }

    public void setKondektur1(String kondektur1) {
        this.kondektur1 = kondektur1;
    }

    public String getKondektur2() {
        return kondektur2;
    }

    public void setKondektur2(String kondektur2) {
        this.kondektur2 = kondektur2;
    }

    public String getKenderaan() {
        return kenderaan;
    }

    public void setKenderaan(String kenderaan) {
        this.kenderaan = kenderaan;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTanki() {
        return tanki;
    }

    public void setTanki(String tanki) {
        this.tanki = tanki;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
