package service;

import model.Kendaraan;
import model.StatusKendaraan;
import persistence.KendaraanDeserializer;
import persistence.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KendaraanManager {
    private final String lokasiFileDatabase = "kendaraan.json";
    private List<Kendaraan> daftarKendaraan;
    private Gson gsonAlatBantuJson;

    public KendaraanManager() {
        this.daftarKendaraan = new ArrayList<>();
        inisialisasiGson();
        muatDataKendaraan();
    }

    private void inisialisasiGson() {
        this.gsonAlatBantuJson = new GsonBuilder()
                .registerTypeAdapter(Kendaraan.class, new KendaraanDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    private void muatDataKendaraan() {
        try (Reader pembacaFileDatabase = new FileReader(lokasiFileDatabase)) {
            Type tipeDaftarKendaraan = new TypeToken<ArrayList<Kendaraan>>() {}.getType();
            List<Kendaraan> dataDimuat = gsonAlatBantuJson.fromJson(pembacaFileDatabase, tipeDaftarKendaraan);
            if (dataDimuat != null) {
                this.daftarKendaraan = dataDimuat;
                perbaruiStatusMaintenanceSemuaKendaraan();
            }
        } catch (IOException pengecualianFile) {
            this.daftarKendaraan = new ArrayList<>();
        }
    }

    private void simpanDataKendaraan() {
        try (Writer penulisFileDatabase = new FileWriter(lokasiFileDatabase)) {
            gsonAlatBantuJson.toJson(this.daftarKendaraan, penulisFileDatabase);
        } catch (IOException pengecualianFile) {
            System.out.println("Gagal menyimpan data kendaraan.");
        }
    }

    // Wrapper publik agar service lain (TransaksiService via Main) bisa memicu penyimpanan
    // setelah mengubah status kendaraan dari luar class ini
    public void simpanData() {
        simpanDataKendaraan();
    }

    private void perbaruiStatusMaintenanceSemuaKendaraan() {
        for (Kendaraan kendaraanItem : daftarKendaraan) {
            kendaraanItem.perbaruiStatusPerawatan();
        }
        simpanDataKendaraan();
    }

    public boolean validasiPlatNomorUnik(String platNomor) {
        for (Kendaraan kendaraanItem : daftarKendaraan) {
            if (kendaraanItem.getPlatNomor().equalsIgnoreCase(platNomor)) {
                return false;
            }
        }
        return true;
    }

    public void tambahKendaraan(Kendaraan kendaraanBaru) {
        if (validasiPlatNomorUnik(kendaraanBaru.getPlatNomor())) {
            daftarKendaraan.add(kendaraanBaru);
            simpanDataKendaraan();
            System.out.println("[SUKSES] Kendaraan dengan plat " + kendaraanBaru.getPlatNomor()
                    + " berhasil ditambahkan ke dalam sistem dengan status TERSEDIA.");
        } else {
            System.out.println("[GAGAL] Plat Nomor sudah terdaftar!");
        }
    }

    public void tampilkanSemuaKendaraan() {
        if (daftarKendaraan.isEmpty()) {
            System.out.println("Data kendaraan masih kosong.");
            return;
        }

        System.out.println("DAFTAR SELURUH KENDARAAN");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-10s | %-15s | %-20s | %-15s\n", "Plat Nomor", "Jenis", "Harga/Hari", "Info Tambahan", "Status");
        System.out.println("--------------------------------------------------------------------------------");

        for (Kendaraan kendaraanItem : daftarKendaraan) {
            kendaraanItem.perbaruiStatusPerawatan();
            System.out.printf("%-15s | %-10s | %-15.0f | %-20s | %-15s\n",
                    kendaraanItem.getPlatNomor(),
                    kendaraanItem.getTipeKendaraan(),
                    kendaraanItem.getHargaSewaPerHari(),
                    kendaraanItem.dapatkanAtributKhusus(),
                    kendaraanItem.getStatusKendaraan().toString());
        }
        simpanDataKendaraan();
    }

    // Khusus menampilkan kendaraan dengan status TERSEDIA (Epic 4 - Menu 3 Staff)
    public void tampilkanKendaraanTersedia() {
        boolean adaTersedia = false;

        System.out.println("DAFTAR KENDARAAN TERSEDIA");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-10s | %-15s | %-20s | %-15s\n", "Plat Nomor", "Jenis", "Harga/Hari", "Info Tambahan", "Status");
        System.out.println("--------------------------------------------------------------------------------");

        for (Kendaraan kendaraanItem : daftarKendaraan) {
            kendaraanItem.perbaruiStatusPerawatan();
            if (kendaraanItem.getStatusKendaraan() == StatusKendaraan.TERSEDIA) {
                adaTersedia = true;
                System.out.printf("%-15s | %-10s | %-15.0f | %-20s | %-15s\n",
                        kendaraanItem.getPlatNomor(),
                        kendaraanItem.getTipeKendaraan(),
                        kendaraanItem.getHargaSewaPerHari(),
                        kendaraanItem.dapatkanAtributKhusus(),
                        kendaraanItem.getStatusKendaraan().toString());
            }
        }

        if (!adaTersedia) {
            System.out.println("Tidak ada kendaraan dengan status TERSEDIA saat ini.");
        }

        simpanDataKendaraan();
    }

    public Kendaraan cariKendaraan(String platNomor) {
        for (Kendaraan kendaraanItem : daftarKendaraan) {
            if (kendaraanItem.getPlatNomor().equalsIgnoreCase(platNomor)) {
                return kendaraanItem;
            }
        }
        return null;
    }

    public void hapusKendaraan(String platNomor) {
        Kendaraan kendaraanTarget = null;
        for (Kendaraan kendaraanItem : daftarKendaraan) {
            if (kendaraanItem.getPlatNomor().equalsIgnoreCase(platNomor)) {
                kendaraanTarget = kendaraanItem;
                break;
            }
        }

        if (kendaraanTarget == null) {
            System.out.println("[GAGAL] Kendaraan dengan plat nomor tersebut tidak ditemukan.");
            return;
        }

        if (kendaraanTarget.getStatusKendaraan() == StatusKendaraan.SEDANG_DISEWA) {
            System.out.println("[GAGAL] Kendaraan masih berstatus SEDANG DISEWA, data tidak dapat dihapus!");
        } else {
            daftarKendaraan.remove(kendaraanTarget);
            simpanDataKendaraan();
            System.out.println("[SUKSES] Kendaraan " + platNomor + " berhasil dihapus dari sistem.");
        }
    }
}