package service;

import model.Pelanggan;
import repository.PelangganRepository;
import java.util.List;

public class LayananService {
    private PelangganRepository repository;

    public LayananService(PelangganRepository repository) {
        this.repository = repository;
    }

    public boolean daftarkanPelanggan(String ktp, String nama, String telepon) {
        List<Pelanggan> daftarPelanggan = repository.loadSemuaPelanggan();

        if (ktp == null || ktp.trim().isEmpty() || ktp.length() != 16 || !ktp.matches("[0-9]+")) {
            System.out.println("[ERROR] Nomor KTP tidak valid. Harus 16 digit angka.");
            return false;
        }

        for (Pelanggan p : daftarPelanggan) {
            if (p.getNomorKtp().equals(ktp)) {
                System.out.println("[GAGAL] Pelanggan dengan KTP tersebut sudah terdaftar.");
                return false;
            }
        }

        Pelanggan pelangganBaru = new Pelanggan(ktp, nama, telepon);
        daftarPelanggan.add(pelangganBaru);
        repository.savePelanggan(daftarPelanggan);
        
        System.out.println("[SUKSES] Pelanggan " + nama + " (KTP: " + ktp + ") berhasil didaftarkan.");
        return true;
    }

    public Pelanggan cariPelangganByKtp(String ktp) {
        List<Pelanggan> daftarPelanggan = repository.loadSemuaPelanggan();
        
        for (Pelanggan p : daftarPelanggan) {
            if (p.getNomorKtp().equals(ktp)) {
                return p;
            }
        }
        return null;
    }
}