package com.kellerkompanie.kekosync.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.ServerInfo;
import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import com.kellerkompanie.kekosync.server.entities.ServerConfig;
import com.kellerkompanie.kekosync.server.helper.DatabaseHelper;
import com.kellerkompanie.kekosync.server.tasks.RebuildAddonSourceFolderTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Locale;

/**
 * @author Schwaggot
 */
@Slf4j
public class KekoSyncServer {

    private ServerConfig serverConfig;

    /**
     * @param jsonSettingsFile the path to the .json file containing the configuration.
     */
    public KekoSyncServer(String jsonSettingsFile) {
        try {
            readSettingsFromJson(jsonSettingsFile);
            DatabaseHelper.setup(serverConfig);
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws ParseException {
        Locale.setDefault(Locale.ENGLISH);

        CommandLineProcessor commandLineProcessor = new CommandLineProcessor();

        if (args.length > 0) {
            // arguments were provided on the command line, parse 'em
            commandLineProcessor.process(args);
        } else {
            // since no commands were provided just print the help instructions
            System.out.println("no arguments provided");
            args = new String[]{"help"};
            commandLineProcessor.process(args);
        }
    }

    /**
     * Builds the specified repository.
     * If updateServerInfo is true the server info file will be updated afterwards.
     *
     * @param addonSourceFolder the path of the folder to be build.
     */
    public void buildAddonSourceFolder(String addonSourceFolder) {
        RebuildAddonSourceFolderTask rrTask = new RebuildAddonSourceFolderTask(addonSourceFolder);
        boolean success = rrTask.execute();

        if (success)
            log.info("successfully built addon source folder {}", addonSourceFolder);
        else
            log.info("error building addon source folder {}", addonSourceFolder);
    }

    /**
     * Builds all repositories and updates server info file afterwards.
     */
    public void buildAllAddonSourceFolders() {
        for (String addonSourceFolder : serverConfig.getAddonSourceFolders()) {
            buildAddonSourceFolder(addonSourceFolder);
        }

        updateServerInfo();
    }

    /**
     * Opens the provided .json file and loads contained configuration settings.
     *
     * @param jsonFilePath the path to the .json file.
     * @throws FileNotFoundException thrown if there is a problem with the provided file.
     */
    private void readSettingsFromJson(String jsonFilePath) throws FileNotFoundException {
        File jsonFile = new File(jsonFilePath);
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        if (jsonFile.exists()) {
            // try to read the settings from provided file
            FileReader fileReader = new FileReader(jsonFile);
            serverConfig = gson.fromJson(fileReader, ServerConfig.class);
            log.info("reading config from file {}", jsonFile.getAbsoluteFile());
        } else {
            // file was not found, fall back to default config
            log.warn("settings file {} not found, falling back to default config", jsonFile.getAbsoluteFile());
            serverConfig = ServerConfig.getDefaultConfig();
            String jsonStr = gson.toJson(serverConfig);

            try (PrintWriter out = new PrintWriter(jsonFile)) {
                out.print(jsonStr);
            }
        }
    }

    /**
     * Prints a list of all current repositories line-wise into the console, e.g., for use in list console command.
     */
    public void printAddonSourceFolders() {
        for (String addonSourceDirectory : serverConfig.getAddonSourceFolders()) {
            System.out.println(addonSourceDirectory);
        }
    }

    /**
     * Writes the current state into the .serverinfo file, including the baseURL, infoURL and all repositories.
     */
    private void updateServerInfo() {
        // TODO update here information about addons and packs
        ServerInfo serverInfo = new ServerInfo(serverConfig.getBaseURL(), serverConfig.getInfoURL());
        String serverInfoJson = new GsonBuilder().setPrettyPrinting().create().toJson(serverInfo);
        try {
            Files.writeString(Paths.get("").resolve(Filenames.FILENAME_SERVERINFO), serverInfoJson);
        } catch (IOException e) {
            log.error("could not write serverinfo file.", e);
        }
    }
}
