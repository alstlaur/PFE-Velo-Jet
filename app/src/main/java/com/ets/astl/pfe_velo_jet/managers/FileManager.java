package com.ets.astl.pfe_velo_jet.managers;

import android.os.Environment;
import android.util.Log;

import com.ets.astl.pfe_velo_jet.entity.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileManager {

    private static FileManager Instance = null;
    private List<Path> data = null;
    private Gson gson;
    private File current;

    private FileManager() {
        //GsonBuilder gsonBuilder = new GsonBuilder(); USE THIS IF I NEED TO CONFIG THE GSON OBJECT
        gson = new Gson(); //gsonBuilder.create();

        if (isExternalStorageReadable()) {
            try {
                current = getPathStorageDir("user-paths");
                JsonReader jsonReader = new JsonReader(new FileReader(current));
                data = gson.fromJson(jsonReader, new TypeToken<List<Path>>(){}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static FileManager getInstance() {
        if (Instance == null) {
            Instance = new FileManager();
        }
        return Instance;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPathStorageDir(String pathName) {
        // Get the directory for the user's public documents directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), pathName);
        if (!file.mkdirs()) {
            Log.e("Velo-Jet", "Directory not created");
        }
        return file;
    }

    public void savePath(Path path) {
        data.add(path);
        if (isExternalStorageWritable()) {
            try {
                FileWriter writer = new FileWriter(current);
                writer.write(gson.toJson(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Path> getPaths() {
        return data;
    }

    public Path getPath(String name) {
        for (Path path: data) {
            if (path.getName() == name) {
                return path;
            }
        }
        return null;
    }
}
