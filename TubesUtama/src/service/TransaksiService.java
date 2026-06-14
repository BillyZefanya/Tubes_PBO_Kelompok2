package service;

import model.Denda;
import model.DendaMobil;
import model.DendaMotor;
import model.Kendaraan;
import model.Mobil;
import model.Motor;
import model.Pelanggan;
import model.Transaksi;
import model.StatusKendaraan;
import persistence.JsonTransaksi;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

// Service untuk mengelola seluruh proses transaksi
public class TransaksiService {

    private List<Transaksi> daftarTransaksi;
    private int nomorTransaksi;
    private JsonTransaksi jsonTransaksi;

    public TransaksiService() {
        jsonTransaksi = new JsonTransaksi();
        daftarTransaksi = jsonTransaksi.muatDataTransaksi();
        nomorTransaksi = hitungNomorTransaksiSelanjutnya();
    }

    // Cari nomor urut tertinggi dari ID transaksi yang sudah ada, lalu +1
    private int hitungNomorTransaksiSelanjutnya() {
        int nomorTertinggi = 0;
        for (Transaksi transaksi : daftarTransaksi) {
            String id = transaksi.getIdTransaksi();
            try {
                String bagianNomor = id.substring(id.lastIndexOf("-") + 1);
                int nomor = Integer.parseInt(bagianNomor);
                if (nomor > nomorTertinggi) {
                    nomorTertinggi = nomor;
                }
            } catch (Exception e) {
                // Kalau format ID tidak sesuai, lewati saja
            }
        }
        return nomorTertinggi + 1;
    }

    // Membuat ID transaksi otomatis, format TRX-001
    public String buatIdTransaksi() {
        String idTransaksi = String.format("TRX-%03d", nomorTransaksi);
        nomorTransaksi++;
        return idTransaksi;
    }

