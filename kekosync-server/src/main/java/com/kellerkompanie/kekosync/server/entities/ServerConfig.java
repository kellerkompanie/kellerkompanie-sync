package com.kellerkompanie.kekosync.server.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ServerConfig {

    @Getter
    private final String baseURL;
    @Getter
    private final String infoURL;
    @Getter
    private final String databaseName;
    @Getter
    private final String databaseUser;
    @Getter
    private final String databasePassword;

    private final List<String> addonSourceFolders;

    public ServerConfig(String baseURL, String infoURL, String databaseName, String databaseUser, String databasePassword) {
        this.baseURL = baseURL;
        this.infoURL = infoURL;
        this.databaseName = databaseName;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.addonSourceFolders = new LinkedList<>();
    }

    public static ServerConfig getDefaultConfig() {
        String baseURL = "http://server.kellerkompanie.com/repository/";
        String infoURL = "http://server.kellerkompanie.com/modpack_info.json";
        String databaseName = "kekosync";
        String databaseUser = "user";
        String databasePassword = "password";
        ServerConfig defaultConfig = new ServerConfig(baseURL, infoURL, databaseName, databaseUser, databasePassword);

        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.main");
        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.event");
        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.maps");
        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.optional");
        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.vietnam");
        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.ironfront");
        defaultConfig.addAddonSourceFolder("/home/arma3server/serverfiles/mods.scifi");

        return defaultConfig;
    }

    private void addAddonSourceFolder(String addonSourceFolder) {
        if (addonSourceFolder == null || addonSourceFolder.isEmpty())
            throw new IllegalArgumentException("addon source folder cannot be null nor empty");

        Path path = Paths.get(addonSourceFolder);
        if (!Files.isReadable(path))
            throw new IllegalArgumentException("cannot access provided path: " + addonSourceFolder);

        addonSourceFolders.add(addonSourceFolder);
    }

    public List<String> getAddonSourceFolders() {
        return Collections.unmodifiableList(addonSourceFolders);
    }
}
