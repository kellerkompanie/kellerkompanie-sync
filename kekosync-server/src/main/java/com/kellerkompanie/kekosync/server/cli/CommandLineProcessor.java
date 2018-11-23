package com.kellerkompanie.kekosync.server.cli;

import com.kellerkompanie.kekosync.server.KekoSyncServer;
import org.apache.commons.cli.*;

/**
 * @author Schwaggot
 */
public class CommandLineProcessor {

    private static final String BUILD = "build";
    private static final String BUILD_ALL = "buildall";
    private static final String LIST = "list";
    private static final String HELP = "help";
    private static final String CONFIG = "config";

    public void process(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(CONFIG, true, "specify settings .json filepath to be loaded");
        options.addOption(BUILD, true, "build repository");
        options.addOption(BUILD_ALL, false, "build all repositories");
        options.addOption(LIST, false, "list repositories");
        options.addOption(HELP, false, "list available commands");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = parser.parse(options, args);
        KekoSyncServer kekoSyncServer = null;

        String jsonFile = "kekosync_config.json";
        if(cmd.hasOption(CONFIG)) {
            jsonFile = cmd.getOptionValue(CONFIG);

        }
        kekoSyncServer = new KekoSyncServer(jsonFile);

        if (cmd.hasOption(BUILD)) {
            String repositoryName = cmd.getOptionValue(BUILD);
            kekoSyncServer.buildRepository(repositoryName, true);
        } else if (cmd.hasOption(BUILD_ALL)) {
            kekoSyncServer.buildAllRepositories();
        } else if (cmd.hasOption(LIST)) {
            kekoSyncServer.printServerRepositories();
        } else if (cmd.hasOption(HELP)) {
            formatter.printHelp("utility-name", options);
            System.exit(0);
        } else {
            System.out.println("error: specify additional parameters");
            formatter.printHelp("utility-name", options);
            System.exit(0);
        }
    }

}
