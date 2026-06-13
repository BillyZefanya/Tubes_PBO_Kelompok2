package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Pelanggan;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PelangganRepository {
    private final String FILE_PATH = "data_pelanggan.json";
    private final Gson gson = new Gson();

    public List<Pelanggan> loadSemuaPelanggan() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Pelanggan>>(){}.getType();
            List<Pelanggan> pelangganList = gson.fromJson(reader, listType);
            return pelangganList != null ? pelangganList : new ArrayList<>();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error membaca file JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void savePelanggan(List<Pelanggan> pelangganList) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(pelangganList, writer);
        } catch (IOException e) {
            System.out.println("Error menyimpan file JSON: " + e.getMessage());
        }
    }
}