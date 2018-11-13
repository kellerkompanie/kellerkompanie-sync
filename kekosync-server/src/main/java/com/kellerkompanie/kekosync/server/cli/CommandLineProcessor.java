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
    private static final String INI = "ini";

    public void process(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(INI, true, "specify settings .ini filepath to be loaded");
        options.addOption(BUILD, true, "build repository");
        options.addOption(BUILD_ALL, false, "build all repositories");
        options.addOption(LIST, false, "list repositories");
        options.addOption(HELP, false, "list available commands");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = parser.parse(options, args);
        KekoSyncServer kekoSyncServer = null;

        if(cmd.hasOption(INI)) {
            String iniFile = cmd.getOptionValue(INI);
            kekoSyncServer = new KekoSyncServer(iniFile);
        } else {
            System.out.println("error: you must specify a .ini to load");
            formatter.printHelp("ini", options);
            System.exit(1);
        }

        if (cmd.hasOption(BUILD)) {
            String repositoryName = cmd.getOptionValue(BUILD);
            kekoSyncServer.buildRepository(repositoryName);
            kekoSyncServer.updateServerInfo();
        } else if (cmd.hasOption(BUILD_ALL)) {
            kekoSyncServer.buildAllRepositories();
            kekoSyncServer.updateServerInfo();
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
