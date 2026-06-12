package model;

import java.time.LocalDate;

// Class untuk menyimpan seluruh informasi transaksi
public class Transaksi {

    // ID unik transaksi
    private String idTransaksi;

    // Data pelanggan yang menyewa
    private Pelanggan pelanggan;

    // Kendaraan yang disewa
    private Kendaraan kendaraan;

    // Tanggal mulai sewa
    private LocalDate tanggalPeminjaman;

    // Tanggal rencana pengembalian
    private LocalDate tanggalPengembalianRencana;

    // Tanggal kendaraan benar-benar dikembalikan
    private LocalDate tanggalPengembalianAktual;

    // Status transaksi
    // BERJALAN atau SELESAI
    private String statusTransaksi;

    // Total biaya sewa
    private double totalBiayaSewa;

    // Total denda keterlambatan
    private double totalDenda;

    // Constructor kosong
    public Transaksi() {

    }

    // Constructor lengkap
    public Transaksi(
            String idTransaksi,
            Pelanggan pelanggan,
            Kendaraan kendaraan,
            LocalDate tanggalPeminjaman,
            LocalDate tanggalPengembalianRencana,
            String statusTransaksi) {

        this.idTransaksi = idTransaksi;
        this.pelanggan = pelanggan;
        this.kendaraan = kendaraan;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.tanggalPengembalianRencana = tanggalPengembalianRencana;
        this.statusTransaksi = statusTransaksi;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    public void setKendaraan(Kendaraan kendaraan) {
        this.kendaraan = kendaraan;
    }

    public LocalDate getTanggalPeminjaman() {
        return tanggalPeminjaman;
    }

    public void setTanggalPeminjaman(LocalDate tanggalPeminjaman) {
        this.tanggalPeminjaman = tanggalPeminjaman;
    }

    public LocalDate getTanggalPengembalianRencana() {
        return tanggalPengembalianRencana;
    }

    public void setTanggalPengembalianRencana(LocalDate tanggalPengembalianRencana) {
        this.tanggalPengembalianRencana = tanggalPengembalianRencana;
    }

    public LocalDate getTanggalPengembalianAktual() {
        return tanggalPengembalianAktual;
    }

    public void setTanggalPengembalianAktual(LocalDate tanggalPengembalianAktual) {
        this.tanggalPengembalianAktual = tanggalPengembalianAktual;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public double getTotalBiayaSewa() {
        return totalBiayaSewa;
    }

    public void setTotalBiayaSewa(double totalBiayaSewa) {
        this.totalBiayaSewa = totalBiayaSewa;
    }

    public double getTotalDenda() {
        return totalDenda;
    }

    public void setTotalDenda(double totalDenda) {
        this.totalDenda = totalDenda;
    }

    @Override
    public String toString() {
        return "ID Transaksi      : " + idTransaksi +
                "\nPelanggan        : " + pelanggan.getNama() +
                "\nKendaraan        : " + kendaraan.getPlatNomor() +
                "\nTanggal Pinjam   : " + tanggalPeminjaman +
                "\nStatus           : " + statusTransaksi;
    }
}