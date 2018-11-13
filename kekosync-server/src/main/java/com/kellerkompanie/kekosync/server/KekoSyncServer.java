package com.kellerkompanie.kekosync.server;

import com.kellerkompanie.kekosync.server.cli.CommandLineProcessor;
import com.kellerkompanie.kekosync.server.entities.ServerRepository;
import com.kellerkompanie.kekosync.server.tasks.RebuildRepositoryTask;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class KekoSyncServer {

    private HashMap<String, ServerRepository> serverRepositories;

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
            commandLineProcessor.process(args);
        } else {
            args = new String[] {"help"};
            commandLineProcessor.process(args);
            /*
            String directory = "E:\\kekosync-demo-repository";
            ZsyncGenerator.cleanDirectory(directory);
            ZsyncGenerator.processDirectory(directory);
            */
            //RebuildRepositoryTask rrTask = new RebuildRepositoryTask("C:\\wamp64\\www\\repo");
            //rrTask.execute();
        }
    }

    public void buildRepository(String repositoryIdentifier) {
        ServerRepository serverRepository = serverRepositories.get(repositoryIdentifier);
        buildRepository(serverRepository);
    }

    private void buildRepository(ServerRepository serverRepository) {
        RebuildRepositoryTask rrTask = new RebuildRepositoryTask(serverRepository);
        boolean success = rrTask.execute();

        if(success)
            log.info("successfully built repository {}", serverRepository.getIdentifier());
        else
            log.info("error building repository {}", serverRepository.getIdentifier());
    }

    public void buildAllRepositories() {
        for (ServerRepository serverRepository : serverRepositories.values()) {
            buildRepository(serverRepository);
        }
    }

    private void readSettingsFromINI(String iniFilePath) throws IOException, BackingStoreException {
        Ini ini = new Ini(new File(iniFilePath));
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

    public void printServerRepositories() {
        for (ServerRepository serverRepository : serverRepositories.values()) {
            System.out.println(serverRepository);
        }
    }
}
