package model;

import java.time.LocalDate;

public abstract class Kendaraan {
    private String platNomor;
    private double hargaSewaPerHari;
    private String tipeKendaraan;
    private StatusKendaraan statusKendaraan;
    private int totalHariDisewa;
    private LocalDate tanggalServisTerakhir;

    public Kendaraan(String platNomor, double hargaSewaPerHari, String tipeKendaraan) {
        this.platNomor = platNomor;
        this.hargaSewaPerHari = hargaSewaPerHari;
        this.tipeKendaraan = tipeKendaraan;
        this.statusKendaraan = StatusKendaraan.TERSEDIA;
        this.totalHariDisewa = 0;
        this.tanggalServisTerakhir = LocalDate.now();
    }

    public abstract String dapatkanAtributKhusus();

    public String getPlatNomor() {
        return platNomor;
    }

    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }

    public double getHargaSewaPerHari() {
        return hargaSewaPerHari;
    }

    public void setHargaSewaPerHari(double hargaSewaPerHari) {
        this.hargaSewaPerHari = hargaSewaPerHari;
    }

    public String getTipeKendaraan() {
        return tipeKendaraan;
    }

    public StatusKendaraan getStatusKendaraan() {
        return statusKendaraan;
    }

    public void setStatusKendaraan(StatusKendaraan statusKendaraan) {
        this.statusKendaraan = statusKendaraan;
    }

    public int getTotalHariDisewa() {
        return totalHariDisewa;
    }

    public void setTotalHariDisewa(int totalHariDisewa) {
        this.totalHariDisewa = totalHariDisewa;
    }

    public LocalDate getTanggalServisTerakhir() {
        return tanggalServisTerakhir;
    }

    public void setTanggalServisTerakhir(LocalDate tanggalServisTerakhir) {
        this.tanggalServisTerakhir = tanggalServisTerakhir;
    }

    // Cek apakah kendaraan perlu masuk status DALAM_PERAWATAN
    // Hanya ubah status jika saat ini TERSEDIA (jangan ganggu yang SEDANG_DISEWA)
    public void perbaruiStatusPerawatan() {
        if (this.statusKendaraan != StatusKendaraan.TERSEDIA) {
            return;
        }

        LocalDate waktuSekarang = LocalDate.now();
        LocalDate batasWaktuServis = this.tanggalServisTerakhir.plusMonths(6);

        if (this.totalHariDisewa >= 180
                || waktuSekarang.isAfter(batasWaktuServis)
                || waktuSekarang.isEqual(batasWaktuServis)) {
            this.statusKendaraan = StatusKendaraan.DALAM_PERAWATAN;
        }
    }

    // Tandai kendaraan selesai diservis: reset hari sewa, update tanggal servis, balik ke TERSEDIA
    public void selesaiPerawatan() {
        this.totalHariDisewa = 0;
        this.tanggalServisTerakhir = LocalDate.now();
        this.statusKendaraan = StatusKendaraan.TERSEDIA;
    }
}