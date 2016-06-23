package com.ets.astl.pfe_velo_jet.managers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ets.astl.pfe_velo_jet.entity.GlobalData;
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
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static FileManager Instance = null;
    private List<Path> data = null;
    private Gson gson;
    private File current;

    private FileManager() {}

    private FileManager(Context context) {
        //GsonBuilder gsonBuilder = new GsonBuilder(); USE THIS IF I NEED TO CONFIG THE GSON OBJECT
        gson = new Gson(); //gsonBuilder.create();

        try {
            current = new File(context.getFilesDir(), "velo-jet.json");
            JsonReader jsonReader = new JsonReader(new FileReader(current));
            Log.e("JSON", jsonReader.toString());
            data = gson.fromJson(jsonReader, new TypeToken<List<Path>>(){}.getType());
            if (data == null) {
                data = new ArrayList<Path>();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            data = new ArrayList<Path>();
        }
    }

    public static FileManager getInstance(Context context) {
        if (Instance == null) {
            Instance = new FileManager(context);
        }
        return Instance;
    }

    public int savePath(Path path) {
        data.add(path);
        try {
            /*FileWriter writer = new FileWriter(current);
            writer.write(gson.toJson(data));*/

            try (Writer writer = new FileWriter(current)) {
                gson.toJson(data, writer);
            }

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
            if (path.getName() == name) {
                return path;
            }
        }
        return null;
    }
}
