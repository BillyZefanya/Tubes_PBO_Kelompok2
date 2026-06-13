package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Kendaraan;
import model.Transaksi;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Class untuk menyimpan dan membaca data transaksi ke file JSON
public class JsonTransaksi {

    private static final String NAMA_FILE = "transaksi.json";

    private Gson gson;

    public JsonTransaksi() {
        // Daftarkan adapter LocalDate dan deserializer Kendaraan (abstract)
        // karena Transaksi punya field bertipe LocalDate dan Kendaraan
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Kendaraan.class, new KendaraanDeserializer())
                .create();
    }

    // Menyimpan seluruh transaksi ke file JSON
    public void simpanDataTransaksi(List<Transaksi> daftarTransaksi) {
        try (FileWriter fileWriter = new FileWriter(NAMA_FILE)) {
            gson.toJson(daftarTransaksi, fileWriter);
        } catch (Exception exception) {
            System.out.println("Gagal menyimpan data transaksi.");
            System.out.println(exception.getMessage());
        }
    }

    // Membaca seluruh transaksi dari file JSON
    public List<Transaksi> muatDataTransaksi() {
        try (FileReader fileReader = new FileReader(NAMA_FILE)) {
            Type tipeListTransaksi = new TypeToken<List<Transaksi>>() {}.getType();
            List<Transaksi> daftarTransaksi = gson.fromJson(fileReader, tipeListTransaksi);
            if (daftarTransaksi == null) {
                return new ArrayList<>();
            }
            return daftarTransaksi;
        } catch (Exception exception) {
            // Belum ada file = transaksi pertama kali, anggap kosong
            return new ArrayList<>();
        }
    }
}