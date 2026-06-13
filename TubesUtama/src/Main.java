import model.Kendaraan;
import model.Mobil;
import model.Motor;
import model.Pelanggan;
import model.Transaksi;
import model.User;
import repository.PelangganRepository;
import service.KendaraanManager;
import service.LaporanService;
import service.LayananService;
import service.TransaksiService;
import service.UserService;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        UserService userService = new UserService();
        KendaraanManager kendaraanManager = new KendaraanManager();
        LayananService layananService = new LayananService(new PelangganRepository());
        TransaksiService transaksiService = new TransaksiService();
        LaporanService laporanService = new LaporanService();

        User loggedInUser = null;

        System.out.println("=== Selamat Datang di Sistem Rental Kendaraan ===");

        while (true) {

            // Berhenti total kalau gagal login 3x
            if (userService.isTerkunci()) {
                System.out.println("Aplikasi dihentikan karena percobaan login melebihi batas.");
                break;
            }

            // Layar login
            if (loggedInUser == null) {
                System.out.println("\n--- SILAKAN LOGIN ---");
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();

                loggedInUser = userService.login(username, password);

                if (loggedInUser != null) {
                    System.out.println("Login berhasil! Selamat datang, " + loggedInUser.getUsername()
                            + " (" + loggedInUser.getRole() + ")");
                }
                continue;
            }

            // Sudah login, arahkan ke menu sesuai role
            String role = loggedInUser.getRole();

            if (role.equalsIgnoreCase("Admin")) {
                boolean logout = menuAdmin(kendaraanManager);
                if (logout) loggedInUser = null;
            } else if (role.equalsIgnoreCase("Staff")) {
                boolean logout = menuStaff(layananService, kendaraanManager, transaksiService);
                if (logout) loggedInUser = null;
            } else if (role.equalsIgnoreCase("Owner")) {
                boolean logout = menuOwner(laporanService, transaksiService);
                if (logout) loggedInUser = null;
            } else {
                System.out.println("Role tidak dikenali.");
                loggedInUser = null;
            }
        }

        scanner.close();
    }

    // ================= MENU ADMIN =================
    // Return true jika user pilih logout
    private static boolean menuAdmin(KendaraanManager kendaraanManager) {
        System.out.println("\n========================================");
        System.out.println("DASHBOARD - ADMIN");
        System.out.println("========================================");
        System.out.println("1. Tambah Kendaraan Baru");
        System.out.println("2. Lihat Semua Kendaraan");
        System.out.println("3. Hapus Kendaraan");
        System.out.println("0. Logout");
        System.out.print("Pilihan Anda > ");
        String pilihan = scanner.nextLine();

        switch (pilihan) {
            case "1":
                tambahKendaraanBaru(kendaraanManager);
                break;
            case "2":
                kendaraanManager.tampilkanSemuaKendaraan();
                tekanEnterUntukLanjut();
                break;
            case "3":
                hapusKendaraan(kendaraanManager);
                break;
            case "0":
                System.out.println("Anda telah berhasil logout.");
                return true;
            default:
                System.out.println("Pilihan tidak valid.");
        }
        return false;
    }

    private static void tambahKendaraanBaru(KendaraanManager kendaraanManager) {
        System.out.println("\n========================================");
        System.out.println("MENU TAMBAH KENDARAAN BARU");
        System.out.println("========================================");
        System.out.println("Pilih Jenis Kendaraan:");
        System.out.println("1. Mobil");
        System.out.println("2. Motor");
        System.out.println("0. Kembali");
        System.out.print("Pilihan Anda > ");
        String jenis = scanner.nextLine();

        if (jenis.equals("0")) {
            return;
        }

        if (!jenis.equals("1") && !jenis.equals("2")) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        System.out.print("Masukkan Plat Nomor : ");
        String platNomor = scanner.nextLine();

        if (platNomor.trim().isEmpty()) {
            System.out.println("[GAGAL] Plat nomor tidak boleh kosong.");
            return;
        }

        double hargaSewa = bacaDouble("Masukkan Harga Sewa/Hari : ");
        if (hargaSewa < 0) {
            System.out.println("[GAGAL] Harga sewa tidak valid.");
            return;
        }

        Kendaraan kendaraanBaru;

        if (jenis.equals("1")) {
            int jumlahPintu = bacaInt("Masukkan Jumlah Pintu : ");
            kendaraanBaru = new Mobil(platNomor, hargaSewa, jumlahPintu);
        } else {
            System.out.print("Masukkan Jenis Transmisi (Manual/Matic) : ");
            String transmisi = scanner.nextLine();
            kendaraanBaru = new Motor(platNomor, hargaSewa, transmisi);
        }

        kendaraanManager.tambahKendaraan(kendaraanBaru);
        tekanEnterUntukLanjut();
    }

    private static void hapusKendaraan(KendaraanManager kendaraanManager) {
        System.out.println("\n========================================");
        System.out.println("MENU HAPUS KENDARAAN");
        System.out.println("========================================");
        System.out.print("Masukkan Plat Nomor yang ingin dihapus (ketik 0 untuk kembali) : ");
        String platNomor = scanner.nextLine();

        if (platNomor.equals("0")) {
            return;
        }

        kendaraanManager.hapusKendaraan(platNomor);
        tekanEnterUntukLanjut();
    }

    // ================= MENU STAFF =================
    private static boolean menuStaff(LayananService layananService,
                                      KendaraanManager kendaraanManager,
                                      TransaksiService transaksiService) {
        System.out.println("\n========================================");
        System.out.println("DASHBOARD - STAFF");
        System.out.println("========================================");
        System.out.println("1. Daftar Pelanggan Baru");
        System.out.println("2. Cari Data Pelanggan");
        System.out.println("3. Cek Kendaraan Tersedia");
        System.out.println("4. Proses Peminjaman (Sewa)");
        System.out.println("5. Proses Pengembalian");
        System.out.println("0. Logout");
        System.out.print("Pilihan Anda > ");
        String pilihan = scanner.nextLine();

        switch (pilihan) {
            case "1":
                daftarPelangganBaru(layananService);
                break;
            case "2":
                cariPelanggan(layananService);
                break;
            case "3":
                kendaraanManager.tampilkanKendaraanTersedia();
                tekanEnterUntukLanjut();
                break;
            case "4":
                prosesPeminjaman(layananService, kendaraanManager, transaksiService);
                break;
            case "5":
                prosesPengembalian(kendaraanManager, transaksiService);
                break;
            case "0":
                System.out.println("Anda telah berhasil logout.");
                return true;
            default:
                System.out.println("Pilihan tidak valid.");
        }
        return false;
    }

    private static void daftarPelangganBaru(LayananService layananService) {
        System.out.println("\n========================================");
        System.out.println("MENU PENDAFTARAN PELANGGAN");
        System.out.println("========================================");
        System.out.print("Masukkan Nomor KTP (ketik 0 untuk kembali) : ");
        String ktp = scanner.nextLine();

        if (ktp.equals("0")) {
            return;
        }

        System.out.print("Masukkan Nama Lengkap : ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan No Telepon : ");
        String telepon = scanner.nextLine();

        layananService.daftarkanPelanggan(ktp, nama, telepon);
        tekanEnterUntukLanjut();
    }

    private static void cariPelanggan(LayananService layananService) {
        System.out.println("\n========================================");
        System.out.println("MENU PENCARIAN PELANGGAN");
        System.out.println("========================================");
        System.out.print("Masukkan Nomor KTP (ketik 0 untuk kembali) : ");
        String ktp = scanner.nextLine();

        if (ktp.equals("0")) {
            return;
        }

        Pelanggan pelanggan = layananService.cariPelangganByKtp(ktp);

        if (pelanggan == null) {
            System.out.println("Data pelanggan tidak ditemukan.");
        } else {
            System.out.println("[DATA DITEMUKAN]");
            System.out.println("Nama Lengkap : " + pelanggan.getNama());
            System.out.println("Nomor KTP    : " + pelanggan.getNomorKtp());
            System.out.println("No Telepon   : " + pelanggan.getNoTelepon());
        }
        tekanEnterUntukLanjut();
    }

    private static void prosesPeminjaman(LayananService layananService,
                                          KendaraanManager kendaraanManager,
                                          TransaksiService transaksiService) {
        System.out.println("\n========================================");
        System.out.println("MENU PEMINJAMAN KENDARAAN");
        System.out.println("========================================");
        System.out.print("Masukkan Nomor KTP Pelanggan (ketik 0 untuk kembali) : ");
        String ktp = scanner.nextLine();

        if (ktp.equals("0")) {
            return;
        }

        Pelanggan pelanggan = layananService.cariPelangganByKtp(ktp);
        if (pelanggan == null) {
            System.out.println("[GAGAL] Pelanggan dengan KTP tersebut tidak ditemukan.");
            tekanEnterUntukLanjut();
            return;
        }

        System.out.print("Masukkan Plat Nomor Kendaraan : ");
        String platNomor = scanner.nextLine();

        Kendaraan kendaraan = kendaraanManager.cariKendaraan(platNomor);
        if (kendaraan == null) {
            System.out.println("[GAGAL] Kendaraan dengan plat nomor tersebut tidak ditemukan.");
            tekanEnterUntukLanjut();
            return;
        }

        int durasi = bacaInt("Rencana Durasi Sewa (Hari) : ");
        if (durasi <= 0) {
            System.out.println("[GAGAL] Durasi sewa harus lebih dari 0 hari.");
            tekanEnterUntukLanjut();
            return;
        }

        System.out.println("Memproses transaksi...");

        LocalDate tanggalPinjam = LocalDate.now();
        LocalDate tanggalRencanaKembali = tanggalPinjam.plusDays(durasi);

        Transaksi transaksi = transaksiService.prosesPeminjaman(pelanggan, kendaraan, tanggalPinjam, tanggalRencanaKembali);

        if (transaksi != null) {
            // Simpan status kendaraan (SEDANG_DISEWA) ke file
            kendaraanManager.simpanData();
            transaksiService.cetakStruk(transaksi);
        }

        tekanEnterUntukLanjut();
    }

    private static void prosesPengembalian(KendaraanManager kendaraanManager,
                                            TransaksiService transaksiService) {
        System.out.println("\n========================================");
        System.out.println("MENU PENGEMBALIAN KENDARAAN");
        System.out.println("========================================");
        System.out.print("Masukkan ID Transaksi (ketik 0 untuk kembali) : ");
        String idTransaksi = scanner.nextLine();

        if (idTransaksi.equals("0")) {
            return;
        }

        Transaksi transaksi = transaksiService.cariTransaksi(idTransaksi);

        if (transaksi == null) {
            System.out.println("[GAGAL] Transaksi tidak ditemukan.");
            tekanEnterUntukLanjut();
            return;
        }

        if (transaksi.getStatusTransaksi().equalsIgnoreCase("SELESAI")) {
            System.out.println("[GAGAL] Transaksi ini sudah selesai sebelumnya.");
            tekanEnterUntukLanjut();
            return;
        }

        System.out.println("Kendaraan ditemukan " + transaksi.getKendaraan().getTipeKendaraan()
                + " (" + transaksi.getKendaraan().getPlatNomor() + ").");

        int hariTerlambat = bacaInt("Durasi Keterlambatan (Hari, isi 0 jika tepat waktu) : ");
        if (hariTerlambat < 0) {
            System.out.println("[GAGAL] Hari keterlambatan tidak boleh negatif.");
            tekanEnterUntukLanjut();
            return;
        }

        System.out.println("Menghitung tagihan...");

        transaksiService.prosesPengembalian(idTransaksi, hariTerlambat);

        // Simpan status kendaraan (TERSEDIA / DALAM_PERAWATAN) ke file
        kendaraanManager.simpanData();

        transaksiService.cetakStruk(transaksi);
        tekanEnterUntukLanjut();
    }

    // ================= MENU OWNER =================
    private static boolean menuOwner(LaporanService laporanService, TransaksiService transaksiService) {
        System.out.println("\n========================================");
        System.out.println("DASHBOARD - OWNER");
        System.out.println("========================================");
        System.out.println("1. Lihat Laporan Pendapatan & Riwayat");
        System.out.println("0. Logout");
        System.out.print("Pilihan Anda > ");
        String pilihan = scanner.nextLine();

        switch (pilihan) {
            case "1":
                laporanService.tampilkanLaporanPendapatan(transaksiService.getDaftarTransaksi());
                tekanEnterUntukLanjut();
                break;
            case "0":
                System.out.println("Anda telah berhasil logout.");
                return true;
            default:
                System.out.println("Pilihan tidak valid.");
        }
        return false;
    }

    // ================= HELPER INPUT =================

    // Baca input integer, ulangi terus selama input bukan angka (anti-crash)
    private static int bacaInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Input harus berupa angka. Coba lagi.");
            }
        }
    }

    // Baca input double, ulangi terus selama input bukan angka (anti-crash)
    private static double bacaDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Input harus berupa angka. Coba lagi.");
            }
        }
    }

    private static void tekanEnterUntukLanjut() {
        System.out.print("\nTekan ENTER untuk kembali ke menu utama...");
        scanner.nextLine();
    }
}