package com.example.sem_i;

public class DataTrainProv {
    int id_trainProv, id_perusahaan, id_jabatan;
    String nama_tp, bidang, nama_pj, alamat, email, website, no_tlp_train;

    public DataTrainProv(int id_trainProv, int id_perusahaan, int id_jabatan, String nama_tp,
                         String bidang, String nama_pj, String alamat, String email, String website,
                         String no_tlp_train){
        this.id_jabatan = id_jabatan;
        this.id_trainProv = id_trainProv;
        this.id_perusahaan = id_perusahaan;
        this.bidang = bidang;
        this.nama_tp = nama_tp;
        this.nama_pj = nama_pj;
        this.alamat = alamat;
        this.email = email;
        this.website = website;
        this.no_tlp_train = no_tlp_train;
    }

    public DataTrainProv(int id_trainprov) {
    }

    public int getId_trainProv() {
        return id_trainProv;
    }

    public void setId_trainProv(int id_trainProv) {
        this.id_trainProv = id_trainProv;
    }

    public int getId_perusahaan() {
        return id_perusahaan;
    }

    public void setId_perusahaan(int id_perusahaan) {
        this.id_perusahaan = id_perusahaan;
    }

    public int getId_jabatan() {
        return id_jabatan;
    }

    public void setId_jabatan(int id_jabatan) {
        this.id_jabatan = id_jabatan;
    }

    public String getNama_tp() {
        return nama_tp;
    }

    public void setNama_tp(String nama_tp) {
        this.nama_tp = nama_tp;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getNama_pj() {
        return nama_pj;
    }

    public void setNama_pj(String nama_pj) {
        this.nama_pj = nama_pj;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNo_tlp_train() {
        return no_tlp_train;
    }

    public void setNo_tlp_train(String no_tlp_train) {
        this.no_tlp_train = no_tlp_train;
    }

}
