package com.kellerkompanie.kekosync.client.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.gui.LocalModGroup;
import com.kellerkompanie.kekosync.core.entities.ServerInfo;
import com.kellerkompanie.kekosync.core.gsonConverter.PathConverter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Settings {

    public static final String SERVER_URL = "server.kellerkompanie.com";
    public static final String REPOSITORY_URL = "http://" + SERVER_URL + "/repository";

    private static final File settingsPath = new File(System.getenv("APPDATA") + File.separator + "KekoSync");
    private static final File settingsFile = new File(settingsPath, File.separator + "settings.json");
    private static Settings instance;
    @Getter
    boolean windowMaximized = false;
    @Getter
    private String executableLocation;
    private HashMap<String, ArmAParameter> launchParams;
    private HashMap<String, String> modsetLocations;

    @Getter
    private double windowWidth = 800;
    @Getter
    private double windowHeight = 600;
    @Getter
    private double windowX = 0;
    @Getter
    private double windowY = 0;
    @Getter
    private ServerInfo serverInfo;

    @Getter
    private int maxSimlutaneousDownloads = 10;

    public static Settings getInstance() {
        if (instance == null) {
            if (!settingsPath.exists()) {
                if (!settingsPath.mkdirs()) {
                    throw new IllegalStateException("unable to create settings folder: " + settingsPath);
                }
            }

            if (settingsFile.exists()) {
                instance = loadSettings();
            } else {
                instance = new Settings();
                instance.createDefaultSettings();
                instance.saveSettings();
            }
        }
        return instance;
    }

    private static Settings loadSettings() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(settingsFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return gson.fromJson(reader, Settings.class);
    }

    public void setExecutableLocation(String executableLocation) {
        this.executableLocation = executableLocation;
        saveSettings();
    }

    public Map<String, ArmAParameter> getLaunchParams() {
        return Collections.unmodifiableMap(launchParams);
    }

    private void createDefaultSettings() {
        modsetLocations = new HashMap<>();
        executableLocation = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Arma 3\\arma3_x64.exe";
        launchParams = ArmAParameter.getDefaultParameters();
    }

    public void saveSettings() {
        log.debug("saving settings");

        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                log.error("ran into trouble while creating settings file", e);
                System.exit(1);
            }
        }

        try (Writer writer = new FileWriter(settingsFile)) {
            Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).setPrettyPrinting().create();
            gson.toJson(this, writer);
        } catch (IOException e) {
            log.error("ran into trouble while writing settings to file", e);
            System.exit(1);
        }
    }

    public void updateWindowSize(double width, double height) {
        this.windowWidth = width;
        this.windowHeight = height;
    }

    public void updateWindowPosition(double x, double y) {
        this.windowX = x;
        this.windowY = y;
    }

    public void updateWindowMaximized(boolean maximized) {
        this.windowMaximized = maximized;
    }

    public void updateLaunchParam(String key, boolean selected) {
        if (!launchParams.containsKey(key)) {
            ArmAParameter defaultParam = ArmAParameter.getDefaultParameters().get(key);
            if (defaultParam != null) {
                launchParams.put(key, defaultParam);
            } else {
                throw new IllegalArgumentException("this launch parameter key does not exist: " + key);
            }
        }

        ArmAParameter param = launchParams.get(key);
        param.setEnabled(selected);
        saveSettings();
    }

    public void updateLaunchParam(String key, String value) {
        if (!launchParams.containsKey(key)) {
            ArmAParameter defaultParam = ArmAParameter.getDefaultParameters().get(key);
            if (defaultParam != null) {
                launchParams.put(key, defaultParam);
            } else {
                throw new IllegalArgumentException("this launch parameter key does not exist: " + key);
            }
        }

        ArmAParameter param = launchParams.get(key);
        param.setValue(value);
        saveSettings();
    }

    public void updateModsetLocation(LocalModGroup localModGroup) {
        modsetLocations.put(localModGroup.getUuid().toString(), localModGroup.getLocation().toAbsolutePath().toString());
        saveSettings();
    }

    public Path getModsetLocation(LocalModGroup localModGroup) {
        String modGroupUUID = localModGroup.getUuid().toString();
        String pathStr = modsetLocations.get(modGroupUUID);
        if (pathStr == null)
            return null;

        return Paths.get(pathStr);
    }

    public Collection<Path> getSearchDirectories() {
        LinkedList<Path> locations = new LinkedList<>();
        for(String pathStr : modsetLocations.values()) {
            locations.add(Paths.get(pathStr));
        }
        return locations;
    }

    public void setMaxSimlutaneousDownloads(int maxSimlutaneousDownloads) {
        this.maxSimlutaneousDownloads = maxSimlutaneousDownloads;
        saveSettings();
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        saveSettings();
    }
}