    // Memproses peminjaman kendaraan
    public Transaksi prosesPeminjaman(
            Pelanggan pelanggan,
            Kendaraan kendaraan,
            LocalDate tanggalPeminjaman,
            LocalDate tanggalPengembalianRencana) {

        if (pelanggan == null) {
            System.out.println("Pelanggan tidak ditemukan.");
            return null;
        }

        if (kendaraan == null) {
            System.out.println("Kendaraan tidak ditemukan.");
            return null;
        }

        if (kendaraan.getStatusKendaraan() != StatusKendaraan.TERSEDIA) {
            System.out.println("Kendaraan sedang tidak tersedia.");
            return null;
        }

        Transaksi transaksiBaru = new Transaksi(
                buatIdTransaksi(),
                pelanggan,
                kendaraan,
                tanggalPeminjaman,
                tanggalPengembalianRencana,
                "BERJALAN");

        kendaraan.setStatusKendaraan(StatusKendaraan.SEDANG_DISEWA);
        daftarTransaksi.add(transaksiBaru);

        jsonTransaksi.simpanDataTransaksi(daftarTransaksi);

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

    // Menghitung biaya sewa berdasarkan rencana durasi
    public double hitungBiayaSewa(Transaksi transaksi) {
        long jumlahHariSewa = ChronoUnit.DAYS.between(
                transaksi.getTanggalPeminjaman(),
                transaksi.getTanggalPengembalianRencana());

        if (jumlahHariSewa <= 0) {
            jumlahHariSewa = 1;
        }

        return jumlahHariSewa * transaksi.getKendaraan().getHargaSewaPerHari();
    }

    // Menghitung denda menggunakan polymorphism (DendaMobil/DendaMotor)
    public double hitungDenda(Transaksi transaksi, int jumlahHariTerlambat) {
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
// hariTerlambatInput: input manual dari staff (0 jika tepat waktu)
// kendaraanAktual: object Kendaraan yang dikelola KendaraanManager (bukan copy dari transaksi.json),
// supaya status & totalHariDisewa tersimpan konsisten ke kendaraan.json
public void prosesPengembalian(String idTransaksi, int hariTerlambatInput, Kendaraan kendaraanAktual) {
    Transaksi transaksi = cariTransaksi(idTransaksi);

    if (transaksi == null) {
        System.out.println("Transaksi tidak ditemukan.");
        return;
    }

    if (transaksi.getStatusTransaksi().equalsIgnoreCase("SELESAI")) {
        System.out.println("Transaksi ini sudah selesai sebelumnya.");
        return;
    }

    LocalDate tanggalPengembalianAktual = LocalDate.now();
    transaksi.setTanggalPengembalianAktual(tanggalPengembalianAktual);

    double biayaSewa = hitungBiayaSewa(transaksi);
    double denda = hitungDenda(transaksi, hariTerlambatInput);

    transaksi.setTotalBiayaSewa(biayaSewa);
    transaksi.setTotalDenda(denda);
    transaksi.setStatusTransaksi("SELESAI");

    // Update status & totalHariDisewa pada object Kendaraan yang dikelola KendaraanManager
    // (bukan pada copy yang tersimpan di dalam Transaksi), agar kendaraan.json tetap konsisten
    long durasiSewaAktual = ChronoUnit.DAYS.between(
            transaksi.getTanggalPeminjaman(),
            tanggalPengembalianAktual);
    if (durasiSewaAktual < 1) {
        durasiSewaAktual = 1;
    }
    kendaraanAktual.setTotalHariDisewa(kendaraanAktual.getTotalHariDisewa() + (int) durasiSewaAktual);

    kendaraanAktual.setStatusKendaraan(StatusKendaraan.TERSEDIA);
    kendaraanAktual.perbaruiStatusPerawatan();

    jsonTransaksi.simpanDataTransaksi(daftarTransaksi);

    System.out.println("Pengembalian berhasil.");
}

    // Format angka ke Rupiah dengan pemisah ribuan titik, contoh: Rp 900.000
    private String formatRupiah(double nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,.0f", nilai);
    }

    // Menampilkan struk sementara setelah peminjaman (Menu 4 Staff)
    public void cetakStrukPeminjaman(Transaksi transaksi, int durasiHariSewa) {
        double estimasiBiaya = durasiHariSewa * transaksi.getKendaraan().getHargaSewaPerHari();

        System.out.println();
        System.out.println("--- STRUK PEMINJAMAN SEMENTARA ---");
        System.out.println("ID Transaksi    : " + transaksi.getIdTransaksi());
        System.out.println("Nama Pelanggan  : " + transaksi.getPelanggan().getNama());
        System.out.println("Kendaraan       : " + transaksi.getKendaraan().getTipeKendaraan()
                + " (" + transaksi.getKendaraan().getPlatNomor() + ")");
        System.out.println("Durasi Sewa     : " + durasiHariSewa + " Hari");
        System.out.println("Estimasi Biaya  : " + formatRupiah(estimasiBiaya));
        System.out.println("----------------------------------");
    }

    // Menampilkan struk tagihan akhir setelah pengembalian (Menu 5 Staff)
    public void cetakStrukPengembalian(Transaksi transaksi, int hariTerlambat) {
        long durasiRencana = ChronoUnit.DAYS.between(
                transaksi.getTanggalPeminjaman(),
                transaksi.getTanggalPengembalianRencana());
        if (durasiRencana <= 0) {
            durasiRencana = 1;
        }

        String jenisKendaraan = transaksi.getKendaraan().getTipeKendaraan();

        System.out.println();
        System.out.println("--- STRUK TAGIHAN AKHIR ---");
        System.out.println("ID Transaksi    : " + transaksi.getIdTransaksi());
        System.out.println("Pelanggan       : " + transaksi.getPelanggan().getNama());
        System.out.println("Kendaraan       : " + jenisKendaraan + " (" + transaksi.getKendaraan().getPlatNomor() + ")");
        System.out.println("Biaya Dasar     : " + formatRupiah(transaksi.getTotalBiayaSewa()) + " (" + durasiRencana + " Hari)");

        if (hariTerlambat > 0) {
            double dendaPerHari = transaksi.getTotalDenda() / hariTerlambat;
            System.out.println("Denda Telat     : " + formatRupiah(transaksi.getTotalDenda())
                    + " (" + hariTerlambat + " Hari x " + formatRupiah(dendaPerHari) + " khusus " + jenisKendaraan + ")");
        } else {
            System.out.println("Denda Telat     : " + formatRupiah(0) + " (Tepat waktu)");
        }

        System.out.println("---------------------------------- +");
        System.out.println("TOTAL BAYAR     : " + formatRupiah(transaksi.getTotalBiayaSewa() + transaksi.getTotalDenda()));
        System.out.println("----------------------------------");
    }

    public List<Transaksi> getDaftarTransaksi() {
        return daftarTransaksi;
    }
}