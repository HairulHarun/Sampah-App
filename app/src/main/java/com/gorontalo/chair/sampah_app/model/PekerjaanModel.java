package com.gorontalo.chair.sampah_app.model;

import java.io.Serializable;

public class PekerjaanModel implements Serializable {
    String id, idpetugas, idtps, namatps, phototps, deskripsitps, status;
    double lattps, longtps;

    public PekerjaanModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdpetugas() {
        return idpetugas;
    }

    public void setIdpetugas(String idpetugas) {
        this.idpetugas = idpetugas;
    }

    public String getIdtps() {
        return idtps;
    }

    public void setIdtps(String idtps) {
        this.idtps = idtps;
    }

    public String getNamatps() {
        return namatps;
    }

    public void setNamatps(String namatps) {
        this.namatps = namatps;
    }

    public double getLattps() {
        return lattps;
    }

    public void setLattps(double lattps) {
        this.lattps = lattps;
    }

    public double getLongtps() {
        return longtps;
    }

    public void setLongtps(double longtps) {
        this.longtps = longtps;
    }

    public String getPhototps() {
        return phototps;
    }

    public void setPhototps(String phototps) {
        this.phototps = phototps;
    }

    public String getDeskripsitps() {
        return deskripsitps;
    }

    public void setDeskripsitps(String deskripsitps) {
        this.deskripsitps = deskripsitps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
