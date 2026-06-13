package persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.User;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUser {
    private static final String NAMA_FILE = "users.json";
    private Gson gson;

    public JsonUser() {
        gson = new Gson();
    }

    // Membaca seluruh data user dari file JSON
    public List<User> muatDataUser() {
        try (FileReader fileReader = new FileReader(NAMA_FILE)) {
            Type tipeListUser = new TypeToken<List<User>>() {}.getType();
            List<User> daftarUser = gson.fromJson(fileReader, tipeListUser);

            if (daftarUser == null) {
                return new ArrayList<>();
            }
            return daftarUser;

        } catch (Exception exception) {
            System.out.println("Gagal memuat data user (Pastikan file users.json ada). Error: " + exception.getMessage());
            return new ArrayList<>();
        }
    }
}
