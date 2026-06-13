package persistence;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter penulisFileJson, LocalDate nilaiTanggal) throws IOException {
        if (nilaiTanggal == null) {
            penulisFileJson.nullValue();
        } else {
            penulisFileJson.value(nilaiTanggal.toString());
        }
    }

    @Override
    public LocalDate read(JsonReader pembacaFileJson) throws IOException {
        // Cek dulu apakah nilainya null, kalau ya jangan panggil nextString()
        if (pembacaFileJson.peek() == JsonToken.NULL) {
            pembacaFileJson.nextNull();
            return null;
        }
        return LocalDate.parse(pembacaFileJson.nextString());
    }
}