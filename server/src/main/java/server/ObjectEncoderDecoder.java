package server;

import com.google.gson.Gson;

/**
 * Encode and decode objects using Gson
 */
public class ObjectEncoderDecoder {
    public <T> Object decode(String json, Class <T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public <T> String encode(T object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
