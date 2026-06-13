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

// Service untuk mengelola seluruh proses transaksi
public class TransaksiService {

    private List<Transaksi> daftarTransaksi;
    private int nomorTransaksi;
    private JsonTransaksi jsonTransaksi;

    public TransaksiService() {
        jsonTransaksi = new JsonTransaksi();
        // Muat data transaksi yang sudah ada dari file (persistence)
        daftarTransaksi = jsonTransaksi.muatDataTransaksi();
        // Tentukan nomor transaksi selanjutnya berdasarkan data yang sudah ada
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

        // Simpan ke file setiap ada transaksi baru
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

    // Menghitung jumlah hari terlambat dari selisih tanggal rencana dan aktual
    public int hitungHariTerlambat(Transaksi transaksi) {
        long jumlahHariTerlambat = ChronoUnit.DAYS.between(
                transaksi.getTanggalPengembalianRencana(),
                transaksi.getTanggalPengembalianAktual());

        if (jumlahHariTerlambat < 0) {
            return 0;
        }

        return (int) jumlahHariTerlambat;
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
    // hariTerlambatInput: input manual dari staff (0 jika tepat waktu), sesuai contoh UI di spek
    public void prosesPengembalian(String idTransaksi, int hariTerlambatInput) {
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

        Kendaraan kendaraan = transaksi.getKendaraan();

        // Update total hari disewa untuk keperluan modul maintenance (kelompok 2)
        long durasiSewaAktual = ChronoUnit.DAYS.between(
                transaksi.getTanggalPeminjaman(),
                tanggalPengembalianAktual);
        if (durasiSewaAktual < 1) {
            durasiSewaAktual = 1;
        }
        kendaraan.setTotalHariDisewa(kendaraan.getTotalHariDisewa() + (int) durasiSewaAktual);

        // Kembalikan status kendaraan, lalu cek apakah perlu masuk maintenance
        kendaraan.setStatusKendaraan(StatusKendaraan.TERSEDIA);
        kendaraan.perbaruiStatusPerawatan();

        // Simpan perubahan transaksi ke file
        jsonTransaksi.simpanDataTransaksi(daftarTransaksi);

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
                + (transaksi.getTotalBiayaSewa() + transaksi.getTotalDenda()));
        System.out.println("Status            : " + transaksi.getStatusTransaksi());
        System.out.println("==================================");
    }

    public List<Transaksi> getDaftarTransaksi() {
        return daftarTransaksi;
    }
}