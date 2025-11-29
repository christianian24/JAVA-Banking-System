package com.aureobank.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonStore {
    private static final Logger logger = LoggerFactory.getLogger(JsonStore.class);
    private final Path baseDir;
    private final Gson gson;

    public JsonStore(Path baseDir) {
        this.baseDir = baseDir;
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            logger.error("Failed to create base directory: {}", baseDir, e);
            // Depending on requirements, we might want to throw a runtime exception here
            // to fail fast if the storage directory is not available.
        }

        // A single adapter class that handles both serialization and deserialization.
        class JavaTimeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
            @Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (typeOfT == Instant.class) return (T) Instant.parse(json.getAsString());
                if (typeOfT == LocalDate.class) return (T) LocalDate.parse(json.getAsString());
                if (typeOfT == LocalDateTime.class) return (T) LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return null;
            }
            @Override public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
                if (src instanceof LocalDateTime) {
                    return new JsonPrimitive(((LocalDateTime) src).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } else {
                    return new JsonPrimitive(src.toString());
                }
            }
        }

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new JavaTimeAdapter<Instant>())
                .registerTypeAdapter(LocalDate.class, new JavaTimeAdapter<LocalDate>())
                .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapter<LocalDateTime>())
                .setPrettyPrinting()
                .create();
    }

    private Path path(String name) { return baseDir.resolve(name); }

    public <T> Map<String, T> readMap(String name, Class<T> clazz) {
        try {
            Path p = path(name);
            if (!Files.exists(p)) return new HashMap<>();
            try (Reader r = Files.newBufferedReader(p)) {
                Type type = TypeToken.getParameterized(Map.class, String.class, clazz).getType();
                Map<String, T> map = gson.fromJson(r, type);
                return map != null ? map : new HashMap<>();
            }
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            logger.error("Failed to read map from file: {}", name, e);
            return new HashMap<>(); // Return empty map on error
        }
    }

    public <T> void writeMap(String name, Map<String, T> map) {
        try (Writer w = Files.newBufferedWriter(path(name))) {
            gson.toJson(map, w);
        } catch (IOException | JsonIOException e) {
            logger.error("Failed to write map to file: {}", name, e);
        }
    }

    public <T> List<T> readList(String name, Class<T> clazz) {
        try {
            Path p = path(name);
            if (!Files.exists(p)) return new ArrayList<>();
            try (Reader r = Files.newBufferedReader(p)) {
                Type type = TypeToken.getParameterized(List.class, clazz).getType();
                List<T> list = gson.fromJson(r, type);
                return list != null ? list : new ArrayList<>();
            }
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            logger.error("Failed to read list from file: {}", name, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }

    public <T> void writeList(String name, List<T> list) {
        try (Writer w = Files.newBufferedWriter(path(name))) {
            gson.toJson(list, w);
        } catch (IOException | JsonIOException e) {
            logger.error("Failed to write list to file: {}", name, e);
        }
    }

    public <T> T readObject(String name, Class<T> clazz, T defaultValue) {
        try {
            Path p = path(name);
            if (!Files.exists(p)) return defaultValue;
            try (Reader r = Files.newBufferedReader(p)) {
                T obj = gson.fromJson(r, clazz);
                return obj != null ? obj : defaultValue;
            }
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            logger.error("Failed to read object from file: {}", name, e);
            return defaultValue; // Return default value on error
        }
    }

    public <T> void writeObject(String name, T obj) {
        try (Writer w = Files.newBufferedWriter(path(name))) { gson.toJson(obj, w); }
        catch (IOException | JsonIOException e) {
            logger.error("Failed to write object to file: {}", name, e);
        }
    }
}
