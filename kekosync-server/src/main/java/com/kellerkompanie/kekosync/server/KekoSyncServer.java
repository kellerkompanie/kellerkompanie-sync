package com.kellerkompanie.kekosync.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.ServerInfo;
import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import com.kellerkompanie.kekosync.server.entities.ServerConfig;
import com.kellerkompanie.kekosync.server.entities.ServerRepository;
import com.kellerkompanie.kekosync.server.tasks.RebuildRepositoryTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author Schwaggot
 */
@Slf4j
public class KekoSyncServer {

    private HashMap<String, ServerRepository> serverRepositories;
    private ServerConfig serverConfig;

    /**
     * @param jsonSettingsFile the path to the .json file containing the configuration.
     */
    public KekoSyncServer(String jsonSettingsFile) {
        serverRepositories = new HashMap<>();

        try {
            readSettingsFromJson(jsonSettingsFile);
        } catch (FileNotFoundException e) {
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
     * Looks up the repository for givien repositoryIdentifier and builds the repository.
     * If updateServerInfo is true the server info file will be updated afterwards.
     *
     * @param repositoryIdentifier the identifier of the repository to be build.
     * @param updateServerInfo     if set to true the server info file will be updated afterwards.
     */
    public void buildRepository(String repositoryIdentifier, boolean updateServerInfo) {
        ServerRepository serverRepository = serverRepositories.get(repositoryIdentifier);
        buildRepository(serverRepository, updateServerInfo);
    }

    /**
     * Builds the specified repository.
     * If updateServerInfo is true the server info file will be updated afterwards.
     *
     * @param serverRepository the repository to be build.
     * @param updateServerInfo if set to true the server info file will be updated afterwards.
     */
    private void buildRepository(ServerRepository serverRepository, boolean updateServerInfo) {
        RebuildRepositoryTask rrTask = new RebuildRepositoryTask(serverRepository);
        boolean success = rrTask.execute();

        if (success)
            log.info("successfully built repository {}", serverRepository.getIdentifier());
        else
            log.info("error building repository {}", serverRepository.getIdentifier());

        if (updateServerInfo)
            updateServerInfo();
    }

    /**
     * Builds all repositories and updates server info file afterwards.
     */
    public void buildAllRepositories() {
        for (ServerRepository serverRepository : serverRepositories.values()) {
            buildRepository(serverRepository, false);
        }

        updateServerInfo();
    }

    /**
     * Opens the provided .ini file and loads contained configuration settings.
     *
     * @param jsonFilePath the path to the .ini file.
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

        // additionally create a map for easy access to repositories based on identifier
        for (ServerRepository serverRepository : serverConfig.getRepositories()) {
            this.serverRepositories.put(serverRepository.getIdentifier(), serverRepository);
        }
    }

    /**
     * Prints a list of all current repositories line-wise into the console, e.g., for use in list console command.
     */
    public void printServerRepositories() {
        for (ServerRepository serverRepository : serverRepositories.values()) {
            System.out.println(serverRepository);
        }
    }

    /**
     * Writes the current state into the .serverinfo file, including the baseURL, infoURL and all repositories.
     */
    private void updateServerInfo() {
        ServerInfo serverInfo = new ServerInfo(serverConfig.getBaseURL(), serverConfig.getInfoURL(), serverRepositories.keySet());
        String serverInfoJson = new GsonBuilder().setPrettyPrinting().create().toJson(serverInfo);
        try {
            Files.write(Paths.get("").resolve(Filenames.FILENAME_SERVERINFO), serverInfoJson.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("could not write serverinfo file.", e);
        }
    }
}
