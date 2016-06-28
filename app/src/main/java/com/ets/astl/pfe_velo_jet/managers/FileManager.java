package com.ets.astl.pfe_velo_jet.managers;

import android.content.Context;
import android.util.Log;

import com.ets.astl.pfe_velo_jet.entity.Path;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static FileManager Instance = null;
    private List<Path> data = new ArrayList<>();
    private Gson gson;
    private File current;

    private FileManager() {}

    private FileManager(Context context) {
        gson = new Gson();

        try {
            current = new File(context.getFilesDir(), "velo-jet.json");
            JsonReader jsonReader = new JsonReader(new FileReader(current));
            data = gson.fromJson(jsonReader, new TypeToken<List<Path>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static FileManager getInstance(Context context) {
        if (Instance == null) {
            Instance = new FileManager(context);
        }
        return Instance;
    }

    public int savePath(Path path) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(path);

        try {
            Writer writer = new FileWriter(current);
            gson.toJson(data, writer);
            writer.close();

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public List<Path> getPaths() {
        return data;
    }

    public Path getPath(String name) {
        for (Path path: data) {
            if (path.getName().equals(name)) {
                return path;
            }
        }
        return null;
    }
}
