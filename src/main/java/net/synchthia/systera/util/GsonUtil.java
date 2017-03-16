package net.synchthia.systera.util;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.lang.reflect.Type;

/**
 * @author Nekoneko
 */

@UtilityClass
public class GsonUtil {

    public static <T> T load(@NonNull Gson gson, @NonNull File file, @NonNull Type type) throws IOException, RuntimeException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            if (!reader.hasNext()) {
                return null;
            }
            return gson.fromJson(reader, type);
        }
    }
    public static <T> void save(@NonNull Gson gson, @NonNull File file, @NonNull T obj, @NonNull Type type) throws IOException, RuntimeException {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            gson.toJson(obj, type, writer);
        }
    }
}
