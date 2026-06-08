package service;

import model.Denda;
import model.DendaMobil;
import model.DendaMotor;
import model.Kendaraan;
import model.Mobil;
import model.Motor;
import model.Pelanggan;
import model.Transaksi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Service untuk mengelola seluruh proses transaksi
public class TransaksiService {

    // Menyimpan seluruh transaksi
    private List<Transaksi> daftarTransaksi;

    // Nomor urut transaksi
    private int nomorTransaksi;

    public TransaksiService() {
        daftarTransaksi = new ArrayList<>();
        nomorTransaksi = 1;
    }

    // Membuat ID transaksi otomatis
    public String buatIdTransaksi() {

        String idTransaksi = String.format("TRX%03d", nomorTransaksi);

        nomorTransaksi++;

        return idTransaksi;
    }

    // Memproses peminjaman kendaraan
    public Transaksi prosesPeminjaman(
            Pelanggan pelanggan,
            Kendaraan kendaraan,
            LocalDate tanggalPeminjaman,
            LocalDate tanggalPengembalianRencana) {

        // Validasi pelanggan
        if (pelanggan == null) {
            System.out.println("Pelanggan tidak ditemukan.");
            return null;
        }

        // Validasi kendaraan
        if (kendaraan == null) {
            System.out.println("Kendaraan tidak ditemukan.");
            return null;
        }

        // Validasi status kendaraan
        if (!kendaraan.getStatus().equalsIgnoreCase("TERSEDIA")) {

            System.out.println("Kendaraan sedang tidak tersedia.");

            return null;
        }

        // Membuat objek transaksi
        Transaksi transaksiBaru = new Transaksi(
                buatIdTransaksi(),
                pelanggan,
                kendaraan,
                tanggalPeminjaman,
                tanggalPengembalianRencana,
                "BERJALAN");

        // Mengubah status kendaraan
        kendaraan.setStatus("DISEWA");

        // Menambahkan transaksi ke list
        daftarTransaksi.add(transaksiBaru);

        System.out.println("Peminjaman berhasil.");

        return transaksiBaru;
    }

    // Mencari transaksi berdasarkan ID
    public Transaksi cariTransaksi(String idTransaksi) {

        for (Transaksi transaksi : daftarTransaksi) {

            if (transaksi.getIdTransaksi().equalsIgnoreCase(idTransaksi)) {

                return transaksi;

            }
        }

        return null;
    }

    // Menghitung biaya sewa
    public double hitungBiayaSewa(Transaksi transaksi) {

        long jumlahHariSewa = ChronoUnit.DAYS.between(
                transaksi.getTanggalPeminjaman(),
                transaksi.getTanggalPengembalianRencana());

        if (jumlahHariSewa <= 0) {
            jumlahHariSewa = 1;
        }

        return jumlahHariSewa * transaksi.getKendaraan().getHargaSewaPerHari();
    }

    // Menghitung jumlah hari terlambat
    public int hitungHariTerlambat(Transaksi transaksi) {

        long jumlahHariTerlambat = ChronoUnit.DAYS.between(
                transaksi.getTanggalPengembalianRencana(),
                transaksi.getTanggalPengembalianAktual());

        if (jumlahHariTerlambat < 0) {
            return 0;
        }

        return (int) jumlahHariTerlambat;
    }

    // Menghitung denda menggunakan polymorphism
    public double hitungDenda(Transaksi transaksi) {

        int jumlahHariTerlambat = hitungHariTerlambat(transaksi);

        Denda denda;

        if (transaksi.getKendaraan() instanceof Mobil) {

            denda = new DendaMobil();

        } else if (transaksi.getKendaraan() instanceof Motor) {

            denda = new DendaMotor();

        } else {

            return 0;
        }

        return denda.hitungDenda(jumlahHariTerlambat);
    }

    // Memproses pengembalian kendaraan
    public void prosesPengembalian(
            String idTransaksi,
            LocalDate tanggalPengembalianAktual) {

        Transaksi transaksi = cariTransaksi(idTransaksi);

        if (transaksi == null) {

            System.out.println("Transaksi tidak ditemukan.");

            return;
        }

        transaksi.setTanggalPengembalianAktual(
                tanggalPengembalianAktual);

        transaksi.setTotalBiayaSewa(
                hitungBiayaSewa(transaksi));

        transaksi.setTotalDenda(
                hitungDenda(transaksi));

        transaksi.setStatusTransaksi(
                "SELESAI");

        transaksi.getKendaraan().setStatus(
                "TERSEDIA");

        System.out.println("Pengembalian berhasil.");
    }

    // Menampilkan seluruh transaksi
    public void tampilkanSeluruhTransaksi() {

        if (daftarTransaksi.isEmpty()) {

            System.out.println("Belum ada transaksi.");

            return;
        }

        for (Transaksi transaksi : daftarTransaksi) {

            System.out.println("----------------------------");

            System.out.println(transaksi);

        }
    }

    // Menampilkan struk transaksi
    public void cetakStruk(Transaksi transaksi) {

        System.out.println();
        System.out.println("========== STRUK RENTAL ==========");
        System.out.println("ID Transaksi      : " + transaksi.getIdTransaksi());
        System.out.println("Nama Pelanggan    : " + transaksi.getPelanggan().getNama());
        System.out.println("Kendaraan         : " + transaksi.getKendaraan().getPlatNomor());
        System.out.println("Tanggal Pinjam    : " + transaksi.getTanggalPeminjaman());
        System.out.println("Rencana Kembali   : " + transaksi.getTanggalPengembalianRencana());
        System.out.println("Tanggal Kembali   : " + transaksi.getTanggalPengembalianAktual());
        System.out.println("Biaya Sewa        : Rp " + transaksi.getTotalBiayaSewa());
        System.out.println("Denda             : Rp " + transaksi.getTotalDenda());
        System.out.println("Total Bayar       : Rp "
                + (transaksi.getTotalBiayaSewa()
                + transaksi.getTotalDenda()));
        System.out.println("Status            : " + transaksi.getStatusTransaksi());
        System.out.println("==================================");
    }

    public List<Transaksi> getDaftarTransaksi() {
        return daftarTransaksi;
    }
}