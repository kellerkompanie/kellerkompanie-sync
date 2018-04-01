package com.kellerkompanie.kekosync.client.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.arma.ArmALauncher;
import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.core.gsonConverter.PathConverter;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Settings {

    private static final String SEARCH_DIRECTORIES = "searchDirs";
    private static final String EXECUTABLE_LOCATION = "arma3location";
    private static final String LAUNCH_PARAMS = "launchParams";

    private static final String SETTINGS_PATH = System.getenv("APPDATA") + File.separator + "KekoSync";
    private static final String SETTINGS_FILE = SETTINGS_PATH + File.separator + "settings.json";
    private static Settings instance;
    private HashMap<String, Serializable> settings;

    private Settings() {
        File settingsFile = new File(SETTINGS_FILE);
        File settingsPath = new File(SETTINGS_PATH);

        if(!settingsPath.exists()) {
            if (!settingsPath.mkdirs()) {
                throw new IllegalStateException("unable to create settings folder: " + SETTINGS_PATH);
            }
        }

        if (!settingsFile.exists()) {
            createDefaultSettings();
            saveSettings();
        } else {
            loadSettings();
        }
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getArmAExecutable() {
        return (String) settings.get(EXECUTABLE_LOCATION);
    }

    public void setArmAExecutable(String executableLocation) {
        settings.put(EXECUTABLE_LOCATION, executableLocation);
        saveSettings();
    }

    public Set<Path> getSearchDirectories() {
        return (Set<Path>) settings.get(SEARCH_DIRECTORIES);
    }

    public void addSearchDirectory(Path searchDir) {
        getSearchDirectories().add(searchDir);
        saveSettings();
    }

    public void removeSearchDirectory(Path path) {
        getSearchDirectories().remove(path);
        saveSettings();
    }

    public List<ArmAParameter> getLaunchParams() {
        return (List<ArmAParameter>) settings.get(LAUNCH_PARAMS);
    }

    private void createDefaultSettings() {
        settings = new HashMap<>();

        HashSet<Path> searchDirectories = new HashSet<>();
        settings.put(SEARCH_DIRECTORIES, searchDirectories);

        String executableLocaton = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Arma 3\\arma3_x64.exe";
        settings.put(EXECUTABLE_LOCATION, executableLocaton);

        ArrayList<ArmAParameter> defaultParams = ArmAParameter.getDefaultParameters();
        settings.put(LAUNCH_PARAMS, defaultParams);
    }

    private void loadSettings() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(SETTINGS_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        settings = gson.fromJson(reader, settings.getClass());
    }

    private void saveSettings() {
        File settingsFile = new File(SETTINGS_FILE);
        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        try (Writer writer = new FileWriter(settingsFile)) {
            Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).setPrettyPrinting().create();
            gson.toJson(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
