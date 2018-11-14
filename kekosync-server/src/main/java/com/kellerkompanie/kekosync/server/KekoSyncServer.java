package com.kellerkompanie.kekosync.server;

import com.google.gson.GsonBuilder;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.ServerInfo;
import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import com.kellerkompanie.kekosync.server.entities.ServerRepository;
import com.kellerkompanie.kekosync.server.tasks.RebuildRepositoryTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;

/**
 * @author Schwaggot
 */
@Slf4j
public class KekoSyncServer {

    private HashMap<String, ServerRepository> serverRepositories;
    private String baseURL;
    private String infoURL;

    /**
     * @param iniFile the path to the .ini file containing the configuration.
     */
    public KekoSyncServer(String iniFile) {
        serverRepositories = new HashMap<>();

        try {
            readSettingsFromINI(iniFile);
        } catch (IOException | BackingStoreException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws ParseException {
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor();

        if (args.length > 1) {
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
     * @param iniFilePath the path to the .ini file.
     * @throws IOException           thrown if there is a problem with the provided file.
     * @throws BackingStoreException thrown if .ini file is malformed.
     */
    private void readSettingsFromINI(String iniFilePath) throws IOException, BackingStoreException {
        Ini ini = new Ini(new File(iniFilePath));
        java.util.prefs.Preferences prefs = new IniPreferences(ini);

        baseURL = prefs.node("general").get("baseURL", null);
        infoURL = prefs.node("general").get("infoURL", null);

        // read repositories
        String[] headerNames = prefs.childrenNames();
        for (String headerName : headerNames) {
            if (headerName.startsWith("repo:")) {
                String repoIdentifier = headerName.replaceFirst("repo:", "");
                String repoName = prefs.node(headerName).get("name", null);
                String repoFolder = prefs.node(headerName).get("folder", null);

                String repoURL = baseURL.endsWith("/") ? baseURL + repoIdentifier : baseURL + "/" + repoIdentifier;
                ServerRepository serverRepository = new ServerRepository(repoIdentifier, repoName, repoFolder, repoURL);
                serverRepositories.put(repoIdentifier, serverRepository);
            }
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
        ServerInfo serverInfo = new ServerInfo(baseURL, infoURL, serverRepositories.keySet());
        String serverInfoJson = new GsonBuilder().setPrettyPrinting().create().toJson(serverInfo);
        try {
            Files.write(Paths.get("").resolve(Filenames.FILENAME_SERVERINFO), serverInfoJson.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("could not write serverinfo file.", e);
        }
    }
}
