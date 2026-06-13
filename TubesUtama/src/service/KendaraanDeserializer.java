package service;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import model.Kendaraan;
import model.Mobil;
import model.Motor;

import java.lang.reflect.Type;

public class KendaraanDeserializer implements JsonDeserializer<Kendaraan> {
    @Override
    public Kendaraan deserialize(JsonElement elemenDataJson, Type tipeDataLama, JsonDeserializationContext konteksDeserilisasi) throws JsonParseException {
        JsonObject objekDataJson = elemenDataJson.getAsJsonObject();
        String tipeKendaraan = objekDataJson.get("tipeKendaraan").getAsString();

        if (tipeKendaraan.equals("Mobil")) {
            return konteksDeserilisasi.deserialize(elemenDataJson, Mobil.class);
        } else if (tipeKendaraan.equals("Motor")) {
            return konteksDeserilisasi.deserialize(elemenDataJson, Motor.class);
        } else {
            throw new JsonParseException("Tipe kendaraan tidak dkenal");
        }
    }
}