package com.kellerkompanie.kekosync.server;

import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import com.kellerkompanie.kekosync.server.entities.ServerRepository;
import com.kellerkompanie.kekosync.server.tasks.RebuildRepositoryTask;
import org.apache.commons.cli.ParseException;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;

/**
 * @author Schwaggot
 */
public class KekoSyncServer {

    private static final String INI_FILE = "kekosync.ini";
    private HashMap<String, ServerRepository> serverRepositories;

    private KekoSyncServer() {
        serverRepositories = new HashMap<>();

        try {
            readSettingsFromINI();
        } catch (IOException | BackingStoreException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws ParseException {
        KekoSyncServer kekoSyncServer = new KekoSyncServer();
        kekoSyncServer.printServerRepositories();
        kekoSyncServer.buildRepository("kellerkompanie-testing");

        if (args.length > 1) {
            CommandLineProcessor commandLineProcessor = new CommandLineProcessor();
            commandLineProcessor.process(args);
        } else {
            /*
            String directory = "E:\\kekosync-demo-repository";
            ZsyncGenerator.cleanDirectory(directory);
            ZsyncGenerator.processDirectory(directory);
            */
            //RebuildRepositoryTask rrTask = new RebuildRepositoryTask("C:\\wamp64\\www\\repo");
            //rrTask.execute();
        }
    }

    private void buildRepository(String repositoryIdentifier) {
        ServerRepository serverRepository = serverRepositories.get(repositoryIdentifier);
        buildRepository(serverRepository);
    }

    private void buildRepository(ServerRepository serverRepository) {
        RebuildRepositoryTask rrTask = new RebuildRepositoryTask(serverRepository);
        rrTask.execute();
    }

    private void buildAllRepositories() {
        for (ServerRepository serverRepository : serverRepositories.values()) {
            buildRepository(serverRepository);
        }
    }

    private void readSettingsFromINI() throws IOException, BackingStoreException {
        Ini ini = new Ini(new File(INI_FILE));
        java.util.prefs.Preferences prefs = new IniPreferences(ini);

        String baseURL = prefs.node("general").get("baseURL", null);

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

    private void printServerRepositories() {
        for (ServerRepository serverRepository : serverRepositories.values()) {
            System.out.println(serverRepository);
        }
    }
}
