import java.util.Scanner;

public class ValidasiInputUmur {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Sistem Pendaftaran ===");
        System.out.print("Masukkan umur Anda: ");
        
        String inputUmur = scanner.nextLine();
        
        try {
            // Mengubah String menjadi integer
            int umur = Integer.parseInt(inputUmur);
            
            // Menampilkan pesan sukses
            System.out.println("Pendaftaran berhasil! Umur Anda terdaftar sebagai " + umur + " tahun.");
            
        } catch (NumberFormatException e) {
            // Menangkap exception jika input bukan berupa angka yang valid
            System.out.println("Error: Input tidak valid. Harap masukkan umur dalam format angka (misal: 20), bukan huruf seperti \"" + inputUmur + "\".");
        } finally {
            scanner.close();
        }
    }
}
