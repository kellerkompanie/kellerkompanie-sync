package com.kellerkompanie.kekosync.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Settings {

    private static final String ARMA3_EXECUTABLE_LOCATION = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Arma 3\\arma3_x64.exe";
    private static final HashSet<Path> searchDirectories = new HashSet<Path>();

    static {
        searchDirectories.add(Paths.get("E:\\kekosync-demo-repository"));
    }

    public static String getArma3Executable() {
        return ARMA3_EXECUTABLE_LOCATION;
    }

    public static Set<Path> getSearchDirectories() {
        return searchDirectories;
    }

    public static void addSearchDirectory(Path searchDir) {
        searchDirectories.add(searchDir);
    }

    public static void removeSearchDirectory(Path path) {
        searchDirectories.remove(path);
    }

}
