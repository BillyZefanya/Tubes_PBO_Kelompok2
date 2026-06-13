import model.User;
import service.UserService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        User loggedInUser = null;

        System.out.println("=== Selamat Datang di Sistem Rental Kendaraan ===");

        while (true) {
            // Jika akun terkunci karena gagal 3x, keluar aplikasi
            if (userService.isTerkunci()) {
                System.out.println("Aplikasi dihentikan karena percobaan login melebihi batas.");
                break;
            }

            // Jika belum login, tampilkan layar login
            if (loggedInUser == null) {
                System.out.println("\n--- SILAKAN LOGIN ---");
                System.out.print("Username: ");
                String username = scanner.nextLine();
                
                System.out.print("Password: ");
                String password = scanner.nextLine();

                loggedInUser = userService.login(username, password);

                if (loggedInUser != null) {
                    System.out.println("Login berhasil! Selamat datang, " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
                }
            } else {
                // Jika sudah login, tampilkan menu sesuai Role
                System.out.println("\n=== MAIN MENU ===");
                String role = loggedInUser.getRole();

                if (role.equalsIgnoreCase("Admin")) {
                    tampilkanMenuAdmin();
                } else if (role.equalsIgnoreCase("Staff")) {
                    tampilkanMenuStaff();
                } else if (role.equalsIgnoreCase("Owner")) {
                    tampilkanMenuOwner();
                } else {
                    System.out.println("Role tidak dikenali.");
                }

                System.out.println("0. Logout");
                System.out.print("Pilih menu: ");
                String pilihan = scanner.nextLine();

                if (pilihan.equals("0")) {
                    loggedInUser = null; // Proses Logout
                    System.out.println("Anda telah berhasil logout.");
                } else {
                    System.out.println("Pilihan tidak valid atau fitur belum diimplementasikan.");
                }
            }
        }

        scanner.close();
    }

    private static void tampilkanMenuAdmin() {
        System.out.println("--- Menu Admin ---");
        System.out.println("1. Tambah Kendaraan");
        System.out.println("2. Lihat Daftar Kendaraan");
        System.out.println("3. Hapus Kendaraan");
    }

    private static void tampilkanMenuStaff() {
        System.out.println("--- Menu Staff ---");
        System.out.println("1. Registrasi Pelanggan");
        System.out.println("2. Cari Pelanggan");
        System.out.println("3. Proses Peminjaman");
        System.out.println("4. Proses Pengembalian");
    }

    private static void tampilkanMenuOwner() {
        System.out.println("--- Menu Owner ---");
        System.out.println("1. Laporan Riwayat Transaksi");
        System.out.println("2. Total Pendapatan");
    }
}
